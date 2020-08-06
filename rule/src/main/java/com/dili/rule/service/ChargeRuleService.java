package com.dili.rule.service;

import com.dili.rule.domain.ChargeRule;
import com.dili.rule.domain.dto.OperatorUser;
import com.dili.rule.domain.vo.ChargeRuleVo;
import com.dili.rule.sdk.domain.input.QueryFeeInput;
import com.dili.rule.sdk.domain.output.QueryFeeOutput;
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

    /**
     * 根据条件查询规则并计算费用
     * @param queryFeeInput
     * @return 费用计算结果
     */
    QueryFeeOutput findRuleInfoAnaCalculateFee(QueryFeeInput queryFeeInput);

    /**
     * 规则审核结果
     * @param id   需要审核的规则ID
     * @param pass 是否通过
     * @return
     */
    BaseOutput<Object> approve(Long id, Boolean pass);

    /**
     * 规则禁启用
     * @param id       需要禁启用的规则ID
     * @param enable 是否启用
     * @return
     */
    BaseOutput<Object> enable(Long id, Boolean enable);


    /**
     * 作废某条规则
     * @param id 规则ID
     * @param operatorUser 操作人信息
     * @return
     */
    Integer obsolete(Long id, OperatorUser operatorUser);

    /**
     * 根据规则有效期，更改规则信息
     * @param ruleInfo
     * @return
     */
    Integer updateRuleInfoWithExpire(ChargeRule ruleInfo, OperatorUser operatorUser);

    /**
     * 扩大优先级
     * @param id 需要扩大优先级的数据ID
     * @return 是否成功
     */
    BaseOutput<Boolean> enlargePriority(long id);

    /**
     * 缩小优先级
     * @param id 需要缩小优先级的数据ID
     * @return 是否成功
     */
    BaseOutput<Boolean> reducePriority(long id);

}
