package com.dili.rule.provider;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.BatchProviderMeta;
import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.ValuePairImpl;
import com.dili.ss.metadata.provider.BatchDisplayTextProviderSupport;
import com.dili.uap.sdk.domain.DataDictionaryValue;
import com.dili.uap.sdk.rpc.DataDictionaryRpc;
import com.dili.uap.sdk.session.SessionContext;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <B></B>
 * <B>Copyright:本软件源代码版权归农丰时代科技有限公司及其研发团队所有,未经许可不得任意复制与传播.</B>
 * <B>农丰时代科技有限公司</B>
 *
 * @author yuehongbo
 * @date 2020/6/5 18:37
 */
@Component
@Scope("prototype")
public class DataDictionaryValueProvider extends BatchDisplayTextProviderSupport {

    //前台需要传入的参数
    protected static final String DD_CODE_KEY = "dd_code";
    //查询数据字典，是否需要根据当前市场过滤
    private static final String DD_WITH_MARKET = "dd_with_market";
    @Autowired
    private DataDictionaryRpc dataDictionaryRpc;

    @Override
    public List<ValuePair<?>> getLookupList(Object val, Map metaMap, FieldMeta fieldMeta) {
        Object queryParams = metaMap.get(QUERY_PARAMS_KEY);
        if(queryParams == null) {
            return Lists.newArrayList();
        }
        List<ValuePair<?>> valuePairs = Lists.newArrayList();
        JSONObject jsonObject = JSONObject.parseObject(String.valueOf(queryParams));
        DataDictionaryValue query = DTOUtils.newDTO(DataDictionaryValue.class);
        produceCondition(query, jsonObject);
        BaseOutput<List<DataDictionaryValue>> output = dataDictionaryRpc.listDataDictionaryValue(query);
        if (output.isSuccess() && CollectionUtil.isNotEmpty(output.getData())) {
            valuePairs = output.getData().stream().filter(Objects::nonNull).sorted(Comparator.comparing(DataDictionaryValue::getId)).map(t -> {
                ValuePairImpl<?> vp = new ValuePairImpl<>(t.getName(), t.getCode());
                return vp;
            }).collect(Collectors.toList());
        }
        return valuePairs;
    }

    @Override
    protected List getFkList(List<String> ddvIds, Map metaMap) {
        Object queryParams = metaMap.get(QUERY_PARAMS_KEY);
        if(queryParams == null) {
            return Lists.newArrayList();
        }
        JSONObject jsonObject = JSONObject.parseObject(String.valueOf(queryParams));
        DataDictionaryValue query = DTOUtils.newDTO(DataDictionaryValue.class);
        produceCondition(query, jsonObject);
        BaseOutput<List<DataDictionaryValue>> output = dataDictionaryRpc.listDataDictionaryValue(query);
        if (output.isSuccess() && CollectionUtil.isNotEmpty(output.getData())) {
            return output.getData();
        }
        return Lists.newArrayList();
    }

    @Override
    protected BatchProviderMeta getBatchProviderMeta(Map metaMap) {
        BatchProviderMeta batchProviderMeta = DTOUtils.newInstance(BatchProviderMeta.class);
        //设置主DTO和关联DTO需要转义的字段名
        batchProviderMeta.setEscapeFiled("name");
        //忽略大小写关联
        batchProviderMeta.setIgnoreCaseToRef(true);
        //关联(数据库)表的主键的字段名，默认取id
        batchProviderMeta.setRelationTablePkField("code");
        //当未匹配到数据时，返回的值
        batchProviderMeta.setMismatchHandler(t -> "-");
        return batchProviderMeta;
    }

    /**
     * 获取数据字典编码
     * @return
     */
    private String getDdCode(JSONObject jsonObject) {
        //清空缓存
        String ddCode = jsonObject.getString(DD_CODE_KEY);
        if (ddCode == null) {
            throw new RuntimeException("dd_code属性为空");
        }
        return ddCode;
    }

    /**
     * 组装查询条件
     * @param condition
     */
    private void produceCondition(DataDictionaryValue condition,JSONObject jsonObject){
        condition.setDdCode(getDdCode(jsonObject));
        //如果需要关联市场，则需要获取当前市场信息
        Boolean aBoolean = jsonObject.getBoolean(DD_WITH_MARKET);
        if (Objects.nonNull(aBoolean) && Boolean.TRUE.equals(aBoolean)) {
            condition.setFirmId(SessionContext.getSessionContext().getUserTicket().getFirmId());
        }
    }
}
