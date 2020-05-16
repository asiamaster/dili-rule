package com.dili.rule.provider;

import com.dili.rule.service.remote.SystemRpcService;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.BatchProviderMeta;
import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.ValuePairImpl;
import com.dili.ss.metadata.provider.BatchDisplayTextProviderSupport;
import com.dili.uap.sdk.domain.Systems;
import com.dili.uap.sdk.domain.dto.SystemDto;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collections;
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
 * @date 2020/5/15 17:57
 */
@Component
@Scope("prototype")
public class SystemProvider extends BatchDisplayTextProviderSupport {

    @Autowired
    private SystemRpcService systemRpcService;

    @Override
    public List<ValuePair<?>> getLookupList(Object obj, Map metaMap, FieldMeta fieldMeta) {
        List<Systems> systemsList = systemRpcService.listByExample(DTOUtils.newInstance(SystemDto.class));
        if (CollectionUtils.isNotEmpty(systemsList)) {
            return systemsList.stream().filter(Objects::nonNull).map(s -> {
                ValuePairImpl<?> vp = new ValuePairImpl<>(s.getName(), s.getCode());
                return vp;
            }).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    protected List getFkList(List<String> relationIds, Map metaMap) {
        if (relationIds != null) {
            List<String> codeList = relationIds.stream()
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());
            if (!codeList.isEmpty()) {
                SystemDto systemDto = DTOUtils.newDTO(SystemDto.class);
                systemDto.setCodeList(codeList);
                return systemRpcService.listByExample(systemDto);
            }
        }
        return null;
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
}