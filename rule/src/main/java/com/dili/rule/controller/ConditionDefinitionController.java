package com.dili.rule.controller;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dili.commons.glossary.YesOrNoEnum;
import com.dili.rule.domain.ConditionDataSource;
import com.dili.rule.domain.ConditionDefinition;
import com.dili.rule.domain.DataSourceColumn;
import com.dili.rule.service.ConditionDataSourceService;
import com.dili.rule.service.ConditionDefinitionService;
import com.dili.rule.service.DataSourceColumnService;
import com.dili.rule.service.remote.RemoteDataQueryService;
import com.dili.ss.constant.ResultCode;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;

import lombok.extern.slf4j.Slf4j;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2020-05-13 11:23:41.
 */
@Controller
@RequestMapping("/conditionDefinition")
@Slf4j
public class ConditionDefinitionController {
	private static final Logger log=LoggerFactory.getLogger(ConditionDefinitionController.class);
    @Autowired
    private ConditionDefinitionService conditionDefinitionService;
    @Autowired
    private ConditionDataSourceService conditionDataSourceService;
    @Autowired
    private DataSourceColumnService dataSourceColumnService;
    @Autowired
    private RemoteDataQueryService remoteDataQueryService;

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
            log.error("查询条件预定义列表异常," + e.getMessage(), e);
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
     * 跳转到数据源-查询框编辑页面
     * @param id 数据ID
     * @param dataTargetId 目标数据源
     * @return
     */
    @RequestMapping(value = "/queryInputPreSave.html", method = RequestMethod.GET)
    public String queryInputPreSave(Long id, Long dataTargetId, ModelMap modelMap) {
        ConditionDefinition conditionDefinition = new ConditionDefinition();
        conditionDefinition.setDataTargetId(dataTargetId);
        conditionDefinition.setRuleCondition(YesOrNoEnum.NO.getCode());
        if (Objects.nonNull(id)) {
            conditionDefinition = conditionDefinitionService.get(id);
        }
        modelMap.put("conditionDefinition", conditionDefinition);
        return "conditionDataSource/queryInputEdit";
    }

    /**
     * 保存预定义数据
     * @param conditionDefinition
     * @return
     */
    @RequestMapping(value = "/save.action", method = {RequestMethod.POST})
    @ResponseBody
    public BaseOutput save(ConditionDefinition conditionDefinition) {
        if (Objects.nonNull(conditionDefinition)) {
            conditionDefinition.setModifyTime(LocalDateTime.now());
            if (Objects.isNull(conditionDefinition.getId())) {
                conditionDefinition.setCreateTime(conditionDefinition.getModifyTime());
            }
            conditionDefinitionService.saveOrUpdate(conditionDefinition);
            return BaseOutput.success();
        }
        return BaseOutput.failure("参数丢失");
    }

    /**
     * 删除数据列
     * @param id 数据列ID
     * @return
     */
    @RequestMapping(value = "/delete.action", method = {RequestMethod.POST})
    @ResponseBody
    public BaseOutput<Object> delete(Long id) {
        if (Objects.isNull(id)) {
            return BaseOutput.failure("参数丢失").setCode(ResultCode.PARAMS_ERROR);
        }
        try {
            conditionDefinitionService.delete(id);
            return BaseOutput.success("删除成功");
        } catch (Exception e) {
            log.error(String.format("删除数据定义[%d]异常:[%s]", id, e.getMessage()), e);
            return BaseOutput.failure();
        }
    }

    /**
     * 根据数据定义获取条件信息并返回条件数据页
     * @param definitionId 条件预定义ID
     * @return
     */
    @RequestMapping(value = "/getConditionData.action", method = {RequestMethod.GET})
    public String getConditionData(Long definitionId, ModelMap modelMap) {
        ConditionDefinition conditionDefinition = this.conditionDefinitionService.get(definitionId);
        modelMap.put("conditionDefinition", conditionDefinition);
        return "conditionDefinition/conditionData";
    }

    /**
     * 获取动态条件数据
     * @param params
     * @return
     */
    @RequestMapping(value = "/ajaxTable.action", method = {RequestMethod.POST})
    public String ajaxTable(@RequestBody Map<String, Object> params, ModelMap modelMap) {
        Long definitionId = Long.parseLong(params.remove("definitionId").toString());
        ConditionDefinition conditionDefinition = this.conditionDefinitionService.get(definitionId);
        Long dataSourceId = conditionDefinition.getDataSourceId();

        ConditionDataSource conditionDataSource = conditionDataSourceService.get(dataSourceId);

        DataSourceColumn columnCondition = new DataSourceColumn();
        columnCondition.setDataSourceId(dataSourceId);
        columnCondition.setVisible(YesOrNoEnum.YES.getCode());
        columnCondition.setOrder("ASC");
        columnCondition.setSort("column_index");
        List<DataSourceColumn> dataSourceColumns = dataSourceColumnService.listByExample(columnCondition);

        Page<Map<String, Object>> page = this.remoteDataQueryService.queryData(conditionDataSource, params);
        modelMap.put("page", page);
        ConditionDefinition condition = new ConditionDefinition();
        condition.setDataTargetId(dataSourceId);
        condition.setRuleCondition(YesOrNoEnum.NO.getCode());
        List<ConditionDefinition> inputConditionDefinitions = this.conditionDefinitionService.list(condition);

        modelMap.put("inputConditionDefinitions", inputConditionDefinitions);
        modelMap.put("conditionDefinition", conditionDefinition);
        modelMap.put("conditionDataSource", conditionDataSource);
        modelMap.put("dataSourceColumns", dataSourceColumns);
        modelMap.put("params", params);
        return "conditionDefinition/ajaxTable";
    }
}