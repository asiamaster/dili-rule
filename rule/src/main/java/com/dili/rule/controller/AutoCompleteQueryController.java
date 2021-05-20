package com.dili.rule.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.dili.rule.domain.DataSourceDefinition;
import com.dili.rule.domain.dto.AutoCompleteQueryDto;
import com.dili.rule.domain.enums.DataSourceTypeEnum;
import com.dili.rule.service.DataSourceDefinitionService;
import com.dili.rule.service.remote.RemoteDataQueryService;
import com.dili.rule.utils.CookieUtil;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.PageOutput;
import com.google.common.collect.Maps;
import one.util.streamex.StreamEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 联想输入查询
 */
@Controller
@RequestMapping("/autoCompleteQuery")
public class AutoCompleteQueryController {
    private static final Logger logger = LoggerFactory.getLogger(AutoCompleteQueryController.class);

    @Autowired
    RemoteDataQueryService remoteDataQueryService;
    @Autowired
    DataSourceDefinitionService dataSourceDefinitionService;


    /**
     * 进行查询
     *
     * @param query
     * @return
     */
    @RequestMapping("/autoQuery.action")
    @ResponseBody
    public BaseOutput autoQuery(@RequestBody AutoCompleteQueryDto query, HttpServletRequest request) {
        String uapSessionId = CookieUtil.getUapSessionId(request);
        DataSourceTypeEnum dataSourceTypeEnum = DataSourceTypeEnum.getInstance(query.getDataSourceType());
        if (dataSourceTypeEnum == null) {
            return BaseOutput.success();
        }
        try {
            DataSourceDefinition dataSourceDefinition = this.dataSourceDefinitionService.get(query.getDataSourceDefinitionId());
            if (dataSourceDefinition == null) {
                return BaseOutput.success();
            }
            Optional<String> dataJson = Optional.ofNullable(dataSourceDefinition.getDataJson());
            Map<String, Object> params = Maps.newHashMap();
            try {
                if (StrUtil.isNotBlank(dataSourceDefinition.getQueryCondition())) {
                    params.putAll(JSONObject.parseObject(dataSourceDefinition.getQueryCondition()));
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return BaseOutput.failure("查询错误");
            }
            params.put("page", 1);
            params.put("rows", 20);
            params.put(query.getAutoCompleteQueryKey(), query.getQuery());
            PageOutput<List> listPageOutput = this.remoteDataQueryService.autoCompleteQueryData(dataSourceTypeEnum, query, dataJson, params, uapSessionId);
            return listPageOutput;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return BaseOutput.failure("查询错误");
        }

    }
}
