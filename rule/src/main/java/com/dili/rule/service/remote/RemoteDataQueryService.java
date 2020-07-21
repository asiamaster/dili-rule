package com.dili.rule.service.remote;

import cn.hutool.core.collection.CollectionUtil;
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
	private static final Logger log=LoggerFactory.getLogger(RemoteDataQueryService.class);
    /**
     * 通过url请求远程接口(http+json)
     * @param url
     * @return
     */
    public Page<Map<String, Object>> queryData(String url) {
        return this.queryData(url, Collections.emptyMap());
    }

    /**
     * 通过url和params请求远程接口(http+json)
     *
     * @param url
     * @param params
     * @return
     */
    public Page<Map<String, Object>> queryData(String url, Map<String, Object> params) {
        Optional<String> jsonDataOpt = this.remoteQuery(url, this.trimParamsMap(params));
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
    public Page<Map<String, Object>> queryData(ConditionDataSource conditionDataSource, Map<String, Object> params) {
        Optional<String> jsonDataOpt = Optional.empty();
        DataSourceTypeEnum dataSourceType = DataSourceTypeEnum.getInitDataMaps()
                .get(conditionDataSource.getDataSourceType());

        if (DataSourceTypeEnum.LOCAL == dataSourceType) {
            String localJsonData = conditionDataSource.getDataJson();
            jsonDataOpt = Optional.ofNullable(localJsonData);
        } else {
            jsonDataOpt = this.remoteQuery(conditionDataSource.getQueryUrl(), params);
        }
        Page<Map<String, Object>> page = this.parseJson(jsonDataOpt, YesOrNoEnum.YES.getCode().equals(conditionDataSource.getPaged()));
        return page;
    }


    /**
     * 通过ConditionDataSource.queeryUrl对应的的url和keys列表参数请求远程接口(http+json)
     *
     * @param conditionDataSource
     * @param keys
     * @return
     */
    public List<Map<String, Object>> queryKeys(ConditionDataSource conditionDataSource, List<Object> keys) {
        if (CollectionUtil.isEmpty(keys)) {
            return Collections.emptyList();
        }
        Optional<String> jsonDataOpt = Optional.empty();
        DataSourceTypeEnum dataSourceType = DataSourceTypeEnum.getInitDataMaps()
                .get(conditionDataSource.getDataSourceType());
        if (DataSourceTypeEnum.LOCAL == dataSourceType) {
            String localJsonData = conditionDataSource.getDataJson();
            jsonDataOpt = Optional.ofNullable(localJsonData);
        } else {
            //构建查询参数
            Map<String,Object> params = Maps.newHashMap();
            params.put(conditionDataSource.getKeysField(),keys);
            //强制内置两个参数，根据当前用户的市场隔离
            params.put("firmCode", SessionContext.getSessionContext().getUserTicket().getFirmCode());
            params.put("marketId", SessionContext.getSessionContext().getUserTicket().getFirmId());
            HttpResponse response = HttpUtil.createPost(conditionDataSource.getKeysUrl()).body(JSONObject.toJSONString(params)).execute();
            if (response.isOk()) {
                jsonDataOpt = Optional.ofNullable(response.body());
            }
            response.close();
        }
        List<Map<String, Object>> list = this.parseJson(jsonDataOpt, false).getContent();
        return list;
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
    private Optional<String> remoteQuery(String url, Map<String, Object> params) {
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
        Optional<String> result = null;
        HttpResponse response = HttpUtil.createPost(url).body(JSONObject.toJSONString(params)).execute();
        if (response.isOk()) {
            result = Optional.ofNullable(response.body());
        }
        response.close();
        return result;
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
                log.error("解析json {} 出错 ", json, e);
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
