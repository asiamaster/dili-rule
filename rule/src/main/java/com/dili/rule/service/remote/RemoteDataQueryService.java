package com.dili.rule.service.remote;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.dili.commons.glossary.YesOrNoEnum;
import com.dili.rule.domain.DataSourceDefinition;
import com.dili.rule.domain.dto.AutoCompleteQueryDto;
import com.dili.rule.domain.enums.DataSourceTypeEnum;
import com.dili.rule.utils.RxUtils;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.PageOutput;
import com.dili.uap.sdk.constant.SessionConstants;
import com.dili.uap.sdk.domain.Firm;
import com.dili.uap.sdk.session.SessionContext;
import com.google.common.collect.Maps;
import okhttp3.*;
import one.util.streamex.StreamEx;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
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
     * 通过DataSourceDefinition.queeryUrl对应的的url和params请求远程接口(http+json)
     *
     * @param dataSourceDefinition
     * @param params
     * @param sessionId
     * @return
     */
    public PageOutput<List> queryData(DataSourceDefinition dataSourceDefinition, Map<String, Object> params, String sessionId, Firm firm) {

        Optional<String> jsonDataOpt = this.queryJsonData(dataSourceDefinition, params, sessionId, firm);
        PageOutput<List> pageout = this.parseJson(jsonDataOpt);
        return pageout;
    }

    /**
     * 联想输入
     *
     * @param queryDto
     * @param dataSourceTypeEnum
     * @param params
     * @param sessionId
     * @return
     */
    public PageOutput<List> autoCompleteQueryData(DataSourceTypeEnum dataSourceTypeEnum, AutoCompleteQueryDto queryDto, Optional<String> dataJson, Map<String, Object> params, String sessionId, Firm firm) {

        Optional<String> jsonDataOpt = StreamEx.ofNullable(queryDto).map(dto -> {
            if (DataSourceTypeEnum.LOCAL == dataSourceTypeEnum) {
                return dataJson;
            } else {
                return this.remoteQuery(queryDto.getQueryUrl(), params, this.buildHeaderMap(sessionId), firm);
            }
        }).filter(Optional::isPresent).map(Optional::get).findFirst();

        PageOutput<List> pageout = this.parseJson(jsonDataOpt);
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
        logger.info("sessionId={}", sessionId);
        header.put(SessionConstants.ACCESS_TOKEN_KEY, sessionId);
        return header;
    }

    /**
     * 远程请求或者本地返回数据转换为json数据结构
     *
     * @param dataSourceDefinition
     * @param params
     * @return
     */
    private Optional<String> queryJsonData(DataSourceDefinition dataSourceDefinition, Map<String, Object> params, String sessionId, Firm firm) {
        DataSourceTypeEnum dataSourceType = DataSourceTypeEnum.getInitDataMaps().get(dataSourceDefinition.getDataSourceType());
        if (StrUtil.isNotBlank(dataSourceDefinition.getQueryCondition())) {
            params.putAll(JSONObject.parseObject(dataSourceDefinition.getQueryCondition()));
        }
        if (DataSourceTypeEnum.LOCAL == dataSourceType) {
            String localJsonData = dataSourceDefinition.getDataJson();
            return Optional.ofNullable(localJsonData);
        } else {
            return this.remoteQuery(dataSourceDefinition.getQueryUrl(), params, this.buildHeaderMap(sessionId), firm);
        }
    }

    /**
     * 通过DataSourceDefinition.queeryUrl对应的的url和keys列表参数请求远程接口(http+json)
     *
     * @param dataSourceDefinition
     * @param keys
     * @return
     */
    public List<Map<String, Object>> queryKeys(DataSourceDefinition dataSourceDefinition, List<Object> keys, String sessionId, Firm firm) {
        if (CollectionUtil.isEmpty(keys)) {
            return Collections.emptyList();
        }
        DataSourceTypeEnum dataSourceType = DataSourceTypeEnum.getInitDataMaps().get(dataSourceDefinition.getDataSourceType());
        if (DataSourceTypeEnum.LOCAL == dataSourceType) {
            String localJsonData = dataSourceDefinition.getDataJson();
            return this.parseJson(Optional.ofNullable(localJsonData)).getData();
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
            params.put("firmCode", firm.getCode());
            params.put("marketId", firm.getId());
            params.put("firmId", firm.getId());
            String keyUrl = dataSourceDefinition.getKeysUrl();
            String jsonBody = JSONObject.toJSONString(params);
            logger.info("keyUrl={},data={}", keyUrl, jsonBody);


            try {
                String responseText = this.postJson(keyUrl, this.buildHeaderMap(sessionId), jsonBody);
                return this.parseJson(Optional.ofNullable(responseText)).getData();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return Collections.emptyList();
            }
        }
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
    private Optional<String> remoteQuery(String url, Map<String, Object> params, Map<String, String> headerMap, Firm firm) {

        //强制内置两个参数，根据当前用户的市场隔离
        params.put("firmCode", firm.getCode());
        params.put("marketId", firm.getId());
        params.put("firmId", firm.getId());

        String jsonBody = JSONObject.toJSONString(params);
        logger.info("url={},  data={},  headerMap={}", url, jsonBody, headerMap);

        return Optional.ofNullable(this.postJson(url, headerMap, jsonBody));

    }

    /**
     * 转换结果对象
     *
     * @param jsonDataOpt
     * @return
     */
    private PageOutput<List> parseJson(Optional<String> jsonDataOpt) {
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

    /**
     * 远程请求
     *
     * @param url
     * @param headeMap
     * @param jsonString
     * @return
     */
    private String postJson(String url, Map<String, String> headeMap, String jsonString
    ) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonString);
        final Request request = new Request.Builder()
                .url(url)
                .headers(Headers.of(headeMap))
                .post(body)
                .build();
        try {
            Call call = this.getOkHttpClient().newCall(request);

            Response resp = call.execute();
            if (resp.isSuccessful()) {
                byte[] bodyBytes = resp.body().bytes();

                String responseText = new String(bodyBytes, StandardCharsets.UTF_8);
                return responseText;
            } else {
                logger.error("url={},resp code={},{}", url, resp.code(), resp.isRedirect());

            }
        } catch (Exception e) {
            logger.error("url=" + url, e);

        }
        return null;
    }

    /**
     * 获得httpclient
     *
     * @return
     */
    private synchronized OkHttpClient getOkHttpClient() {
        if (this.okHttpClient != null) {
            return this.okHttpClient;
        }
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.followRedirects(true).followSslRedirects(true);
        clientBuilder.connectTimeout(Duration.ofSeconds(10));
        clientBuilder.readTimeout(Duration.ofSeconds(30));
        clientBuilder.writeTimeout(Duration.ofSeconds(10));
        clientBuilder.sslSocketFactory(RxUtils.createSSLSocketFactory(), new RxUtils.TrustAllManager());
        clientBuilder.hostnameVerifier(new RxUtils.TrustAllHostnameVerifier());
        // 连接池实例
        ConnectionPool connectionPool = new ConnectionPool(5, 100, TimeUnit.SECONDS);
        clientBuilder.connectionPool(connectionPool);
        this.okHttpClient = clientBuilder.build();
        return this.okHttpClient;
    }

    private OkHttpClient okHttpClient;
}
