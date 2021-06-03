package com.dili.rule.dto;

import com.dili.rule.domain.ChargeConditionVal;
import com.dili.ss.domain.annotation.Operator;

import javax.persistence.Column;
import java.util.List;

public class ChargeConditionValQueryDto extends ChargeConditionVal {
    @Column(name = "`rule_id`")
    @Operator(Operator.IN)
    private List<Long> ruleIdList;

    public List<Long> getRuleIdList() {
        return ruleIdList;
    }

    public void setRuleIdList(List<Long> ruleIdList) {
        this.ruleIdList = ruleIdList;
    }
}
