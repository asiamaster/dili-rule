package com.dili.rule.domain.dto;

import com.dili.rule.domain.ChargeRule;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * <B>费用规则计算返回值对象</B>
 * <B>Copyright:本软件源代码版权归农丰时代科技有限公司及其研发团队所有,未经许可不得任意复制与传播.</B>
 * <B>农丰时代科技有限公司</B>
 *
 * @author yuehongbo
 * @date 2020/6/29 15:49
 */
public class CalculateFeeDto {

    /**
     * 若匹配，则为匹配到的规则信息
     */
    private ChargeRule ruleInfo;

    /**
     * 计算出来的费用信息
     */
    private BigDecimal fee;

    /**
     *
     */
    private String code;

    /**
     * 错误信息
     */
    private Optional<String> message = Optional.empty();

    public ChargeRule getRuleInfo() {
        return ruleInfo;
    }
    public void setRuleInfo(ChargeRule ruleInfo) {
        this.ruleInfo = ruleInfo;
    }
    public BigDecimal getFee() {
        return fee;
    }
    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }
    public Optional<String> getMessage() {
        return message;
    }
    public void setMessage(Optional<String> message) {
        this.message = message;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
}
