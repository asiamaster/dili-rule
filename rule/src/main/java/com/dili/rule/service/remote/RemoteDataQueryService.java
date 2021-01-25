package com.dili.rule.service.remote;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.dili.commons.glossary.YesOrNoEnum;
import com.dili.rule.domain.DataSourceDefinition;
import com.dili.rule.domain.enums.DataSourceTypeEnum;
import com.dili.ss.domain.PageOutput;
import com.dili.uap.sdk.session.SessionContext;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <B></B>
 * <B>Copyright:本软件源代码版权归农丰时代科技有限公司及其研发团队所有,未经许可不得任意复制与传播.</B>
 * <B>农丰时代科技有限公司</B>
 *
 * @author yuehongbo
 * @date 2020/5/20 11:39
 */
@Service
public class RemoteDataQueryService {

    private static final Logger logger = LoggerFactory.getLogger(RemoteDataQueryService.class);

    /**
     * 通过url请求远程接口(http+json)
     *
     * @param url
     * @return
     */
    public PageOutput<List> queryData(String url) {
        return this.queryData(url, Collections.emptyMap(), Collections.emptyMap());
    }

    /**
     * 通过url和params请求远程接口(http+json)
     *
     * @param url
     * @param params
     * @param header
     * @param page
     * @param rows
     * @return
     */
    public PageOutput<List> queryData(String url, Map<String, Object> params, Map<String, String> header) {
        Optional<String> jsonDataOpt = this.remoteQuery(url, this.trimParamsMap(params), header);
        PageOutput<List> pageout = this.parseJson(jsonDataOpt, false);
        return pageout;
    }

    /**
     * 通过DataSourceDefinition.queeryUrl对应的的url和params请求远程接口(http+json)
     *
     * @param dataSourceDefinition
     * @param params
     * @param sessionId
     * @return
     */
    public PageOutput<List> queryData(DataSourceDefinition dataSourceDefinition, Map<String, Object> params, String sessionId) {

        Optional<String> jsonDataOpt = this.queryJsonData(dataSourceDefinition, params, sessionId);
        PageOutput<List> pageout = this.parseJson(jsonDataOpt, YesOrNoEnum.YES.getCode().equals(dataSourceDefinition.getPaged()));
        return pageout;
    }

    /**
     * 创建headermap
     *
     * @param sessionId
     * @return
     */
    private Map<String, String> buildHeaderMap(String sessionId) {

        Map<String, String> header = new HashMap<>();
        header.put("sessionId", sessionId);
        header.put("UAP_SessionId", sessionId);
        return header;
    }

    /**
     * 远程请求或者本地返回数据转换为json数据结构
     *
     * @param dataSourceDefinition
     * @param params
     * @return
     */
    private Optional<String> queryJsonData(DataSourceDefinition dataSourceDefinition, Map<String, Object> params, String sessionId) {
        DataSourceTypeEnum dataSourceType = DataSourceTypeEnum.getInitDataMaps().get(dataSourceDefinition.getDataSourceType());
        if (StrUtil.isNotBlank(dataSourceDefinition.getQueryCondition())) {
            params.putAll(JSONObject.parseObject(dataSourceDefinition.getQueryCondition()));
        }
        if (DataSourceTypeEnum.LOCAL == dataSourceType) {
            String localJsonData = dataSourceDefinition.getDataJson();
            return Optional.ofNullable(localJsonData);
        } else {
            return this.remoteQuery(dataSourceDefinition.getQueryUrl(), params, this.buildHeaderMap(sessionId));
        }
    }

    /**
     * 通过DataSourceDefinition.queeryUrl对应的的url和keys列表参数请求远程接口(http+json)
     *
     * @param dataSourceDefinition
     * @param keys
     * @return
     */
    public List<Map<String, Object>> queryKeys(DataSourceDefinition dataSourceDefinition, List<Object> keys, String sessionId) {
        if (CollectionUtil.isEmpty(keys)) {
            return Collections.emptyList();
        }
        DataSourceTypeEnum dataSourceType = DataSourceTypeEnum.getInitDataMaps().get(dataSourceDefinition.getDataSourceType());
        if (DataSourceTypeEnum.LOCAL == dataSourceType) {
            String localJsonData = dataSourceDefinition.getDataJson();
            return this.parseJson(Optional.ofNullable(localJsonData), false).getData();
        } else {
            //构建查询参数
            Map<String, Object> params = Maps.newHashMap();
            if (StrUtil.isNotBlank(dataSourceDefinition.getQueryCondition())) {
                params.putAll(JSONObject.parseObject(dataSourceDefinition.getQueryCondition()));
            }
            if (StrUtil.isNotBlank(dataSourceDefinition.getKeysField())) {
                params.put(dataSourceDefinition.getKeysField(), keys);
            }
            //强制内置两个参数，根据当前用户的市场隔离
            params.put("firmCode", SessionContext.getSessionContext().getUserTicket().getFirmCode());
            params.put("marketId", SessionContext.getSessionContext().getUserTicket().getFirmId());
            params.put("firmId", SessionContext.getSessionContext().getUserTicket().getFirmId());
            String keyUrl = dataSourceDefinition.getKeysUrl();
            String jsonBody = JSONObject.toJSONString(params);
            logger.info("keyUrl={},data={}", keyUrl, jsonBody);
            try (HttpResponse response = HttpUtil.createPost(keyUrl).addHeaders(this.buildHeaderMap(sessionId)).body(jsonBody).execute();) {
                if (response.isOk()) {
                    return this.parseJson(Optional.ofNullable(response.body()), false).getData();
                }
            }catch (Exception e) {
            	logger.error(e.getMessage(),e);
            	return Collections.emptyList();
			}
        }
        return Collections.emptyList();
    }

    /**
     * 去掉key为null的entry,并且如果value为String类型,去掉前后空格
     *
     * @param params
     * @return
     */
    private Map<String, Object> trimParamsMap(Map<String, Object> params) {
        if (CollectionUtils.isEmpty(params)) {
            return new HashMap<>();
        }
        Map<String, Object> resultMap = params.entrySet().stream().filter(e -> Objects.nonNull(e.getKey())).map(e -> {
            String key = e.getKey();
            Object value = e.getValue();
            MutablePair<String, Object> pair = new MutablePair<>(key, value);
            if (value instanceof String) {
                Object v = StringUtils.trimToEmpty(String.valueOf(value));
                pair.setValue(v);
            }
            return pair;
        }).collect(Collectors.toMap(MutablePair::getKey, MutablePair::getValue));
        return resultMap;
    }

    /**
     * 远程查询，并返回对应的responseBody
     *
     * @param url
     * @param params
     * @return
     */
    private Optional<String> remoteQuery(String url, Map<String, Object> params, Map<String, String> headerMap) {

        //强制内置两个参数，根据当前用户的市场隔离
        params.put("firmCode", SessionContext.getSessionContext().getUserTicket().getFirmCode());
        params.put("marketId", SessionContext.getSessionContext().getUserTicket().getFirmId());
        params.put("firmId", SessionContext.getSessionContext().getUserTicket().getFirmId());

        String jsonBody = JSONObject.toJSONString(params);
        logger.info("url={},  data={},  headerMap={}", url, jsonBody, headerMap);
        try (HttpResponse response = HttpUtil.createPost(url).addHeaders(headerMap).body(jsonBody).execute();) {
            if (response.isOk()) {
                return Optional.ofNullable(response.body());
            } else {
                logger.error("远程请求出错,status: {}", response.getStatus());
            }
        }
        return Optional.empty();
    }

    /**
     * 转换结果对象
     *
     * @param jsonDataOpt
     * @param paged
     * @return
     */
    private PageOutput<List> parseJson(Optional<String> jsonDataOpt, Boolean paged) {
        PageOutput<List> output = PageOutput.failure();
        if (jsonDataOpt.isPresent()) {
            String json = jsonDataOpt.get();
            try {
                output = JSONObject.parseObject(json, PageOutput.class);
            } catch (Exception e) {
                logger.error("解析json:" + json + " 出错 ", e);
            }
        }
        return output;
    }
}
