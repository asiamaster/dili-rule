package com.dili.rule.domain.vo;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Transient;

import com.dili.rule.domain.ChargeRule;

/**
 * <B></B>
 * <B>Copyright:本软件源代码版权归农丰时代科技有限公司及其研发团队所有,未经许可不得任意复制与传播.</B>
 * <B>农丰时代科技有限公司</B>
 *
 * @author yuehongbo
 * @date 2020/5/18 16:59
 */
public class ChargeRuleVo extends ChargeRule implements Serializable {

    @Transient
    private static final long serialVersionUID = 6501003059869163674L;

    /**
     * 规则条件标签值
     */
    private String conditionLabel;

    /**
     * 规则条件设置项
     * json格式
     */
    @Transient
    private List<ConditionVo> conditionList;


    public String getConditionLabel() {
        return conditionLabel;
    }
    public void setConditionLabel(String conditionLabel) {
        this.conditionLabel = conditionLabel;
    }
	public List<ConditionVo> getConditionList() {
		return conditionList;
	}
	public void setConditionList(List<ConditionVo> conditionList) {
		this.conditionList = conditionList;
	}
}
