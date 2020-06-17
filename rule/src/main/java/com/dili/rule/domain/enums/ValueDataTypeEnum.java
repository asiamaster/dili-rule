package com.dili.rule.domain.enums;

import com.google.common.collect.Maps;
import lombok.Getter;

import java.util.Map;

/**
 * <B></B>
 * <B>Copyright:本软件源代码版权归农丰时代科技有限公司及其研发团队所有,未经许可不得任意复制与传播.</B>
 * <B>农丰时代科技有限公司</B>
 *
 * @author yuehongbo
 * @date 2020/5/20 16:34
 */
public enum ValueDataTypeEnum {

    /**
     * 小数类型
     */
    DECIMAL(100, "小数类型"),
    /**
     * 整数类型
     */
    INTEGER(200, "整数类型"),
    /**
     * 文本类型
     */
    TEXT(300, "文本类型"),
    /**
     * 日期类型
     */
    DATE(400, "日期类型"),
    ;
    ValueDataTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    ValueDataTypeEnum(Integer code, String desc, Boolean range) {
        this.code = code;
        this.desc = desc;
    }
    @Getter
    private Integer code;
    @Getter
    private String desc;


    private static Map<Integer, ValueDataTypeEnum> initMaps = Maps.newLinkedHashMap();

    static {
        for (ValueDataTypeEnum anEnum : ValueDataTypeEnum.values()) {
            initMaps.put(anEnum.getCode(), anEnum);
        }
    }

    /**
     * 获取转换后的map对象
     *
     * @return
     */
    public static Map<Integer, ValueDataTypeEnum> getInitDataMaps() {
        return initMaps;
    }

    /**
     * 获取某个枚举值实例信息
     * @param code
     * @return
     */
    public static ValueDataTypeEnum getInstance(Integer code) {
        return initMaps.get(code);
    }
    

    /**
     * @param code the code to set
     */
    public Integer getCode() {
        return this.code;
    }

    /**
     * @return String return the desc
     */
    public String getDesc() {
        return desc;
    }


}
