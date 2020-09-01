package com.dili.rule.controller;

import com.dili.rule.domain.DatasourceQueryConfig;
import com.dili.rule.service.DatasourceQueryConfigService;
import com.dili.ss.domain.BaseOutput;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 数据尖查询配置
 */
@Controller
@RequestMapping(value = "/datasourceQueryConfig")
public class DatasourceQueryConfigController {

    @Autowired
    DatasourceQueryConfigService datasourceQueryConfigService;
//
//    /**
//     * 跳转到数据源-查询框条件设置管理首页面
//     *
//     * @param dataSourceId
//     * @param modelMap
//     * @return String
//     */
//    @RequestMapping(value = "/queryInputList.action", method = RequestMethod.GET)
//    public String queryInputList(Long dataSourceId, ModelMap modelMap) {
//        modelMap.put("dataSourceId", dataSourceId);
//        return "datasourceQueryConfig/queryInputList";
//    }

    /**
     * 跳转到数据源-查询框条件设置管理首页面
     *
     * @param dataTargetId 查询框对应的目标数据源
     * @param modelMap
     * @return String
     */
    @RequestMapping(value = "/list.html", method = RequestMethod.GET)
    public String list(DatasourceQueryConfig query, ModelMap modelMap) {
        if (query.getDataSourceId() == null) {
            query.setDataSourceId(0L);
        }
        modelMap.put("query", query);
        return "datasourceQueryConfig/list";
    }

    /**
     * 跳转到数据源-查询框条件设置管理首页面
     *
     * @param dataTargetId 查询框对应的目标数据源
     * @param modelMap
     * @return String
     */
    @RequestMapping(value = "/preSave.html", method = RequestMethod.GET)
    public String preSave(DatasourceQueryConfig item, ModelMap modelMap) {
        if (item.getId() != null) {
            modelMap.put("item", this.datasourceQueryConfigService.get(item.getId()));
        } else {
            modelMap.put("item", item);
        }
        return "datasourceQueryConfig/edit";
    }

    /**
     * 跳转到数据源-查询框条件设置管理首页面
     *
     * @param dataTargetId 查询框对应的目标数据源
     * @param modelMap
     * @return String
     */
    @RequestMapping(value = "/listPage.action", method = RequestMethod.POST)
    @ResponseBody
    public String listPage(DatasourceQueryConfig query) throws Exception {
        if (query.getDataSourceId() == null) {
            query.setDataSourceId(0L);
        }
        return this.datasourceQueryConfigService.listEasyuiPageByExample(query, true).toString();
    }

    /**
     *保存(更新)数据源查询框配置
     */
    @RequestMapping(value = "/save.action")
    @ResponseBody
    public BaseOutput save(@RequestBody DatasourceQueryConfig input) {
        if (input.getId() != null) {
            this.datasourceQueryConfigService.update(input);
        } else {
            this.datasourceQueryConfigService.insertSelective(input);
        }
        return BaseOutput.success();
    }
    /**
     *删除数据源查询框配置
     */
    @RequestMapping(value = "/delete.action")
    @ResponseBody
    public BaseOutput delete(@RequestBody DatasourceQueryConfig input) {
        if (input.getId() != null) {
            this.datasourceQueryConfigService.delete(input.getId());
        } 
        return BaseOutput.success();
    }
}
