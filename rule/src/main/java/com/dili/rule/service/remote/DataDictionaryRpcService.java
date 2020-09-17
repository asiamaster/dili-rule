package com.dili.rule.service.remote;

import com.dili.commons.glossary.YesOrNoEnum;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.uap.sdk.domain.DataDictionaryValue;
import com.dili.uap.sdk.rpc.DataDictionaryRpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author yuehongbo
 * @Copyright 本软件源代码版权归农丰时代科技有限公司及其研发团队所有, 未经许可不得任意复制与传播.
 * @date 2020/9/17 16:45
 */
@Service
public class DataDictionaryRpcService {

    private static final Logger log= LoggerFactory.getLogger(DataDictionaryRpcService.class);

    @Autowired
    private DataDictionaryRpc dataDictionaryRpc;
    /**
     * 获取费用业务类型
     * @param marketId 市场ID
     * @param enable 是否查询启用
     * @return
     */
    public List<DataDictionaryValue> getBusinessType(Long marketId, Boolean enable) {
        List<DataDictionaryValue> dataList = null;
        try {
            DataDictionaryValue dataDictionaryValue = DTOUtils.newInstance(DataDictionaryValue.class);
            dataDictionaryValue.setDdCode("base_business_type");
            dataDictionaryValue.setFirmId(marketId);
            if (Objects.nonNull(enable) && enable) {
                dataDictionaryValue.setState(YesOrNoEnum.YES.getCode());
            }
            BaseOutput<List<DataDictionaryValue>> output = dataDictionaryRpc.listDataDictionaryValue(dataDictionaryValue);
            if (output.isSuccess()) {
                dataList = output.getData();
            }
        } catch (Exception t) {
            log.error("获取业务类型异常:" + t.getMessage(), t);
        }
        return dataList;
    }
}
