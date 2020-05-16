package com.dili.rule.service.impl;

import com.dili.rule.domain.Rule;
import com.dili.rule.mapper.RuleMapper;
import com.dili.rule.service.RuleService;
import com.dili.ss.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2020-05-13 11:17:11.
 */
@Service
public class RuleServiceImpl extends BaseServiceImpl<Rule, Long> implements RuleService {

    public RuleMapper getActualDao() {
        return (RuleMapper)getDao();
    }
}