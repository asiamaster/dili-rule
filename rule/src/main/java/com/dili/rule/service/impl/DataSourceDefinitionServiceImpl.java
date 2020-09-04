package com.dili.rule.service.impl;

import com.dili.rule.domain.DataSourceDefinition;
import com.dili.rule.domain.DataSourceColumn;
import com.dili.rule.service.DataSourceColumnService;
import com.dili.ss.base.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Objects;
import com.dili.rule.service.DataSourceDefinitionService;
import com.dili.rule.mapper.DataSourceDefinitionMapper;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2020-05-13 11:19:20.
 */
@Service
public class DataSourceDefinitionServiceImpl extends BaseServiceImpl<DataSourceDefinition, Long> implements DataSourceDefinitionService {

    public DataSourceDefinitionMapper getActualMapper() {
        return (DataSourceDefinitionMapper)getDao();
    }

    @Autowired
    private DataSourceColumnService dataSourceColumnService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Long key) {
        if (Objects.isNull(key)) {
            return 0;
        }
        DataSourceColumn dataSourceColumn = new DataSourceColumn();
        dataSourceColumn.setDataSourceId(key);
        dataSourceColumnService.deleteByExample(dataSourceColumn);
        return super.delete(key);
    }
}