package com.dili.rule.provider;

import com.dili.rule.domain.enums.ConditionTypeEnum;
import com.dili.rule.domain.enums.DataSourceTypeEnum;
import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.ValuePairImpl;
import com.dili.ss.metadata.ValueProvider;
import com.google.common.collect.Lists;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <B></B>
 * <B>Copyright:本软件源代码版权归农丰时代科技有限公司及其研发团队所有,未经许可不得任意复制与传播.</B>
 * <B>农丰时代科技有限公司</B>
 *
 * @author yuehongbo
 * @date 2020/5/15 15:40
 */
@Component
@Scope("prototype")
public class ConditionTypeProvider implements ValueProvider {

    private static final List<ValuePair<?>> BUFFER;

    static {
        BUFFER = Lists.newArrayList();
        BUFFER.addAll(Stream.of(ConditionTypeEnum.values())
                .map(e -> new ValuePairImpl<String>(e.getDesc(), String.valueOf(e.getCode())))
                .collect(Collectors.toList()));
    }

    @Override
    public List<ValuePair<?>> getLookupList(Object val, Map metaMap, FieldMeta fieldMeta) {
        return BUFFER;
    }

    @Override
    public String getDisplayText(Object obj, Map metaMap, FieldMeta fieldMeta) {
        if (obj == null || "".equals(obj)) {
            return null;
        }
        ConditionTypeEnum instance = ConditionTypeEnum.getInstance(Integer.valueOf(obj.toString()));
        if (Objects.nonNull(instance)) {
            return instance.getDesc();
        }
        return null;
    }
}
