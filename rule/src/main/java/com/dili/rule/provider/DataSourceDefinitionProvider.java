package com.dili.rule.provider;

import cn.hutool.core.collection.CollectionUtil;
import com.dili.rule.domain.dto.DataSourceDefinitionDto;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.BatchProviderMeta;
import com.dili.ss.metadata.provider.BatchDisplayTextProviderSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import com.dili.rule.service.DataSourceDefinitionService;

/**
 * <B>条件数据源值provider</B>
 * <B>Copyright:本软件源代码版权归农丰时代科技有限公司及其研发团队所有,未经许可不得任意复制与传播.</B>
 * <B>农丰时代科技有限公司</B>
 *
 * @author yuehongbo
 * @date 2020/5/23 16:44
 */
@Component
@Scope("prototype")
public class DataSourceDefinitionProvider extends BatchDisplayTextProviderSupport {

    @Autowired
    private DataSourceDefinitionService dataSourceDefinitionService;

    @Override
    protected BatchProviderMeta getBatchProviderMeta(Map metaMap) {
        BatchProviderMeta batchProviderMeta = DTOUtils.newInstance(BatchProviderMeta.class);
        //设置主DTO和关联DTO需要转义的字段名
        batchProviderMeta.setEscapeFiled("name");
        //忽略大小写关联
        batchProviderMeta.setIgnoreCaseToRef(true);
        //关联(数据库)表的主键的字段名，默认取id
        batchProviderMeta.setRelationTablePkField("id");
        //当未匹配到数据时，返回的值
        batchProviderMeta.setMismatchHandler(t -> "-");
        return batchProviderMeta;
    }

    @Override
    protected List getFkList(List<String> relationIds, Map metaMap) {
        if (CollectionUtil.isNotEmpty(relationIds)) {
            List<Long> idList = relationIds.stream()
                    .filter(Objects::nonNull)
                    .distinct()
                    .map(t -> Long.valueOf(t))
                    .collect(Collectors.toList());
            if (!idList.isEmpty()) {
                DataSourceDefinitionDto dataSourceDefinitionDto = new DataSourceDefinitionDto();
                dataSourceDefinitionDto.setIdList(idList);
                return dataSourceDefinitionService.listByExample(dataSourceDefinitionDto);
            }
        }
        return null;
    }
}
