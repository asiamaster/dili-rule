package com.dili.rule.provider;

import com.dili.rule.domain.enums.TargetTypeEnum;
import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.ValuePairImpl;
import com.dili.ss.metadata.ValueProvider;
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
 * @date 2020/7/3 15:46
 */
@Component
@Scope("prototype")
public class TargetTypeProvider implements ValueProvider {

    @Override
    public List<ValuePair<?>> getLookupList(Object val, Map metaMap, FieldMeta fieldMeta) {
        return Stream.of(TargetTypeEnum.values())
                .map(e -> new ValuePairImpl<>(e.getName(), String.valueOf(e.getCode())))
                .collect(Collectors.toList());
    }

    @Override
    public String getDisplayText(Object obj, Map metaMap, FieldMeta fieldMeta) {
        if (obj == null || "".equals(obj)) {
            return null;
        }
        TargetTypeEnum instance = TargetTypeEnum.getInstance(Integer.valueOf(obj.toString()));
        if (Objects.nonNull(instance)) {
            return instance.getName();
        }
        return null;
    }
}
