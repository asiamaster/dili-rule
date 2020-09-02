package com.dili.rule.service.remote;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.dili.commons.glossary.YesOrNoEnum;
import com.dili.rule.domain.ConditionDataSource;
import com.dili.rule.domain.enums.DataSourceTypeEnum;
import com.dili.ss.domain.PageOutput;
import com.dili.uap.sdk.session.SessionContext;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
	private static final Logger logger=LoggerFactory.getLogger(RemoteDataQueryService.class);
    /**
     * 通过url请求远程接口(http+json)
     * @param url
     * @return
     */
    public Page<Map<String, Object>> queryData(String url) {
        return this.queryData(url, Collections.emptyMap(), Collections.emptyMap());
    }

    /**
     * 通过url和params请求远程接口(http+json)
     *
     * @param url
     * @param params
     * @return
     */
    public Page<Map<String, Object>> queryData(String url, Map<String, Object> params,Map<String, String> header) {
        Optional<String> jsonDataOpt = this.remoteQuery(url, this.trimParamsMap(params),header);
        Page<Map<String, Object>> page = this.parseJson(jsonDataOpt, false);
        return page;
    }

    /**
     * 通过ConditionDataSource.queeryUrl对应的的url和params请求远程接口(http+json)
     *
     * @param conditionDataSource
     * @param params
     * @return
     */
    public Page<Map<String, Object>> queryData(ConditionDataSource conditionDataSource, Map<String, Object> params, Map<String, String> header) {
        Optional<String> jsonDataOpt = this.queryJsonData(conditionDataSource, params,header);
        Page<Map<String, Object>> page = this.parseJson(jsonDataOpt, YesOrNoEnum.YES.getCode().equals(conditionDataSource.getPaged()));
        return page;
    }


    /**
     * 远程请求或者本地返回数据转换为json数据结构
     * @param conditionDataSource
     * @param params
     * @return 
     */
    private Optional<String> queryJsonData(ConditionDataSource conditionDataSource, Map<String, Object> params, Map<String, String> header) {
        DataSourceTypeEnum dataSourceType = DataSourceTypeEnum.getInitDataMaps().get(conditionDataSource.getDataSourceType());
        if (StrUtil.isNotBlank(conditionDataSource.getQueryCondition())) {
            params.putAll(JSONObject.parseObject(conditionDataSource.getQueryCondition()));
        }
        if (DataSourceTypeEnum.LOCAL == dataSourceType) {
            String localJsonData = conditionDataSource.getDataJson();
            return Optional.ofNullable(localJsonData);
        } else {
            return this.remoteQuery(conditionDataSource.getQueryUrl(), params,header);
        }
    }
    
    
    /**
     * 通过ConditionDataSource.queeryUrl对应的的url和keys列表参数请求远程接口(http+json)
     *
     * @param conditionDataSource
     * @param keys
     * @return
     */
    public List<Map<String, Object>> queryKeys(ConditionDataSource conditionDataSource, List<Object> keys, Map<String, String> header) {
        if (CollectionUtil.isEmpty(keys)) {
            return Collections.emptyList();
        }
        DataSourceTypeEnum dataSourceType = DataSourceTypeEnum.getInitDataMaps().get(conditionDataSource.getDataSourceType());
        if (DataSourceTypeEnum.LOCAL == dataSourceType) {
            String localJsonData = conditionDataSource.getDataJson();
            return this.parseJson( Optional.ofNullable(localJsonData), false).getContent();
        } else {
            //构建查询参数
            Map<String, Object> params = Maps.newHashMap();
            if (StrUtil.isNotBlank(conditionDataSource.getQueryCondition())) {
                params.putAll(JSONObject.parseObject(conditionDataSource.getQueryCondition()));
            }
            if (StrUtil.isNotBlank(conditionDataSource.getKeysField())){
                params.put(conditionDataSource.getKeysField(), keys);
            }
            //强制内置两个参数，根据当前用户的市场隔离
            params.put("firmCode", SessionContext.getSessionContext().getUserTicket().getFirmCode());
            params.put("marketId", SessionContext.getSessionContext().getUserTicket().getFirmId());
            params.put("firmId", SessionContext.getSessionContext().getUserTicket().getFirmId());
            String keyUrl=conditionDataSource.getKeysUrl();
            String jsonBody=JSONObject.toJSONString(params);
            logger.info("keyUrl={},data={}",keyUrl,jsonBody);
            try(HttpResponse response = HttpUtil.createPost(keyUrl).addHeaders(header).body(jsonBody).execute();){
                if (response.isOk()) {
                    return   this.parseJson(Optional.ofNullable(response.body()), false).getContent();
                }
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
            MutablePair<String, Object> pair = new MutablePair<String, Object>(key, value);
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
     * @param url
     * @param params
     * @return
     */
    private Optional<String> remoteQuery(String url, Map<String, Object> params, Map<String, String> headerMap) {
        Integer pageNumber = 0;
        Integer pageSize = 10;
        if (!CollectionUtils.isEmpty(params)) {
            try {
                pageNumber = Integer.parseInt(params.get("page").toString());
            } catch (Exception e) {
                pageNumber = 0;
            }
            try {
                pageSize = Integer.parseInt(params.get("size").toString());
            } catch (Exception e) {
                pageSize = 10;
            }
        } else {
            params = Maps.newHashMap();
        }
        params.put("index", pageNumber + 1);
        params.put("pageSize", pageSize);
        //强制内置两个参数，根据当前用户的市场隔离
        params.put("firmCode", SessionContext.getSessionContext().getUserTicket().getFirmCode());
        params.put("marketId", SessionContext.getSessionContext().getUserTicket().getFirmId());
        params.put("firmId", SessionContext.getSessionContext().getUserTicket().getFirmId());
        
        String jsonBody=JSONObject.toJSONString(params);
        logger.info("url={},  data={},  headerMap={}",url,jsonBody,headerMap);
        try( HttpResponse response = HttpUtil.createPost(url).addHeaders(headerMap).body(jsonBody).execute();){
           if (response.isOk()) {
                return  Optional.ofNullable(response.body());
            }
        }
        return Optional.empty();
    }

    /**
     * 转换结果对象
     * @param jsonDataOpt
     * @param paged
     * @return
     */
    private Page<Map<String, Object>> parseJson(Optional<String> jsonDataOpt, Boolean paged) {
        PageOutput<List> output = PageOutput.failure();
        if (jsonDataOpt.isPresent()) {
            String json = jsonDataOpt.get();
            try {
                output = JSONObject.parseObject(json, PageOutput.class);
            } catch (Exception e) {
                logger.error("解析json {} 出错 ", json, e);
            }
        }
        if (output.isSuccess()) {
            Pageable pageRequest = Pageable.unpaged();;
            if (Objects.nonNull(output.getPageNum()) && Objects.nonNull(output.getPageSize()) && Boolean.TRUE.equals(paged)){
                pageRequest = PageRequest.of(output.getPageNum() - 1, output.getPageSize());
            }
            Page<Map<String, Object>> pageResult = new PageImpl<Map<String, Object>>(output.getData(), pageRequest,
                    output.getTotal() == null ? output.getData().size() : output.getTotal());
            return pageResult;
        }
        Pageable pageRequest = PageRequest.of(0, 10);
        Page<Map<String, Object>> pageResult = new PageImpl<>(Collections.emptyList(), pageRequest, 0);
        return pageResult;
    }
}
