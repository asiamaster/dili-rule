package com.dili.rule.service.impl;

import com.dili.rule.domain.ChargeRule;
import com.dili.rule.domain.ConditionDefinition;
import com.dili.rule.mapper.ConditionDefinitionMapper;
import com.dili.rule.service.ConditionDefinitionService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import one.util.streamex.StreamEx;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2020-05-13 11:23:41.
 */
@Service
public class ConditionDefinitionServiceImpl extends BaseServiceImpl<ConditionDefinition, Long> implements ConditionDefinitionService {

    public ConditionDefinitionMapper getActualDao() {
        return (ConditionDefinitionMapper)getDao();
    }

    @Override
    public String convertTargetValDefinition(String targetVal, Boolean label) {
        if (StringUtils.isBlank(targetVal) || targetVal.indexOf("[") < 0) {
            return targetVal;
        }
        List<Long> idList = parseVariableId(targetVal);
        Map<Long, String> idLableMap = StreamEx.of(idList).map(id -> {
            return this.get(id);
        }).nonNull().toMap(ConditionDefinition::getId, t -> {
                    if (label) {
                        return t.getLabel();
                    } else {
                        return t.getMatchKey();
                    }
                }
        );
        for (Long id : idList) {
            targetVal = targetVal.replaceAll("\\[" + id + "\\]", idLableMap.getOrDefault(id, ""));
        }
        return targetVal;
    }

    @Override
    public BaseOutput save(ConditionDefinition conditionDefinition) {
        if (Objects.nonNull(conditionDefinition)) {
            conditionDefinition.setModifyTime(LocalDateTime.now());
            if (Objects.isNull(conditionDefinition.getId())) {
                conditionDefinition.setCreateTime(conditionDefinition.getModifyTime());
            }
            if (isExistsSameMatchKey(conditionDefinition)) {
                return BaseOutput.failure("在本业务对应的规则定义中，已存在相同指标的相同匹配key");
            }
            saveOrUpdate(conditionDefinition);
            return BaseOutput.success();
        }
        return BaseOutput.failure("参数丢失");
    }


    /**
     * 获取表达式中的关联定义ID
     * @param text 表达式文本
     * @return 关联的计算ID集
     */
    private List<Long> parseVariableId(String text) {
        Pattern pattern = Pattern.compile("(\\[\\d+\\])");
        Matcher m = pattern.matcher(text);
        List<Long> idList = new ArrayList<>();
        while (m.find()) {
            idList.add(Long.parseLong(m.group().replace("[", "").replace("]", "")));
        }
        return idList;
    }

    /**
     * 判断是否以及存在相同的匹配key
     * 根据 所属市场、业务、指标类型，判断是否已存在相同的匹配key
     * 如果存在重复相同的key，则无法正常匹配字段信息：如 匹配客户和部门，，key 都叫id，传入参数后，无法区分到底是哪一个的id
     * @param conditionDefinition
     * @return
     */
    private boolean isExistsSameMatchKey(ConditionDefinition conditionDefinition) {
        ConditionDefinition condition = new ConditionDefinition();
        condition.setMarketId(conditionDefinition.getMarketId());
        condition.setBusinessType(conditionDefinition.getBusinessType());
        condition.setTargetType(conditionDefinition.getTargetType());
        condition.setMatchKey(conditionDefinition.getMatchKey());
        long sameNameCount = this.listByExample(condition).stream().filter((r) -> !r.getId().equals(conditionDefinition.getId())).count();
        return sameNameCount > 0;
    }
}