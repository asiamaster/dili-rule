package com.dili.rule.controller;

import com.dili.rule.domain.DataSourceColumn;
import com.dili.rule.service.DataSourceColumnService;
import com.dili.ss.constant.ResultCode;
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
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2020-05-13 11:19:57.
 */
@Controller
@RequestMapping("/dataSourceColumn")
@Slf4j
public class DataSourceColumnController {
    @Autowired
    private DataSourceColumnService dataSourceColumnService;

    /**
     * 跳转到数据源列管理首页面
     * @param dataSourceId 数据源ID
     * @param modelMap
     * @return String
     */
    @RequestMapping(value="/index.html", method = RequestMethod.GET)
    public String index(Long dataSourceId,ModelMap modelMap) {
        modelMap.put("dataSourceId",dataSourceId);
        return "dataSourceColumn/list";
    }

    /**
     * 分页查询某数据源种的列的数据列表信息
     * @param dataSourceColumn
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/listPage.action", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String listPage(DataSourceColumn dataSourceColumn, HttpServletRequest request) {
        if (Objects.isNull(dataSourceColumn) || Objects.isNull(dataSourceColumn.getDataSourceId())) {
            log.warn("查询数据列时,数据源参数丢失");
            return new EasyuiPageOutput(0, Collections.emptyList()).toString();
        }
        try {
            return dataSourceColumnService.listEasyuiPageByExample(dataSourceColumn, true).toString();
        } catch (Exception e) {
            log.error(String.format("查询数据源[%d]对应的数据列时异常[%s]", dataSourceColumn.getDataSourceId(), e.getMessage()), e);
            return new EasyuiPageOutput(0, Collections.emptyList()).toString();
        }
    }

    /**
     * 进入数据列预编辑页面
     * @param dataSourceId
     * @param id
     * @param modelMap
     * @return
     */
    @RequestMapping(value = "/preSave.html", method = {RequestMethod.GET})
    public String preSave(Long dataSourceId, Long id, ModelMap modelMap) {
        DataSourceColumn dataSourceColumn = new DataSourceColumn();
        dataSourceColumn.setDataSourceId(dataSourceId);
        if (id != null) {
            dataSourceColumn = dataSourceColumnService.get(id);
        }
        modelMap.put("dataSourceColumn", dataSourceColumn);
        return "dataSourceColumn/edit";
    }

    /**
     * 保存数据源的列信息
     * @param dataSourceColumn
     * @return
     */
    @RequestMapping(value = "/save.action", method = {RequestMethod.POST})
    @ResponseBody
    public BaseOutput save(DataSourceColumn dataSourceColumn) {
        if (Objects.isNull(dataSourceColumn)) {
            return BaseOutput.failure("参数丢失");
        }
        try {
            dataSourceColumn.setModifyTime(LocalDateTime.now());
            if (Objects.isNull(dataSourceColumn.getId())) {
                dataSourceColumn.setCreateTime(dataSourceColumn.getModifyTime());
            }
            dataSourceColumnService.saveOrUpdate(dataSourceColumn);
            return BaseOutput.success();
        } catch (Exception e) {
            log.error(String.format("保存数据列信息[$s]出现异常:[%s]", dataSourceColumn.toString(), e.getMessage()), e);
            return BaseOutput.failure();
        }
    }

    /**
     * 删除数据列
     * @param id 数据列ID
     * @return
     */
    @RequestMapping(value = "/doDelete.action", method = {RequestMethod.POST})
    @ResponseBody
    public BaseOutput<Object> delete(Long id) {
        if (Objects.isNull(id)) {
            return BaseOutput.failure("参数丢失").setCode(ResultCode.PARAMS_ERROR);
        }
        try {
            dataSourceColumnService.delete(id);
            return BaseOutput.success("删除成功");
        } catch (Exception e) {
            log.error(String.format("删除数据列[%d]异常:[%s]", id, e.getMessage()), e);
            return BaseOutput.failure();
        }
    }

    /**
     * 根据数据源获取数据列信息
     * @param dataSourceId 数据源ID
     * @return
     */
    @RequestMapping(value = "/getByDataSource.action")
    @ResponseBody
    public BaseOutput<List<DataSourceColumn>> getInfoByDataSource(Long dataSourceId) {
        //数据源列，必须所属某数据源，所以，所属数据源为必传项
        if (Objects.isNull(dataSourceId)) {
            return BaseOutput.failure("参数丢失");
        }
        DataSourceColumn query = new DataSourceColumn();
        query.setDataSourceId(dataSourceId);
        return BaseOutput.success().setData(dataSourceColumnService.list(query));
    }
}