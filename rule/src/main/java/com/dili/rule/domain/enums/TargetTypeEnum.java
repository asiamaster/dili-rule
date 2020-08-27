package com.dili.rule.domain.enums;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * <B>指标类型</B>
 * <B>Copyright:本软件源代码版权归农丰时代科技有限公司及其研发团队所有,未经许可不得任意复制与传播.</B>
 * <B>农丰时代科技有限公司</B>
 *
 * @author yuehongbo
 * @date 2020/7/3 15:08
 */
public enum TargetTypeEnum {

    CONDITION(0, "条件指标"),
    VARIABLE(1, "计算指标"),
    ;

    TargetTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
    private static Map<Integer, TargetTypeEnum> initMaps = Maps.newLinkedHashMap();

    static {
        for (TargetTypeEnum an : TargetTypeEnum.values()) {
            initMaps.put(an.getCode(), an);
        }
    }

    /**
     * 获取转换后的map对象
     *
     * @return
     */
    public static Map<Integer, TargetTypeEnum> getInitDataMaps() {
        return initMaps;
    }

    /**
     * 获取某个枚举值实例信息
     *
     * @param code
     * @return
     */
    public static TargetTypeEnum getInstance(Integer code) {
        return initMaps.get(code);
    }

    private Integer code;
    private String name;

    public Integer getCode() {
        return code;
    }
    public String getName() {
        return name;
    }
}
