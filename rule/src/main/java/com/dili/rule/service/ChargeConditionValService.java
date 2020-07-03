package com.dili.rule.service;

import com.dili.rule.domain.ChargeConditionVal;
import com.dili.rule.domain.ChargeRule;
import com.dili.rule.domain.ConditionDefinition;
import com.dili.ss.base.BaseService;

import java.util.List;
import java.util.Map;

/**
 * <B></B> <B>Copyright:本软件源代码版权归农丰时代科技有限公司及其研发团队所有,未经许可不得任意复制与传播.</B>
 * <B>农丰时代科技有限公司</B>
 *
 * @author yuehongbo
 * @date 2020/5/16 18:03
 */
public interface ChargeConditionValService extends BaseService<ChargeConditionVal, Long> {

	/**
	 * 根据规则ID获取规则条件指标详细信息
	 * 
	 * @param chargeRule 规则对象
	 */
	Map<String, Object> getRuleCondition(ChargeRule chargeRule);

	/**
	 * 根据规则ID获取计算条件(变量)指标详细信息
	 * 
	 * @param chargeRule 规则对象
	 */
	List<ConditionDefinition> getRuleVariable(ChargeRule chargeRule);

	/**
	 * 根据规则ID删除规则条件信息
	 * 
	 * @param ruleId 规则ID
	 * @return
	 */
	Integer deleteByRuleId(Long ruleId);
}
