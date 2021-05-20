package com.dili.rule.domain.dto;

import com.dili.rule.domain.ChargeRule;

import javax.persistence.Transient;
import java.util.Map;

public class ChargeRuleQueryDto extends ChargeRule {
    private Map<Long, String> conditionMap;


    @Transient
    private String sortSql;

    @Transient
    private String extSql;

    public String getExtSql() {
        return extSql;
    }

    public void setExtSql(String extSql) {
        this.extSql = extSql;
    }

    public String getSortSql() {
        return sortSql;
    }

    public void setSortSql(String sortSql) {
        this.sortSql = sortSql;
    }

    public Map<Long, String> getConditionMap() {
        return conditionMap;
    }

    public void setConditionMap(Map<Long, String> conditionMap) {
        this.conditionMap = conditionMap;
    }
}
