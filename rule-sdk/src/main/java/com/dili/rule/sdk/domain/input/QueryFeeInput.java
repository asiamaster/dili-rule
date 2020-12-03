package com.dili.rule.sdk.domain.input;

import java.util.HashMap;
import java.util.Map;

/**
 * <B>费用查询计算时的输入对象</B>
 * <B>Copyright:本软件源代码版权归农丰时代科技有限公司及其研发团队所有,未经许可不得任意复制与传播.</B>
 * <B>农丰时代科技有限公司</B>
 *
 * @author yuehongbo
 * @date 2020/6/29 11:11
 */
public class QueryFeeInput {

    /**
     * 请求数据ID
     */
    private String requestDataId;

    /**
     * 规则所属于某个市场
     */
    private Long marketId;

    /**
     * 所属的业务类型
     */
    private String businessType;

    /**
     * 收费项
     */
    private Long chargeItem;

    /**
     * 计算参数数据项
     */
    private Map<String, Object> calcParams = new HashMap<>();

    /**
     * 条件指标数据值
     */
    private Map<String, Object> conditionParams = new HashMap<>();

    public String getRequestDataId() {
        return requestDataId;
    }
    public void setRequestDataId(String requestDataId) {
        this.requestDataId = requestDataId;
    }
    public Long getMarketId() {
        return marketId;
    }
    public void setMarketId(Long marketId) {
        this.marketId = marketId;
    }
    public String getBusinessType() {
        return businessType;
    }
    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }
    public Long getChargeItem() {
        return chargeItem;
    }
    public void setChargeItem(Long chargeItem) {
        this.chargeItem = chargeItem;
    }
    public Map<String, Object> getCalcParams() {
        return calcParams;
    }
    public void setCalcParams(Map<String, Object> calcParams) {
        this.calcParams = calcParams;
    }
    public Map<String, Object> getConditionParams() {
        return conditionParams;
    }
    public void setConditionParams(Map<String, Object> conditionParams) {
        this.conditionParams = conditionParams;
    }
}
