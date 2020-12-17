package com.dili.rule.sdk.rpc;

import com.dili.rule.sdk.domain.input.QueryFeeInput;
import com.dili.rule.sdk.domain.output.QueryFeeOutput;
import com.dili.ss.domain.BaseOutput;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * <B>计费规则rpc类</B>
 * <B>Copyright:本软件源代码版权归农丰时代科技有限公司及其研发团队所有,未经许可不得任意复制与传播.</B>
 * <B>农丰时代科技有限公司</B>
 *
 * @author yuehongbo
 * @date 2020/6/30 15:57
 */
@FeignClient(name = "dili-rule", contextId = "chargeRuleRpc")
public interface ChargeRuleRpc {

    /**
     * 根据条件匹配规则并返回费用信息
     * @param queryFeeInput
     * @return 费用计算处理结果
     */
    @RequestMapping(value = "/api/chargeRule/queryFee", method = RequestMethod.POST)
    BaseOutput<QueryFeeOutput> queryFee(QueryFeeInput queryFeeInput);

    /**
     * 批量获取费用信息
     * @param queryFeeInputList 批量获取的输入条件
     * @return 处理结果
     */
    @RequestMapping(value = "/api/chargeRule/batchQueryFee", method = RequestMethod.POST)
    BaseOutput<List<QueryFeeOutput>> batchQueryFee(List<QueryFeeInput> queryFeeInputList);


    /**
     * 批量获取费用信息
     * <resultCode>
     *     200-找到并匹配到规则，正常返回计算出的费用;
     *     404-根据业务费用项未找到可用（有效）的规则;
     *     2000-根据传入的匹配条件未匹配到规则;
     *     5000-其它错误信息;
     * </resultCode>
     * @param queryFeeInputList 批量获取的输入条件
     * @return 处理结果
     */
    @RequestMapping(value="/api/chargeRule/batchQueryFeeWithoutShortcut", method = RequestMethod.POST)
    public BaseOutput<List<QueryFeeOutput>> batchQueryFeeWithoutShortcut(@RequestBody List<QueryFeeInput> queryFeeInputList);
}
