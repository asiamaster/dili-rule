package com.dili.rule.provider;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dili.assets.sdk.dto.BusinessChargeItemDto;
import com.dili.assets.sdk.rpc.BusinessChargeItemRpc;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.BatchProviderMeta;
import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.provider.BatchDisplayTextProviderSupport;

import cn.hutool.core.collection.CollectionUtil;

/**
 * <B></B>
 * <B>Copyright:本软件源代码版权归农丰时代科技有限公司及其研发团队所有,未经许可不得任意复制与传播.</B>
 * <B>农丰时代科技有限公司</B>
 *
 * @author yuehongbo
 * @date 2020/6/6 16:34
 */
@Component
public class ChargeItemProvider extends BatchDisplayTextProviderSupport {

    @Autowired
    private BusinessChargeItemRpc chargeItemRpc;

    @Override
    public List<ValuePair<?>> getLookupList(Object obj, Map metaMap, FieldMeta fieldMeta) {
        throw new UnsupportedOperationException("此方法暂未实现");
    }


    @Override
    protected List getFkList(List<String> relationIds, Map metaMap) {
        if (CollectionUtil.isEmpty(relationIds)) {
            return Collections.EMPTY_LIST;
        }
        List<Long> idList = relationIds.stream().distinct().map(c -> Long.valueOf(c)).collect(Collectors.toList());
        BusinessChargeItemDto condition = new BusinessChargeItemDto();
        condition.setIdList(idList);
        return list(condition);
    }

    @Override
    protected BatchProviderMeta getBatchProviderMeta(Map metaMap) {
        BatchProviderMeta batchProviderMeta = DTOUtils.newInstance(BatchProviderMeta.class);
        //设置主DTO和关联DTO需要转义的字段名
        batchProviderMeta.setEscapeFiled("chargeItem");
        //忽略大小写关联
        batchProviderMeta.setIgnoreCaseToRef(true);
        //关联(数据库)表的主键的字段名，默认取id
        batchProviderMeta.setRelationTablePkField("id");
        //当未匹配到数据时，返回的值
        batchProviderMeta.setMismatchHandler(t -> "-");
        return batchProviderMeta;
    }

    /**
     * 远程获取收费项信息
     * @param condition
     * @return
     */
    private List<BusinessChargeItemDto> list(BusinessChargeItemDto condition) {
        try {
            BaseOutput<List<BusinessChargeItemDto>> output = chargeItemRpc.listByExample(condition);
            if (output.isSuccess()) {
                return output.getData();
            }
        } catch (Throwable t) {
            log.error("远程查询收费信息异常:" + t.getMessage(), t);
        }
        return Collections.emptyList();
    }
}