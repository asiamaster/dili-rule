package com.dili.rule.api;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.dili.rule.domain.dto.CalculateFeeDto;
import com.dili.rule.sdk.domain.input.QueryFeeInput;
import com.dili.rule.sdk.domain.output.QueryFeeOutput;
import com.dili.rule.service.ChargeRuleService;
import com.dili.ss.constant.ResultCode;
import com.dili.ss.domain.BaseOutput;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 计费规则服务
 * Copyright:本软件源代码版权归农丰时代科技有限公司及其研发团队所有,未经许可不得任意复制与传播.
 * 农丰时代科技有限公司
 *
 * @author yuehongbo
 * @date 2020/6/29 14:38
 */
@RestController
@RequestMapping("/api/chargeRule")
public class ChargeRuleApiController {

    private static final Logger logger = LoggerFactory.getLogger(ChargeRuleApiController.class);

    @Autowired
    private ChargeRuleService chargeRuleService;

    /**
     * 根据条件匹配规则并返回费用信息
     * <resultCode>
     *     200-找到并匹配到规则，正常返回计算出的费用;
     *     404-根据业务费用项未找到可用（有效）的规则;
     *     2000-根据传入的匹配条件未匹配到规则;
     *     5000-其它错误信息;
     * </resultCode>
     * @param queryFeeInput 费用计算输入条件
     * @return 费用计算结果
     */
    @RequestMapping("/queryFee")
    public BaseOutput<QueryFeeOutput> queryFee(@RequestBody QueryFeeInput queryFeeInput) {
        logger.info("queryFeeInput: {}", JSONObject.toJSONString(queryFeeInput));
        Optional<String> checkResult = this.checkInput(queryFeeInput);
        if (checkResult.isPresent()) {
            return BaseOutput.failure(checkResult.get());
        }
        QueryFeeOutput vo = this.queryRule(queryFeeInput);
        if (StringUtils.isNotBlank(vo.getMessage())) {
            return BaseOutput.failure(vo.getMessage()).setData(vo).setCode(vo.getCode());
        } else {
            return BaseOutput.successData(vo);
        }
    }

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
    @RequestMapping("/batchQueryFee")
    public BaseOutput<List<QueryFeeOutput>> batchQueryFee(@RequestBody List<QueryFeeInput> queryFeeInputList) {
        logger.info("queryFeeInputList: {}", JSONObject.toJSONString(queryFeeInputList));
        if (CollectionUtil.isEmpty(queryFeeInputList)) {
            return BaseOutput.failure("数据为空").setCode(ResultCode.PARAMS_ERROR);
        }
        return this.checkInputList(queryFeeInputList)
                .map(BaseOutput.failure()::setMessage).orElseGet(() -> this.queryRules(queryFeeInputList));

    }

    /**
     * 批量验证输入参数的是否正确
     * @param queryFeeInputList 输入参数值
     * @return 验证结果信息
     */
    private Optional<String> checkInputList(List<QueryFeeInput> queryFeeInputList) {
        return queryFeeInputList.stream().map(this::checkInput).filter(Optional::isPresent).map(Optional::get).findFirst();
    }

    /**
     * 验证输入条件是否合法
     * @param queryFeeInput 输入参数
     * @return 验证结果信息
     */
    private Optional<String> checkInput(QueryFeeInput queryFeeInput) {
        if (Objects.isNull(queryFeeInput.getMarketId())) {
            return Optional.of("所属市场不能为空");
        }
        if (StringUtils.isBlank(queryFeeInput.getBusinessType())) {
            return Optional.of("业务类型不能为空");
        }
        if (Objects.isNull(queryFeeInput.getChargeItem())) {
            return Optional.of("收费项不能为空");
        }
        return Optional.empty();
    }

    /**
     * 查询规则并根据条件计算金额
     * @param queryFeeInput 规则计算输入条件
     * @return 计算结果
     */
    private QueryFeeOutput queryRule(QueryFeeInput queryFeeInput) {
        QueryFeeOutput output = new QueryFeeOutput();
        try {
            output = chargeRuleService.findRuleInfoAnaCalculateFee(queryFeeInput);
            if (!output.getCode().equals(ResultCode.OK)) {
                output.setSuccess(false);
            }
            return output;
        } catch (Exception e) {
            logger.error("规则计算费用时异常:" + e.getMessage(), e);
            output.setMessage("规则无法计算，请输入金额");
            output.setSuccess(false);
            output.setCode(ResultCode.APP_ERROR);
            return output;
        }
    }

    /**
     * 批量获取规则并计算结果
     * @param queryFeeInputList 规则计算输入值
     * @return 批量处理结果
     */
    private BaseOutput<List<QueryFeeOutput>> queryRules(List<QueryFeeInput> queryFeeInputList) {
        List<QueryFeeOutput> resultList = new ArrayList<>();
        for (QueryFeeInput queryFeeInput : queryFeeInputList) {
            QueryFeeOutput output = this.queryRule(queryFeeInput);
            if (StringUtils.isNotBlank(output.getMessage())) {
                return BaseOutput.failure(output.getMessage()).setData(output).setCode(output.getCode());
            }
            resultList.add(output);
        }
        return BaseOutput.successData(resultList);
    }
}
