package com.dili.rule.controller;

import com.dili.rule.domain.DataSourceDefinition;
import com.dili.rule.domain.enums.DataSourceTypeEnum;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import com.dili.rule.service.DataSourceDefinitionService;
import com.dili.rule.utils.EasyuiPageOutputUtil;

/**
 * 数据源管理控制层
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2020-05-13 11:19:20.
 */
@Controller
@RequestMapping("/dataSourceDefinition")
public class DataSourceDefinitionController {
	private static final Logger log=LoggerFactory.getLogger(DataSourceDefinitionController.class);
    @Autowired
    private DataSourceDefinitionService dataSourceDefinitionService;

    /**
     * 跳转到数据源管理首页面
     * @param modelMap
     * @return String
     */
    @RequestMapping(value="/index.html", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        return "dataSourceDefinition/list";
    }

    /**
     * 分页查询数据源列表信息
     * @param dataSourceDefinition
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/listPage.action", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String listPage(DataSourceDefinition dataSourceDefinition, HttpServletRequest request) {
        try {
            return dataSourceDefinitionService.listEasyuiPageByExample(dataSourceDefinition, true).toString();
        } catch (Exception e) {
            log.error("查询数据源列表异常," + e.getMessage(), e);
            return EasyuiPageOutputUtil.build(0, Collections.emptyList()).toString();
        }
    }

    /**
     * 跳转到新增或修改页面
     * @param id 数据ID
     * @return
     */
    @RequestMapping(value = "/preSave.html", method = RequestMethod.GET)
    public String preSave(Long id, ModelMap modelMap) {

        modelMap.put("dataSourceTypeEnumList",DataSourceTypeEnum.values());
        if (Objects.nonNull(id)) {
            DataSourceDefinition dataSourceDefinition = dataSourceDefinitionService.get(id);
            if (Objects.nonNull(dataSourceDefinition)) {
                modelMap.put("dataSourceDefinition", dataSourceDefinition);
            }
            return "dataSourceDefinition/edit";
        }
        return "dataSourceDefinition/add";
    }

    /**
     * 保存数据源信息
     * @param dataSource
     * @return
     */
    @RequestMapping(value = "/save.action", method = {RequestMethod.POST})
    @ResponseBody
    public BaseOutput save(@RequestBody DataSourceDefinition dataSource) {
        if (Objects.isNull(dataSource)) {
            return BaseOutput.failure("参数丢失");
        }
        //数据库json 要么是json格式，要么null对象，，空字符串json解析会失败
        if ("".equals(dataSource.getDataJson())) {
            dataSource.setDataJson(null);
        }
        if ("".equals(dataSource.getQueryCondition())) {
            dataSource.setQueryCondition(null);
        }
        try {
            dataSource.setModifyTime(LocalDateTime.now());
            if (Objects.isNull(dataSource.getId())) {
                dataSource.setCreateTime(dataSource.getModifyTime());
            }
            dataSourceDefinitionService.saveOrUpdate(dataSource);
            return BaseOutput.success();
        } catch (Exception e) {
            log.error(String.format("保存数据源信息[%s]出现异常:[%s]", dataSource.toString(), e.getMessage()), e);
            return BaseOutput.failure();
        }
    }

    /**
     * 删除数据源
     * @param id 数据源ID
     * @return
     */
    @RequestMapping(value = "/doDelete.action", method = {RequestMethod.POST})
    @ResponseBody
    public BaseOutput<Object> delete(Long id) {
        try {
            dataSourceDefinitionService.delete(id);
            return BaseOutput.success("删除成功");
        } catch (Exception e) {
            log.error(String.format("删除数据源[%d]异常:[%s]", id, e.getMessage()), e);
            return BaseOutput.failure();
        }
    }

    /**
     * 根据系统编码及业务类型获取数据源信息
     * @return
     */
    @RequestMapping(value = "/getDataSource.action", method = {RequestMethod.POST})
    @ResponseBody
    public BaseOutput<List<DataSourceDefinition>> getDataSource() {
        return BaseOutput.success().setData(dataSourceDefinitionService.list(null));
    }



}