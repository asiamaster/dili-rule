/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dili.rule.provider;

import com.dili.rule.domain.enums.ActionExpressionTypeEnum;
import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.ValuePairImpl;
import com.dili.ss.metadata.ValueProvider;
import java.util.ArrayList;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import one.util.streamex.StreamEx;

@Component
@Scope("prototype")
public class ActionExpressionTypeProvider implements ValueProvider {

    @Override
    public List<ValuePair<?>> getLookupList(Object val, Map metaMap, FieldMeta fieldMeta) {

        List<ValuePair<?>> list = new ArrayList<>();

        StreamEx.of(ActionExpressionTypeEnum.values()).forEach((e) -> {
            ValuePair<?> item = new ValuePairImpl<>(e.getDesc(), String.valueOf(e.getCode()));
            list.add(item);
        }
        );
        return list;

    }

    @Override
    public String getDisplayText(Object val, Map metaMap, FieldMeta fieldMeta) {
        if (val == null) {
            return "";
        }
        try {
            return ActionExpressionTypeEnum.fromCode(Integer.valueOf(String.valueOf(val))).map(ActionExpressionTypeEnum::getDesc).orElse("");
        } catch (Exception e) {
//            e.printStackTrace();
        }

        return String.valueOf(val);
    }

}
