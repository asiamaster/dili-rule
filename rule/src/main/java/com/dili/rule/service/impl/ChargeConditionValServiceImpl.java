package com.dili.rule.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dili.commons.glossary.YesOrNoEnum;
import com.dili.rule.domain.ChargeConditionVal;
import com.dili.rule.domain.ChargeRule;
import com.dili.rule.domain.DataSourceDefinition;
import com.dili.rule.domain.ConditionDefinition;
import com.dili.rule.domain.DataSourceColumn;
import com.dili.rule.domain.enums.MatchTypeEnum;
import com.dili.rule.domain.enums.TargetTypeEnum;
import com.dili.rule.domain.enums.ValueDataTypeEnum;
import com.dili.rule.domain.vo.ConditionDefinitionVo;
import com.dili.rule.mapper.ChargeConditionValMapper;
import com.dili.rule.service.ChargeConditionValService;
import com.dili.rule.service.ConditionDefinitionService;
import com.dili.rule.service.DataSourceColumnService;
import com.dili.rule.service.remote.RemoteDataQueryService;
import com.dili.ss.base.BaseServiceImpl;
import com.google.common.collect.Lists;

import one.util.streamex.StreamEx;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dili.rule.service.DataSourceDefinitionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <B></B>
 * <B>Copyright:本软件源代码版权归农丰时代科技有限公司及其研发团队所有,未经许可不得任意复制与传播.</B>
 * <B>农丰时代科技有限公司</B>
 *
 * @author yuehongbo
 * @date 2020/5/16 18:04
 */
@Service
public class ChargeConditionValServiceImpl extends BaseServiceImpl<ChargeConditionVal, Long> implements ChargeConditionValService {
    private static final Logger logger = LoggerFactory.getLogger(ChargeConditionValServiceImpl.class);

    public ChargeConditionValMapper getActualMapper() {
        return (ChargeConditionValMapper) getDao();
    }

    @Autowired
    private ConditionDefinitionService conditionDefinitionService;
    @Autowired
    private DataSourceDefinitionService dataSourceDefinitionService;
    @Autowired
    private DataSourceColumnService dataSourceColumnService;
    @Autowired
    private RemoteDataQueryService remoteDataQueryService;

    @Override
    public Map<String, Object> getRuleCondition(ChargeRule chargeRule, String sessionId) {
        Map<String, Object> resultMap = new HashMap<>();
        List<ChargeConditionVal> chargeConditionValList = Lists.newArrayList();
        if (Objects.nonNull(chargeRule.getId())) {
            //根据规则，查询规则条件信息
            ChargeConditionVal conditionVal = new ChargeConditionVal();
            conditionVal.setRuleId(chargeRule.getId());
            chargeConditionValList = list(conditionVal);
        }
        resultMap.put("chargeConditionVals", chargeConditionValList);
        //根据规则所在的市场、系统、业务，查询预定义的规则条件
        ConditionDefinition conditionDefinition = new ConditionDefinition();
        conditionDefinition.setMarketId(chargeRule.getMarketId());
        conditionDefinition.setBusinessType(chargeRule.getBusinessType());
        conditionDefinition.setRuleCondition(YesOrNoEnum.YES.getCode());
        conditionDefinition.setTargetType(TargetTypeEnum.CONDITION.getCode());

        conditionDefinition.setSort("dataSourceId,matchType,id");
        conditionDefinition.setOrder("desc,asc,asc");

        List<ConditionDefinition> conditionDefinitionList = conditionDefinitionService.listByExample(conditionDefinition);
        //组装已选条件与预定义的条件值
        List<ConditionDefinitionVo> conditionDefinitions = generate(chargeConditionValList, conditionDefinitionList, sessionId);
        resultMap.put("conditionDefinitions", conditionDefinitions);

        return resultMap;
    }

    @Override
    public Integer deleteByRuleId(Long ruleId) {
        if (Objects.isNull(ruleId)) {
            return 0;
        }
        ChargeConditionVal val = new ChargeConditionVal();
        val.setRuleId(ruleId);
        return getActualMapper().delete(val);
    }


    /**************** 私有方法分割线 **********************************/

    /**
     * 组装已选规则条件与预定义数据
     *
     * @param chargeConditionValList  已设置的规则条件
     * @param conditionDefinitionList 已设置的预定义条件
     */
    private List<ConditionDefinitionVo> generate(List<ChargeConditionVal> chargeConditionValList, List<ConditionDefinition> conditionDefinitionList, String sessionId) {
        Map<Long, ChargeConditionVal> conditionValMap = chargeConditionValList.stream().collect(Collectors.toMap(ChargeConditionVal::getDefinitionId, RuleConditionVal -> RuleConditionVal));
        List<ConditionDefinitionVo> voList = Lists.newArrayList();
        conditionDefinitionList.stream().unordered().forEach(conditionDefinition -> {
            ConditionDefinitionVo vo = new ConditionDefinitionVo();
            BeanUtils.copyProperties(conditionDefinition, vo);
            if (conditionValMap.containsKey(vo.getId())) {
                ChargeConditionVal conditionVal = conditionValMap.get(vo.getId());
                MatchTypeEnum matchType = MatchTypeEnum.getInitDataMaps().get(conditionDefinition.getMatchType());
                JSONArray objects = JSON.parseArray(conditionVal.getVal());
                if (matchType == MatchTypeEnum.EQUALS) {
                    vo.getValues().addAll(objects);
                } else if (matchType == MatchTypeEnum.BETWEEN) {
                    vo.getValues().addAll(objects);
                } else if (matchType == MatchTypeEnum.IN) {
                    vo.getValues().addAll(objects);
                    DataSourceDefinition dataSourceDefinition = dataSourceDefinitionService.get(conditionDefinition.getDataSourceId());
                    logger.info("objects={}", objects);
                    if (Objects.nonNull(dataSourceDefinition)) {
                        String matchColumn = conditionDefinition.getMatchColumn();
                        List<Map<String, Object>> keyTextMap = remoteDataQueryService.queryKeys(dataSourceDefinition, objects, sessionId);
//                        logger.info("keyTextMap={}",keyTextMap);
                        if (keyTextMap != null && !keyTextMap.isEmpty()) {
                            DataSourceColumn condition = new DataSourceColumn();
                            condition.setDataSourceId(conditionDefinition.getDataSourceId());
                            List<DataSourceColumn> columns = dataSourceColumnService.list(condition);
                            for (Object value : objects) {
                                List<String> displayedText = new ArrayList<>();
                                for (Map<String, Object> row : keyTextMap) {
                                    Object matchValue = row.get(matchColumn);
                                    if (String.valueOf(value).equals(String.valueOf(matchValue))) {
//                                        logger.info("value={},matchColumn={}，matchValue={}",value,matchColumn,matchValue);

                                        for (DataSourceColumn column : columns) {
                                            if (YesOrNoEnum.YES.getCode().equals(column.getDisplay())) {
                                                Object obj = row.get(column.getColumnCode());
                                                if (obj != null&&StringUtils.isNotBlank(String.valueOf(obj))) {
                                                    displayedText.add(String.valueOf(obj).trim());
                                                }

                                            }
                                        }

                                    }
                                }

                                if(displayedText.isEmpty()){
                                    vo.getTexts().add("(未知)");
                                }else{
                                    vo.getTexts().add(String.join("#", displayedText));
                                }

                            }
                        } else {
                            vo.getTexts().addAll(StreamEx.of(objects).map(o -> {
                                return "(未知)";
                            }).toList());
                        }

                    } else {
                        vo.getTexts().addAll(StreamEx.of(objects).map(o -> {
                            return "(未知)";
                        }).toList());
                    }

                }
            }
            voList.add(vo);
        });
        return voList;
    }

    @Override
    public List<ConditionDefinition> getRuleVariable(ChargeRule chargeRule, Optional<ValueDataTypeEnum> dataType) {
        Map<String, Object> resultMap = new HashMap<>();
        //根据规则所在的市场、系统、业务，查询预定义的规则条件
        ConditionDefinition conditionDefinition = new ConditionDefinition();
        conditionDefinition.setMarketId(chargeRule.getMarketId());
        conditionDefinition.setBusinessType(chargeRule.getBusinessType());
        conditionDefinition.setTargetType(TargetTypeEnum.VARIABLE.getCode());

        conditionDefinition.setSort("matchType,id");
        conditionDefinition.setOrder("asc,asc");

        dataType.ifPresent(dt -> {
            conditionDefinition.setDataType(dt.getCode());
        });

        List<ConditionDefinition> conditionDefinitionList = conditionDefinitionService.list(conditionDefinition);
        return conditionDefinitionList;
    }

}
