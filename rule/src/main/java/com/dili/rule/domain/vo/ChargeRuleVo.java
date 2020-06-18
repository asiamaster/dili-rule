package com.dili.rule.domain.vo;

import com.dili.rule.domain.ChargeRule;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

/**
 * <B></B>
 * <B>Copyright:本软件源代码版权归农丰时代科技有限公司及其研发团队所有,未经许可不得任意复制与传播.</B>
 * <B>农丰时代科技有限公司</B>
 *
 * @author yuehongbo
 * @date 2020/5/18 16:59
 */
@Getter
@Setter
@ToString(callSuper = true)
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
    private List<ConditionVo> conditions;
    

    /**
     * @return String return the conditionLabel
     */
    public String getConditionLabel() {
        return conditionLabel;
    }

    /**
     * @param conditionLabel the conditionLabel to set
     */
    public void setConditionLabel(String conditionLabel) {
        this.conditionLabel = conditionLabel;
    }

	public List<ConditionVo> getConditions() {
		return conditions;
	}

	public void setConditions(List<ConditionVo> conditions) {
		this.conditions = conditions;
	}



}
