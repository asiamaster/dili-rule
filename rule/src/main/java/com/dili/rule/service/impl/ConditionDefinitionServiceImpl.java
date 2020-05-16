package com.dili.rule.service.impl;

import com.dili.rule.domain.ConditionDefinition;
import com.dili.rule.mapper.ConditionDefinitionMapper;
import com.dili.rule.service.ConditionDefinitionService;
import com.dili.ss.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2020-05-13 11:23:41.
 */
@Service
public class ConditionDefinitionServiceImpl extends BaseServiceImpl<ConditionDefinition, Long> implements ConditionDefinitionService {

    public ConditionDefinitionMapper getActualDao() {
        return (ConditionDefinitionMapper)getDao();
    }
}