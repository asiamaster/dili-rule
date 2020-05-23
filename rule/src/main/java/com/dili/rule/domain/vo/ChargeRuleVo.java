package com.dili.rule.domain.vo;

import com.dili.rule.domain.ChargeRule;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Objects;

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
     * 有效期文本
     */
    private String expireValue;

    public String getExpireValue() {
        StringBuilder str = new StringBuilder();
        if (Objects.nonNull(getExpireStart())) {
            str.append(getExpireStart().toString());
        }
        str.append(" — ");
        if (Objects.nonNull(getExpireEnd())) {
            str.append(getExpireEnd().toString());
        }
        return str.toString();
    }
}
