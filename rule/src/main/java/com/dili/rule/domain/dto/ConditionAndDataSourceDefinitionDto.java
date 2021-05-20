package com.dili.rule.domain.dto;

import com.dili.rule.domain.ConditionDefinition;
import com.dili.rule.domain.DataSourceDefinition;

import java.util.List;

public class ConditionAndDataSourceDefinitionDto {
    private ConditionDefinition conditionDefinition;
    private DataSourceDefinition dataSourceDefinition;
    private List<String> displayColumnCodeList;

    public List<String> getDisplayColumnCodeList() {
        return displayColumnCodeList;
    }

    public void setDisplayColumnCodeList(List<String> displayColumnCodeList) {
        this.displayColumnCodeList = displayColumnCodeList;
    }

    public ConditionDefinition getConditionDefinition() {
        return conditionDefinition;
    }

    public void setConditionDefinition(ConditionDefinition conditionDefinition) {
        this.conditionDefinition = conditionDefinition;
    }

    public DataSourceDefinition getDataSourceDefinition() {
        return dataSourceDefinition;
    }

    public void setDataSourceDefinition(DataSourceDefinition dataSourceDefinition) {
        this.dataSourceDefinition = dataSourceDefinition;
    }
}
