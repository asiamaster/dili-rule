package com.dili.rule.controller;

import com.alibaba.fastjson.JSON;
import com.dili.commons.glossary.YesOrNoEnum;
import com.dili.rule.domain.ConditionDefinition;
import com.dili.rule.domain.DataSourceColumn;
import com.dili.rule.domain.DataSourceDefinition;
import com.dili.rule.domain.DatasourceQueryConfig;
import com.dili.rule.domain.dto.ConditionAndDataSourceDefinitionDto;
import com.dili.rule.domain.dto.DataSourceDefinitionDto;
import com.dili.rule.domain.dto.RemoteAjaxInputDto;
import com.dili.rule.domain.enums.MatchTypeEnum;
import com.dili.rule.domain.enums.ViewModeEnum;
import com.dili.rule.service.ConditionDefinitionService;
import com.dili.rule.service.DataSourceColumnService;
import com.dili.rule.service.DataSourceDefinitionService;
import com.dili.rule.service.DatasourceQueryConfigService;
import com.dili.rule.service.remote.RemoteDataQueryService;
import com.dili.rule.utils.CookieUtil;
import com.dili.rule.utils.EasyuiPageOutputUtil;
import com.dili.rule.utils.FirmUtil;
import com.dili.ss.constant.ResultCode;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.domain.PageOutput;
import com.dili.uap.sdk.domain.Firm;
import com.dili.uap.sdk.session.SessionContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import one.util.streamex.StreamEx;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
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
import java.util.*;
import java.util.function.Function;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2020-05-13 11:23:41.
 */
@Controller
@RequestMapping("/conditionDefinition")
public class ConditionDefinitionController {

    private static final Logger log = LoggerFactory.getLogger(ConditionDefinitionController.class);
    @Autowired
    private ConditionDefinitionService conditionDefinitionService;
    @Autowired
    private DataSourceDefinitionService dataSourceDefinitionService;
    @Autowired
    private DataSourceColumnService dataSourceColumnService;
    @Autowired
    private RemoteDataQueryService remoteDataQueryService;
    @Autowired
    DatasourceQueryConfigService datasourceQueryConfigService;


    /**
     * 跳转到规则预定义首页面
     *
     * @param modelMap
     * @return String
     */
    @RequestMapping(value = "/index.html", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        return "conditionDefinition/list";
    }

    /**
     * 分页查询规则预定义列表信息
     *
     * @param conditionDefinition
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/listPage.action", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String listPage(ConditionDefinition conditionDefinition, HttpServletRequest request) {
        try {
            if(conditionDefinition.getMarketId()==null||StringUtils.isBlank(conditionDefinition.getBusinessType())){
                return EasyuiPageOutputUtil.build(0, Lists.newArrayList()).toString();
            }
            return conditionDefinitionService.listEasyuiPageByExample(conditionDefinition, true).toString();
        } catch (Exception e) {
            log.error("查询条件预定义列表异常," + e.getMessage(), e);
            return EasyuiPageOutputUtil.build(0, Collections.emptyList()).toString();
        }
    }

    /**
     * 跳转到编辑页面
     *
     * @param conditionDefinition 数据ID
     * @return
     */
    @RequestMapping(value = "/preSave.html", method = RequestMethod.GET)
    public String preSave(ConditionDefinition conditionDefinition, ModelMap modelMap) {
        if (Objects.nonNull(conditionDefinition.getId())) {
            conditionDefinition = conditionDefinitionService.get(conditionDefinition.getId());
        }
        modelMap.put("conditionDefinition", conditionDefinition);
        modelMap.put("matchTypeEnumList", MatchTypeEnum.values());
        return "conditionDefinition/edit";
    }


    /**
     * 保存预定义数据
     *
     * @param conditionDefinition
     * @return
     */
    @RequestMapping(value = "/save.action", method = {RequestMethod.POST})
    @ResponseBody
    public BaseOutput save(ConditionDefinition conditionDefinition) {
        return conditionDefinitionService.save(conditionDefinition);
    }

    /**
     * 删除数据列
     *
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
     * 跳转到编辑页面
     *
     * @param id 数据ID
     * @return
     */
    @RequestMapping(value = "/view.action", method = RequestMethod.GET)
    public String view(Long id, ModelMap modelMap) {
        if (Objects.nonNull(id)) {
            ConditionDefinition conditionDefinition = conditionDefinitionService.get(id);
            if (Objects.nonNull(conditionDefinition)) {
                modelMap.put("conditionDefinition", conditionDefinition);
            }
        }
        return "conditionDefinition/view";
    }

    /**
     * 根据数据定义获取条件信息并返回条件数据页
     *
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
     * 返回当前分页参数
     *
     * @param params
     * @return
     */
    private Integer getPageNumber(Map<String, Object> params) {
        Object pageObj = params.get("page");
        if (pageObj == null) {
            return 1;
        }
        if (!NumberUtils.isCreatable(String.valueOf(pageObj))) {
            return 1;
        }
        Integer page = NumberUtils.createBigInteger(String.valueOf(pageObj)).intValue();
        if (page <= 0) {
            return 1;
        }
        return page;
    }

    /**
     * 返回当前分页参数
     *
     * @param params
     * @return
     */
    private Integer getRows(Map<String, Object> params) {
        Object rowsObj = params.get("rows");
        if (rowsObj == null) {
            return 10;
        }
        if (!NumberUtils.isCreatable(String.valueOf(rowsObj))) {
            return 10;
        }
        Integer rows = NumberUtils.createBigInteger(String.valueOf(rowsObj)).intValue();
        if (rows <= 0) {
            return 10;
        }
        if (rows >= 100) {
            return 100;
        }
        return rows;
    }

    /**
     * SB
     *
     * @param dataSourceDefinition
     * @param params
     * @return
     */
    private Map<String, Object> addPageParams(DataSourceDefinition dataSourceDefinition, Map<String, Object> params) {
        if (YesOrNoEnum.YES.getCode().equals(dataSourceDefinition.getPaged())) {
            Integer page = this.getPageNumber(params);
            Integer rows = this.getRows(params);
            params.put("page", page);
            params.put("rows", rows);

        } else {
            params.put("page", 1);
            params.put("rows", Integer.MAX_VALUE);
        }
        return params;
    }

    /**
     * 获取动态条件数据
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "/popupPage.action", method = {RequestMethod.POST})
    public String popupPage(@RequestBody Map<String, Object> params, ModelMap modelMap, HttpServletRequest request) {

        Firm firm= FirmUtil.from(SessionContext.getSessionContext().getUserTicket());
        Long definitionId = Long.parseLong(params.remove("definitionId").toString());
        ConditionDefinition conditionDefinition = this.conditionDefinitionService.get(definitionId);
        Long dataSourceId = conditionDefinition.getDataSourceId();
        DataSourceDefinition dataSourceDefinition = dataSourceDefinitionService.get(dataSourceId);
        this.addPageParams(dataSourceDefinition, params);

        ViewModeEnum viewMode = ViewModeEnum.fromCode(conditionDefinition.getViewMode()).orElse(ViewModeEnum.TABLE_MULTI);

        DataSourceColumn columnCondition = new DataSourceColumn();
        columnCondition.setDataSourceId(dataSourceId);
        columnCondition.setVisible(YesOrNoEnum.YES.getCode());
        columnCondition.setOrder("ASC");
        columnCondition.setSort("column_index");
        List<DataSourceColumn> dataSourceColumns = dataSourceColumnService.listByExample(columnCondition);


        ConditionDefinition condition = new ConditionDefinition();
        condition.setDataTargetId(dataSourceId);
        condition.setRuleCondition(YesOrNoEnum.NO.getCode());
        List<ConditionDefinition> inputConditionDefinitions = this.conditionDefinitionService.list(condition);
        modelMap.put("inputConditionDefinitions", inputConditionDefinitions);
        modelMap.put("conditionDefinition", conditionDefinition);
        modelMap.put("dataSourceDefinition", dataSourceDefinition);
        modelMap.put("dataSourceColumns", dataSourceColumns);
        modelMap.put("params", params);
        modelMap.put("jsonParams", JSON.toJSONString(params));
        modelMap.put("dataSourceId", dataSourceId);
        String uapSessionId = CookieUtil.getUapSessionId(request);
        switch (viewMode) {
            case TABLE_MULTI:

                List<DatasourceQueryConfig> queryConfigList = datasourceQueryConfigService.findByDataSourceId(dataSourceId);
                PageOutput<List> pagedData = this.remoteDataQueryService.queryData(dataSourceDefinition, params, uapSessionId,firm);
                modelMap.put("pagedData", pagedData);
                modelMap.put("queryConfigList", queryConfigList);
                return "conditionDefinition/dynamic/table";
            case TREE_MULTI:
                ObjectMapper mapper = new ObjectMapper();
                try {
//                    PageOutput<List> treeData = this.remoteDataQueryService.queryData(dataSourceDefinition, params, uapSessionId);
//                    modelMap.put("nodes", mapper.writeValueAsString(treeData.getData()));
                    DataSourceColumn displayColumn = StreamEx.of(dataSourceColumns).filter(item -> YesOrNoEnum.YES.getCode().equals(item.getDisplay())).findFirst().orElse(new DataSourceColumn());
                    modelMap.put("displayColumn", mapper.writeValueAsString(displayColumn));
                    modelMap.put("parentColumn", conditionDefinition.getParentColumn());
                } catch (JsonProcessingException e) {
//                    modelMap.put("nodes", "[]");
                    modelMap.put("displayColumn", "{}");
                }

                return "conditionDefinition/dynamic/tree";

            default:
                return "conditionDefinition/dynamic/empty";
        }

        // return "conditionDefinition/ajaxTable";
        //return "conditionDefinition/dynamic/tree";
    }

    /**
     * SB
     *
     * @param inputDto
     * @param request
     * @return
     */
    @RequestMapping("/queryRemoteData.action")
    @ResponseBody
    public BaseOutput queryRemoteData(@RequestBody RemoteAjaxInputDto inputDto, HttpServletRequest request) {
        try {
            String uapSessionId = CookieUtil.getUapSessionId(request);
            Firm firm= FirmUtil.from(SessionContext.getSessionContext().getUserTicket());
            DataSourceDefinition dataSourceDefinition = dataSourceDefinitionService.get(inputDto.getDataSourceId());
            PageOutput<List> pagedData = this.remoteDataQueryService.queryData(dataSourceDefinition, inputDto.getParams(), uapSessionId,firm);
            return pagedData;
        } catch (Exception e) {
            return BaseOutput.failure("远程查询错误");

        }

    }

    /**
     * 查询所有IN的条件定义
     *
     * @param query
     * @return
     */
    @RequestMapping("/queryConditionDefinitionList.action")
    @ResponseBody
    public BaseOutput<Object> queryInConditionDefinitionList(@RequestBody ConditionDefinition query) {
        if (query == null || query.getMarketId() == null || StringUtils.isBlank(query.getBusinessType())) {
            return BaseOutput.success();
        }

        ConditionDefinition q = new ConditionDefinition();
        q.setMarketId(query.getMarketId());
        q.setBusinessType(query.getBusinessType().trim());
        q.setMatchType(MatchTypeEnum.IN.getCode());
        List<ConditionDefinition> list = this.conditionDefinitionService.listByExample(q);
        Map<Long, DataSourceDefinition> dataSourceDefinitionMap = this.findDataSourceDefinitionByIdList(list);
        List<ConditionAndDataSourceDefinitionDto> retList = StreamEx.of(list).filter(conditionDefinition -> {
            return dataSourceDefinitionMap.containsKey(conditionDefinition.getDataSourceId());
        }).map(conditionDefinition -> {
            ConditionAndDataSourceDefinitionDto dto = new ConditionAndDataSourceDefinitionDto();
            dto.setConditionDefinition(conditionDefinition);
            dto.setDataSourceDefinition(dataSourceDefinitionMap.get(conditionDefinition.getDataSourceId()));
            DataSourceColumn dcq=new DataSourceColumn();
            dcq.setDataSourceId(conditionDefinition.getDataSourceId());
            dcq.setDisplay(YesOrNoEnum.YES.getCode());
            List<String>displayColumnCodeList=StreamEx.of(this.dataSourceColumnService.listByExample(dcq)).map(DataSourceColumn::getColumnCode).toList();
            dto.setDisplayColumnCodeList(displayColumnCodeList);
            if(displayColumnCodeList.isEmpty()){
                return null;
            }
            return dto;

        }).nonNull().toList();


        return BaseOutput.successData(retList);

    }

    /**
     * 查询数据源定义
     *
     * @param list
     * @return
     */
    private Map<Long, DataSourceDefinition> findDataSourceDefinitionByIdList(List<ConditionDefinition> list) {
        List<Long> dataSourceIdList = StreamEx.of(list).map(item -> item.getDataSourceId()).nonNull().toList();
        if (dataSourceIdList.isEmpty()) {
            return Maps.newHashMap();
        }
        DataSourceDefinitionDto query = new DataSourceDefinitionDto();
        query.setIdList(dataSourceIdList);
        List<DataSourceDefinition> dataSourceDefinitionList = this.dataSourceDefinitionService.listByExample(query);

        return StreamEx.of(dataSourceDefinitionList).filter(ds -> {
            return StringUtils.isNotBlank(ds.getAutoCompleteQueryKey());
        }).toMap(DataSourceDefinition::getId, Function.identity());
    }
}
