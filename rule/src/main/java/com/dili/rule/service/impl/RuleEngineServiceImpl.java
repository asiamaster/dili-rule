package com.dili.rule.service.impl;

import com.alibaba.fastjson.JSON;
import com.dili.rule.domain.ChargeConditionVal;
import com.dili.rule.domain.ChargeRule;
import com.dili.rule.domain.dto.RuleFactsDto;
import com.dili.rule.domain.enums.MatchTypeEnum;
import com.dili.rule.domain.enums.ValueDataTypeEnum;
import com.dili.rule.service.ChargeConditionValService;
import com.dili.rule.service.RuleEngineService;
import org.apache.commons.lang3.StringUtils;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jeasy.rules.core.RuleBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * <B></B>
 * <B>Copyright:本软件源代码版权归农丰时代科技有限公司及其研发团队所有,未经许可不得任意复制与传播.</B>
 * <B>农丰时代科技有限公司</B>
 *
 * @author yuehongbo
 * @date 2020/6/29 17:05
 */
@Service
public class RuleEngineServiceImpl implements RuleEngineService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleEngineServiceImpl.class);

    private final RulesEngine rulesEngine = new DefaultRulesEngine();

    @Autowired
    private ChargeConditionValService chargeConditionValService;

    @Override
    public Boolean checkChargeRule(ChargeRule ruleInfo, Map<String, Object> conditionParams) {
        //根据规则，查询规则条件信息
        ChargeConditionVal conditionValCondition = new ChargeConditionVal();
        conditionValCondition.setRuleId(ruleInfo.getId());
        List<ChargeConditionVal> ruleConditionValList = chargeConditionValService.list(conditionValCondition);
        Map<String, RuleFactsDto> ruleFactsVoMap = this.buildRuleFactsMap(ruleConditionValList, conditionParams);
//        if (ruleConditionValList.size() != ruleFactsVoMap.size()) {
//            return false;
//        }
        // define facts
        Facts facts = this.buildFacts(ruleFactsVoMap);
        // define rules
        Rules rules = this.buildRules(ruleInfo.getId(), ruleInfo.getRuleName());
        boolean result = this.rulesEngine.check(rules, facts).values().stream().allMatch(Predicate.isEqual(Boolean.TRUE));
        return result;
    }

    /**
     * 构建计费规则实际用到的值信息
     * @param ruleConditionValList
     * @param conditionParams
     * @return
     */
    private Map<String, RuleFactsDto> buildRuleFactsMap(List<ChargeConditionVal> ruleConditionValList, Map<String, Object> conditionParams) {
        Map<String, RuleFactsDto> ruleFactsVoMap = ruleConditionValList.stream()
                .filter(rcv -> conditionParams.containsKey(rcv.getMatchKey()))
                .map(rcv -> {
                    String matchKey = rcv.getMatchKey();
                    String val = rcv.getVal();
                    if (StringUtils.isBlank(val)) {
                        val = "[]";
                    }
                    MatchTypeEnum matchTypeEnum = MatchTypeEnum.getInitDataMaps().get(rcv.getMatchType());
                    ValueDataTypeEnum valueDataTypeEnum = ValueDataTypeEnum.getInitDataMaps().get(rcv.getDataType());
                    List<Object> conditionValues = JSON.parseArray(val);
                    String givenValue = String.valueOf(conditionParams.get(matchKey));
                    LOGGER.info("givenValue = {}",givenValue);
                    RuleFactsDto ruleFactsDto = new RuleFactsDto();
                    ruleFactsDto.setGivenValue(givenValue);
                    ruleFactsDto.setConditionValues(conditionValues);
                    ruleFactsDto.setMatchTypeEnum(matchTypeEnum);
                    ruleFactsDto.setValueDataTypeEnum(valueDataTypeEnum);
                    ruleFactsDto.setMatchKey(matchKey);
                    return ruleFactsDto;
                }).collect(Collectors.toMap(RuleFactsDto::getMatchKey, Function.identity()));
        return ruleFactsVoMap;
    }

    /**
     * 封装一组规则事实数据
     * @param ruleFactsDtoMap
     * @return
     */
    private Facts buildFacts(Map<String, RuleFactsDto> ruleFactsDtoMap) {
        Facts facts = new Facts();
        List matchKeys = new ArrayList<>(ruleFactsDtoMap.keySet());
        facts.put("matchKeys", matchKeys);
        facts.put("ruleFactsDtoMap", ruleFactsDtoMap);
        return facts;
    }

    /**
     * 构建一组规则信息
     * @param ruleId
     * @param ruleName
     * @return
     */
    private  Rules buildRules(Long ruleId,String ruleName) {
        Rules rules = new Rules();
        StringBuilder ruleDesc=new StringBuilder()
                .append("RuleInfo id: ").append(ruleId)
                .append(",name: ").append(ruleName);
        Rule r = buildRule(ruleName, ruleDesc.toString());
        rules.register(r);
        return rules;
    }

    /**
     * 构建一个规则信息
     * @param name
     * @param desc
     * @return
     */
    private Rule buildRule(String name, String desc) {

        return new RuleBuilder().name(name).description(desc).when(f -> {
            List<String> matchKeys = f.get("matchKeys");
            Map<String, RuleFactsDto> ruleFactsVoMap = f.get("ruleFactsDtoMap");
            boolean result = matchKeys.stream().map(k -> {
                RuleFactsDto ruleFactsVo = ruleFactsVoMap.get(k);
                return conditionResult(ruleFactsVo);
            }).allMatch(Boolean.TRUE::equals);
            return result;
        }).then(f -> {}).build();

    }

    /**
     * 条件匹配是否匹配
     * @param ruleFactsVo
     * @return
     */
    private Boolean conditionResult(RuleFactsDto ruleFactsVo) {
        ValueDataTypeEnum valueDataTypeEnum = ruleFactsVo.getValueDataTypeEnum();
        List<Object> conditionValues = ruleFactsVo.getConditionValues();
        Comparable<Object> givenValue = convertValue(ruleFactsVo.getGivenValue(), valueDataTypeEnum);
        if (Objects.isNull(givenValue)) {
            return false;
        }
        if (Objects.isNull(conditionValues) || conditionValues.isEmpty()) {
            conditionValues = Arrays.asList(null, null);
        }
        List<Comparable<Object>> conditionList = convertValues(conditionValues, valueDataTypeEnum);

        switch (ruleFactsVo.getMatchTypeEnum()) {
            case EQUALS:
                return givenValue.equals(conditionList.get(0));
            case BETWEEN:
                return compareBetweenValues(givenValue, conditionList);
            case IN:
                return conditionList.contains(givenValue);
            default:
                return false;
        }
    }

    /**
     * 转换值信息
     * @param value
     * @param valueDataTypeEnum
     * @param <T>
     * @return
     */
    private <T> Comparable<T> convertValue(Object value, ValueDataTypeEnum valueDataTypeEnum) {
        if (Objects.isNull(value)) {
            return null;
        }
        String str = String.valueOf(value);
        LOGGER.info("str={}",str);
        switch (valueDataTypeEnum) {
            case DATE:
                return (Comparable<T>) Instant.parse(str);
            case DECIMAL:
                return (Comparable<T>) new BigDecimal(str);
            case INTEGER:
                return (Comparable<T>) new BigInteger(str);
            default:
                break;
        }
        return (Comparable<T>) str;
    }

    /**
     * 批量转换值信息
     * @param values
     * @param valueDataTypeEnum
     * @param <T>
     * @return
     */
    private <T> List<T> convertValues(List<Object> values, ValueDataTypeEnum valueDataTypeEnum) {
        return (List<T>) values.stream().map(v -> convertValue(v, valueDataTypeEnum)).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * 对比值数据
     * @param givenValue
     * @param conditionValues
     * @return
     */
    private boolean compareBetweenValues(Comparable<Object> givenValue, List<Comparable<Object>> conditionValues) {
        if (givenValue == null) {
            return false;
        }
        if (conditionValues == null) {
            return false;
        }
        if (conditionValues.size() != 2) {
            return false;
        }
        boolean anyNull = conditionValues.stream().anyMatch(Objects::isNull);
        if (anyNull) {
            return false;
        }
        return givenValue.compareTo(conditionValues.get(0)) >= 0 && givenValue.compareTo(conditionValues.get(1)) <= 0;

    }
}
