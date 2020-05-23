package com.dili.rule.controller;

import com.dili.rule.domain.ConditionDataSource;
import com.dili.rule.service.ConditionDataSourceService;
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
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 数据源管理控制层
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2020-05-13 11:19:20.
 */
@Controller
@RequestMapping("/conditionDataSource")
@Slf4j
public class ConditionDataSourceController {
    @Autowired
    private ConditionDataSourceService conditionDataSourceService;

    /**
     * 跳转到数据源管理首页面
     * @param modelMap
     * @return String
     */
    @RequestMapping(value="/index.html", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        return "conditionDataSource/list";
    }

    /**
     * 分页查询数据源列表信息
     * @param conditionDataSource
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/listPage.action", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String listPage(ConditionDataSource conditionDataSource, HttpServletRequest request) {
        try {
            return conditionDataSourceService.listEasyuiPageByExample(conditionDataSource, true).toString();
        } catch (Exception e) {
            log.error("查询数据源列表异常," + e.getMessage(), e);
            return new EasyuiPageOutput(0, Collections.emptyList()).toString();
        }
    }

    /**
     * 跳转到新增或修改页面
     * @param id 数据ID
     * @return
     */
    @RequestMapping(value = "/preSave.html", method = RequestMethod.GET)
    public String preSave(Long id, ModelMap modelMap) {
        if (Objects.nonNull(id)) {
            ConditionDataSource conditionDataSource = conditionDataSourceService.get(id);
            if (Objects.nonNull(conditionDataSource)) {
                modelMap.put("conditionDataSource", conditionDataSource);
            }
            return "conditionDataSource/edit";
        }
        return "conditionDataSource/add";
    }

    /**
     * 保存数据源信息
     * @param dataSource
     * @return
     */
    @RequestMapping(value = "/save.action", method = {RequestMethod.POST})
    @ResponseBody
    public BaseOutput save(ConditionDataSource dataSource) {
        if (Objects.isNull(dataSource)) {
            return BaseOutput.failure("参数丢失");
        }
        //数据库json 要么是json格式，要么null对象，，空字符串json解析会失败
        if ("".equals(dataSource.getDataJson())) {
            dataSource.setDataJson(null);
        }
        try {
            dataSource.setModifyTime(LocalDateTime.now());
            if (Objects.isNull(dataSource.getId())) {
                dataSource.setCreateTime(dataSource.getModifyTime());
            }
            conditionDataSourceService.saveOrUpdate(dataSource);
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
            conditionDataSourceService.delete(id);
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
    public BaseOutput<List<ConditionDataSource>> getDataSource() {
        return BaseOutput.success().setData(conditionDataSourceService.list(null));
    }

    /**
     * 跳转到数据源-查询框条件设置管理首页面
     * @param dataTargetId 查询框对应的目标数据源
     * @param modelMap
     * @return String
     */
    @RequestMapping(value="/queryInput.html", method = RequestMethod.GET)
    public String queryInput(Long dataTargetId,ModelMap modelMap) {
        modelMap.put("dataTargetId",dataTargetId);
        return "conditionDataSource/queryInputList";
    }

    /**
     * 跳转到数据源-查询框条件设置管理首页面
     * @param dataSourceId
     * @param modelMap
     * @return String
     */
    @RequestMapping(value="/queryInputList.action", method = RequestMethod.GET)
    public String queryInputList(Long dataSourceId,ModelMap modelMap) {
        modelMap.put("dataSourceId",dataSourceId);
        return "conditionDataSource/queryInputList";
    }

}