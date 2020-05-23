package com.dili.rule.mapper;

import com.dili.rule.domain.ChargeRule;
import com.dili.rule.domain.vo.ChargeRuleVo;
import com.dili.ss.base.MyMapper;

import java.util.List;

public interface ChargeRuleMapper extends MyMapper<ChargeRule> {

    /**
     * 根据条件联合查询数据信息
     *
     * @param user
     * @return
     */
    List<ChargeRuleVo> listForPage(ChargeRule user);
}