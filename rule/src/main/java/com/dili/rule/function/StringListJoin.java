package com.dili.rule.function;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import org.beetl.core.Context;
import org.beetl.core.Function;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * <B>List<String>类型数据转成以特定字符分割的String字符串</B>
 * <B>Copyright:本软件源代码版权归农丰时代科技有限公司及其研发团队所有,未经许可不得任意复制与传播.</B>
 * <B>农丰时代科技有限公司</B>
 *
 * @author yuehongbo
 * @date 2020/6/6 16:52
 */
@Component("stringListJoin")
@ConditionalOnExpression("'${beetl.enable}'=='true'")
public class StringListJoin implements Function {
    @Override
    public Object call(Object[] objects, Context context) {
        Object data = objects[0];
        if (Objects.nonNull(data)){
            Object separator = objects[1];
            if (Objects.isNull(separator)){
                separator = ",";
            }
            return StrUtil.join(String.valueOf(separator), CollectionUtil.toList(data));
        }
        return null;
    }
}
