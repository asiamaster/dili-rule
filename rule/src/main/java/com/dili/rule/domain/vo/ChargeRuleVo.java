package com.dili.rule.domain.vo;

import com.dili.rule.domain.ChargeRule;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Transient;
import java.io.Serializable;

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
    private String conditions;
    

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

    /**
     * @return String return the conditions
     */
    public String getConditions() {
        return conditions;
    }

    /**
     * @param conditions the conditions to set
     */
    public void setConditions(String conditions) {
        this.conditions = conditions;
    }

}
