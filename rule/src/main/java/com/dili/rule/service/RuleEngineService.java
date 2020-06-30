package com.dili.rule.service;

import com.dili.rule.domain.ChargeRule;

import java.util.Map;

/**
 * <B>规则匹配Engine</B>
 * <B>Copyright:本软件源代码版权归农丰时代科技有限公司及其研发团队所有,未经许可不得任意复制与传播.</B>
 * <B>农丰时代科技有限公司</B>
 *
 * @author yuehongbo
 * @date 2020/6/29 17:02
 */
public interface RuleEngineService {

    /**
     * 基于给定的RuleInfo和传递的数据,确定条件值是否匹配
     * @param ruleInfo
     * @param conditionParams
     * @return
     */
    Boolean checkChargeRule(ChargeRule ruleInfo, Map<String, Object> conditionParams);
}
