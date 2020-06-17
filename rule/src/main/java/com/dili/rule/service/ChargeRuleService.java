package com.dili.rule.service;

import com.dili.rule.domain.ChargeRule;
import com.dili.rule.domain.dto.OperatorUser;
import com.dili.rule.domain.vo.ChargeRuleVo;
import com.dili.ss.base.BaseService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;

/**
 * <B></B>
 * <B>Copyright:本软件源代码版权归农丰时代科技有限公司及其研发团队所有,未经许可不得任意复制与传播.</B>
 * <B>农丰时代科技有限公司</B>
 *
 * @author yuehongbo
 * @date 2020/5/16 18:00
 */
public interface ChargeRuleService extends BaseService<ChargeRule, Long> {

    /**
     * 分页查询计费规则数据
     * @param chargeRule
     * @return
     */
    EasyuiPageOutput listForEasyuiPage(ChargeRule chargeRule) throws Exception;

    /**
     * 保存规则数据信息
     * @param chargeRuleVo
     * @return
     */
    BaseOutput<ChargeRule> save(ChargeRuleVo chargeRuleVo,OperatorUser operatorUser);

    /**
     * 根据有效期时间，变更规则的状态
     * @param rule 需要变更状态的规则
     */
    void updateStateByExpires(ChargeRule rule,OperatorUser operatorUser);

    /**
     * 根据有效期时间，变更规则的状态
     * @param id 需要变更状态的规则ID
     */
    void updateStateByExpires(Long id,OperatorUser operatorUser);

}
