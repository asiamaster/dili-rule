package com.dili.rule.controller;

import com.dili.rule.domain.ChargeRule;
import com.dili.rule.service.ChargeConditionValService;
import com.dili.rule.service.ChargeRuleService;
import com.dili.rule.service.remote.MarketRpcService;
import com.dili.rule.service.remote.SystemRpcService;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.uap.sdk.domain.dto.SystemDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

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
@Slf4j
public class ChargeRuleController {

    @Autowired
    private ChargeRuleService chargeRuleService;
    @Autowired
    private MarketRpcService marketRpcService;
    @Autowired
    private SystemRpcService systemRpcService;
    @Autowired
    private ChargeConditionValService chargeConditionValService;

    /**
     * 跳转到计费规则管理首页面
     * @param modelMap
     * @return String
     */
    @RequestMapping(value="/index.html", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        modelMap.put("marketList", marketRpcService.getCurrentUserFirms());
        modelMap.put("systemList", systemRpcService.listByExample(DTOUtils.newInstance(SystemDto.class)));
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
            return chargeRuleService.listEasyuiPageByExample(chargeRule, true).toString();
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
            if (null != chargeRule.getId()) {
                chargeRule = chargeRuleService.get(chargeRule.getId());
                if (Objects.nonNull(chargeRule)) {
                    modelMap.addAttribute("action", "update");
                }
            }
            modelMap.put("marketList", marketRpcService.getCurrentUserFirms());
            modelMap.put("systemList", systemRpcService.listByExample(DTOUtils.newInstance(SystemDto.class)));
            modelMap.addAttribute("chargeRule", chargeRule);
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
}
