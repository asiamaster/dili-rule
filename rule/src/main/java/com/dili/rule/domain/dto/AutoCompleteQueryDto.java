package com.dili.rule.domain.dto;

public class AutoCompleteQueryDto {
    private Long dataSourceDefinitionId;
    private String query;
    private String autoCompleteQueryKey;
    private String queryUrl;
    private String dataSourceType;

    public String getDataSourceType() {
        return dataSourceType;
    }

    public void setDataSourceType(String dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    public Long getDataSourceDefinitionId() {
        return dataSourceDefinitionId;
    }

    public void setDataSourceDefinitionId(Long dataSourceDefinitionId) {
        this.dataSourceDefinitionId = dataSourceDefinitionId;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getAutoCompleteQueryKey() {
        return autoCompleteQueryKey;
    }

    public void setAutoCompleteQueryKey(String autoCompleteQueryKey) {
        this.autoCompleteQueryKey = autoCompleteQueryKey;
    }

    public String getQueryUrl() {
        return queryUrl;
    }

    public void setQueryUrl(String queryUrl) {
        this.queryUrl = queryUrl;
    }
}
