package com.dili.rule.service.impl;

import com.dili.rule.domain.RuleConditionVal;
import com.dili.rule.mapper.RuleConditionValMapper;
import com.dili.rule.service.RuleConditionValService;
import com.dili.ss.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2020-05-13 11:18:54.
 */
@Service
public class RuleConditionValServiceImpl extends BaseServiceImpl<RuleConditionVal, Long> implements RuleConditionValService {

    public RuleConditionValMapper getActualDao() {
        return (RuleConditionValMapper)getDao();
    }
}