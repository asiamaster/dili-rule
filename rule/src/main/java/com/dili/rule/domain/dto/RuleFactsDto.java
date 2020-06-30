package com.dili.rule.domain.dto;

import com.dili.rule.domain.enums.MatchTypeEnum;
import com.dili.rule.domain.enums.ValueDataTypeEnum;
import com.google.common.base.MoreObjects;

import java.util.ArrayList;
import java.util.List;

/**
 * <B>计费规则时间数据对象</B>
 * <B>Copyright:本软件源代码版权归农丰时代科技有限公司及其研发团队所有,未经许可不得任意复制与传播.</B>
 * <B>农丰时代科技有限公司</B>
 *
 * @author yuehongbo
 * @date 2020/6/29 17:25
 */
public class RuleFactsDto {

    /**
     * 匹配key
     */
    private String matchKey = null;
    /**
     * 规定值
     */
    private String givenValue = null;
    /**
     * 条件值
     */
    private List<Object> conditionValues = new ArrayList<>();

    private MatchTypeEnum matchTypeEnum;
    private ValueDataTypeEnum valueDataTypeEnum;
    public String getMatchKey() {
        return matchKey;
    }
    public void setMatchKey(String matchKey) {
        this.matchKey = matchKey;
    }
    public String getGivenValue() {
        return givenValue;
    }
    public void setGivenValue(String givenValue) {
        this.givenValue = givenValue;
    }
    public List<Object> getConditionValues() {
        return conditionValues;
    }
    public void setConditionValues(List<Object> conditionValues) {
        this.conditionValues = conditionValues;
    }
    public MatchTypeEnum getMatchTypeEnum() {
        return matchTypeEnum;
    }
    public void setMatchTypeEnum(MatchTypeEnum matchTypeEnum) {
        this.matchTypeEnum = matchTypeEnum;
    }
    public ValueDataTypeEnum getValueDataTypeEnum() {
        return valueDataTypeEnum;
    }
    public void setValueDataTypeEnum(ValueDataTypeEnum valueDataTypeEnum) {
        this.valueDataTypeEnum = valueDataTypeEnum;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("matchKey", matchKey)
                .add("givenValue", givenValue)
                .add("conditionValues", conditionValues)
                .add("matchTypeEnum", matchTypeEnum)
                .add("valueDataTypeEnum", valueDataTypeEnum)
                .toString();
    }
}
