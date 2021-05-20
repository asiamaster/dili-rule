package com.dili.rule.domain.dto;

import cn.hutool.core.date.DateUtil;
import com.dili.rule.domain.ChargeRule;

import javax.persistence.Transient;
import java.util.Objects;

public class ChargeRuleResultDto extends ChargeRule {

    @Transient
    private String conditionLabel;

    public String getConditionLabel() {
        return conditionLabel;
    }

    public void setConditionLabel(String conditionLabel) {
        this.conditionLabel = conditionLabel;
    }

    public String getExpireValue() {
        StringBuilder str = new StringBuilder();
        if (Objects.nonNull(getExpireStart())) {
            str.append(DateUtil.formatLocalDateTime(getExpireStart()));
        }
        str.append(" 至 ");
        if (Objects.nonNull(getExpireEnd())) {
            str.append(DateUtil.formatLocalDateTime(getExpireEnd()));
        }
        return str.toString();
    }

}
