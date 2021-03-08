package com.dili.rule.domain.dto;

import java.util.Map;

public class RemoteAjaxInputDto {
    private Long dataSourceId;
    private Map<String, Object> params;

    public Long getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(Long dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}
