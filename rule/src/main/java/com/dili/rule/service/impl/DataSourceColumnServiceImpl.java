package com.dili.rule.service.impl;

import com.dili.rule.domain.DataSourceColumn;
import com.dili.rule.mapper.DataSourceColumnMapper;
import com.dili.rule.service.DataSourceColumnService;
import com.dili.ss.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2020-05-13 11:19:57.
 */
@Service
public class DataSourceColumnServiceImpl extends BaseServiceImpl<DataSourceColumn, Long> implements DataSourceColumnService {

    public DataSourceColumnMapper getActualDao() {
        return (DataSourceColumnMapper)getDao();
    }
}