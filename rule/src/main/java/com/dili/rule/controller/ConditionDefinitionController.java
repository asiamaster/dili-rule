package com.dili.rule.controller;

import com.dili.rule.domain.ConditionDefinition;
import com.dili.rule.service.ConditionDefinitionService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Objects;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2020-05-13 11:23:41.
 */
@Controller
@RequestMapping("/conditionDefinition")
@Slf4j
public class ConditionDefinitionController {
    @Autowired
    private ConditionDefinitionService conditionDefinitionService;

    /**
     * 跳转到规则预定义首页面
     * @param modelMap
     * @return String
     */
    @RequestMapping(value="/index.html", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        return "conditionDefinition/list";
    }

    /**
     * 分页查询规则预定义列表信息
     * @param conditionDefinition
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/listPage.action", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String listPage(ConditionDefinition conditionDefinition, HttpServletRequest request) {
        try {
            return conditionDefinitionService.listEasyuiPageByExample(conditionDefinition, true).toString();
        } catch (Exception e) {
            log.error("查询规则预定义列表异常," + e.getMessage(), e);
            return new EasyuiPageOutput(0, Collections.emptyList()).toString();
        }
    }

    /**
     * 跳转到编辑页面
     * @param id 数据ID
     * @return
     */
    @RequestMapping(value = "/preSave.html", method = RequestMethod.GET)
    public String preSave(Long id, ModelMap modelMap) {
        if (Objects.nonNull(id)) {
            ConditionDefinition conditionDefinition = conditionDefinitionService.get(id);
            if (Objects.nonNull(conditionDefinition)) {
                modelMap.put("conditionDefinition", conditionDefinition);
            }
        }
        return "conditionDefinition/edit";
    }

    /**
     * 保存预定义数据
     * @param conditionDefinition
     * @return
     */
    @RequestMapping(value = "/save.action", method = {RequestMethod.POST})
    @ResponseBody
    public BaseOutput save(ConditionDefinition conditionDefinition) {
        conditionDefinitionService.saveOrUpdate(conditionDefinition);
        return BaseOutput.success();
    }
}