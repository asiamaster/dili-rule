package com.dili.rule.controller;

import com.alibaba.fastjson.JSON;
import com.dili.assets.sdk.dto.BusinessChargeItemDto;
import com.dili.rule.domain.ChargeRule;
import com.dili.rule.domain.ConditionDefinition;
import com.dili.rule.domain.dto.OperatorUser;
import com.dili.rule.domain.enums.ValueDataTypeEnum;
import com.dili.rule.domain.vo.ChargeRuleVo;
import com.dili.rule.service.ChargeConditionValService;
import com.dili.rule.service.ChargeRuleService;
import com.dili.rule.service.ConditionDefinitionService;
import com.dili.rule.service.remote.BusinessChargeItemRpcService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.uap.sdk.domain.DataDictionaryValue;
import com.dili.uap.sdk.rpc.DataDictionaryRpc;
import com.dili.uap.sdk.session.SessionContext;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.*;

/**
 * <B></B>
 * <B>Copyright:本软件源代码版权归农丰时代科技有限公司及其研发团队所有,未经许可不得任意复制与传播.</B>
 * <B>农丰时代科技有限公司</B>
 *
 * @author yuehongbo
 * @date 2020/5/16 18:06
 */
@Controller
@RequestMapping("/chargeRule")
public class ChargeRuleController {
	private static final Logger log=LoggerFactory.getLogger(ChargeRuleController.class);

    @Autowired
    private ChargeRuleService chargeRuleService;
    @Autowired
    private ChargeConditionValService chargeConditionValService;
    @Autowired
    private DataDictionaryRpc dataDictionaryRpc;
    @Autowired
    private ConditionDefinitionService conditionDefinitionService;
    @Autowired
    private BusinessChargeItemRpcService businessChargeItemRpcService;

    /**
     * 跳转到计费规则管理首页面
     * @param modelMap
     * @return String
     */
    @RequestMapping(value="/index.html", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        modelMap.put("marketId", SessionContext.getSessionContext().getUserTicket().getFirmId());
        modelMap.put("businessTypeList", getBusinessType());
        return "chargeRule/list";
    }

    /**
     * 分页查询计费规则列表信息
     * @param chargeRule
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/listPage.action", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String listPage(ChargeRule chargeRule) {
        try {
            return chargeRuleService.listForEasyuiPage(chargeRule).toString();
        } catch (Exception e) {
            log.error(String.format("根据[%s]查询计费规则列表异常,[%s]", chargeRule, e.getMessage()), e);
            return new EasyuiPageOutput(0, Collections.emptyList()).toString();
        }
    }

    /**
     * 计费规则预编辑
     * @param chargeRule 计费规则信息
     * @param modelMap
     * @return
     */
    @RequestMapping(value = "/preSave.html", method = {RequestMethod.GET, RequestMethod.POST})
    public String preSave(ChargeRule chargeRule, ModelMap modelMap) {
        if (Objects.nonNull(chargeRule)) {
            modelMap.addAttribute("action", "insert");
            chargeRule.setMarketId(SessionContext.getSessionContext().getUserTicket().getFirmId());
            if (null != chargeRule.getId()) {
                chargeRule = chargeRuleService.get(chargeRule.getId());
                if (Objects.nonNull(chargeRule)) {
                    //转换获取计算参数
                    String actionExpression = conditionDefinitionService.convertTargetValDefinition(chargeRule.getActionExpression(), false);
                    chargeRule.setActionExpression(actionExpression);
                    modelMap.addAttribute("action", "update");
                } else {
                    chargeRule.setExpireStart(LocalDateTime.now());
                }
            } else {
                chargeRule.setExpireStart(LocalDateTime.now());
            }
            List<DataDictionaryValue> businessTypeList = getBusinessType();
            String businessType = chargeRule.getBusinessType();
            Optional<DataDictionaryValue> dataDictionaryValue = businessTypeList.stream().filter(t -> businessType.equals(t.getCode())).findFirst();
            if (dataDictionaryValue.isPresent()) {
                modelMap.put("businessTypeName", dataDictionaryValue.get().getName());
            }
            Optional<BusinessChargeItemDto> businessChargeItemDto = businessChargeItemRpcService.get(chargeRule.getChargeItem());
            if (businessChargeItemDto.isPresent()) {
                modelMap.put("chargeItemName", businessChargeItemDto.get().getChargeItem());
            }
            if(StringUtils.isNotBlank(chargeRule.getActionExpressionParams())){
                modelMap.addAttribute("actionExpressionParams", JSON.parse(chargeRule.getActionExpressionParams()));
            }
            modelMap.addAttribute("chargeRule", chargeRule);
        } else {
            chargeRule = new ChargeRule();
            chargeRule.setExpireStart(LocalDateTime.now());
        }
        return "chargeRule/edit";
    }

    /**
     * 获取对应的规则条件值
     * @param chargeRule
     * @param modelMap
     * @return
     */
    @RequestMapping(value = "/getRuleCondition.action", method = { RequestMethod.GET, RequestMethod.POST })
    public String getRuleCondition(ChargeRule chargeRule, ModelMap modelMap) {
        Map<String, Object> map = chargeConditionValService.getRuleCondition(chargeRule);
        modelMap.addAllAttributes(map);
        return "chargeRule/ruleCondition";
    }
    
    /**
     * 获取对应的规则条件值
     * @param chargeRule
     * @param modelMap
     * @return
     */
    @RequestMapping(value = "/getRuleVariable.action", method = { RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    public List<ConditionDefinition> getRuleVariable(ChargeRule chargeRule,Integer dataType, ModelMap modelMap) {
    	List<ConditionDefinition> list = chargeConditionValService.getRuleVariable(chargeRule,ValueDataTypeEnum.fromCode(dataType));
        return list;
    }

    /**
     * 保存计费规则信息
     * @param chargeRuleVo
     * @return
     */
    @RequestMapping(value = "/save.action", method = {RequestMethod.POST })
    @ResponseBody
    public BaseOutput<ChargeRule> save(@RequestBody ChargeRuleVo chargeRuleVo) {
        try {
            return chargeRuleService.save(chargeRuleVo,OperatorUser.fromSessionContext());
        } catch (IllegalArgumentException ex) {
            return BaseOutput.failure(ex.getMessage());
        } catch (Exception e) {
            log.error(String.format("保存计费规则信息[%s] 失败：[%s]", chargeRuleVo.toString(), e.getMessage()), e);
            return BaseOutput.failure(e.getMessage());
        }
    }

    /**
     * 规则审核
     *
     * @param id   需要审核的规则ID
     * @param pass 是否通过 true-是
     * @return
     */
    @RequestMapping(value = "/approve.action", method = { RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    public BaseOutput<Object> approve(Long id, Boolean pass) {
        return chargeRuleService.approve(id, pass);
    }

    /**
     * 规则禁启用
     *
     * @param id       需要禁启用的规则ID
     * @param enable 是否启用 true-是
     * @return
     */
    @RequestMapping(value = "/enable.action", method = { RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    public BaseOutput<Object> enable(Long id, Boolean enable) {
        return chargeRuleService.enable(id, enable);
    }

    /**
     * 计费规则详情查看
     * @param id 规则ID
     * @param modelMap
     * @return
     */
    @RequestMapping(value = "/view.action", method = {RequestMethod.GET, RequestMethod.POST})
    public String view(Long id, ModelMap modelMap) {
        if (Objects.nonNull(id)) {
            ChargeRule chargeRule = chargeRuleService.get(id);
            if (Objects.nonNull(chargeRule)) {
                //转换获取计算参数
                String actionExpression = conditionDefinitionService.convertTargetValDefinition(chargeRule.getActionExpression(), true);
                chargeRule.setActionExpression(actionExpression);
                modelMap.put("businessTypeList", getBusinessType());
                modelMap.addAttribute("chargeRule", chargeRule);
            }
        }
        return "chargeRule/view";
    }

    /**
     * 查看详情-获取对应的规则条件值
     * @param chargeRule
     * @param modelMap
     * @return
     */
    @RequestMapping(value = "/viewRuleCondition.action", method = { RequestMethod.GET, RequestMethod.POST })
    public String viewRuleCondition(ChargeRule chargeRule, ModelMap modelMap) {
        Map<String, Object> map = chargeConditionValService.getRuleCondition(chargeRule);
        modelMap.addAllAttributes(map);
        return "chargeRule/viewCondition";
    }


    /**
     * 规则优先级调整
     * @param id      需要调整的规则ID
     * @param enlarge 是否调升 true-是
     * @return
     */
    @RequestMapping(value = "/adjustPriority.action", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public BaseOutput<Boolean> adjustPriority(Long id, Boolean enlarge) {
        if (Objects.isNull(id) || Objects.isNull(enlarge)) {
            return BaseOutput.failure("参数丢失");
        }
        if (enlarge) {
            return chargeRuleService.enlargePriority(id);
        } else {
            return chargeRuleService.reducePriority(id);
        }
    }

    /**
     * 获取费用业务类型
     * @return
     */
    private List<DataDictionaryValue> getBusinessType() {
        List<DataDictionaryValue> dataList = null;
        try {
            BaseOutput<List<DataDictionaryValue>> output = dataDictionaryRpc.listDataDictionaryValueByDdCode("base_business_type");
            if (output.isSuccess()) {
                dataList = output.getData();
            }
        } catch (Throwable t) {
            log.error("获取业务类型异常:" + t.getMessage(), t);
        }
        return dataList;
    }
}
