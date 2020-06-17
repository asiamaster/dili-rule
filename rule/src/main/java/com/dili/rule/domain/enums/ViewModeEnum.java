package com.dili.rule.domain.enums;

import com.google.common.collect.Maps;
import lombok.Getter;

import java.util.Map;

/**
 * <B></B> <B>Copyright:本软件源代码版权归农丰时代科技有限公司及其研发团队所有,未经许可不得任意复制与传播.</B>
 * <B>农丰时代科技有限公司</B>
 *
 * @author yuehongbo
 * @date 2020/5/16 14:23
 */
public enum ViewModeEnum {

    SELECT(10, "选择列表"), TABLE_SINGLE(20, "表格单选"), TABLE_MULTI(25, "表格多选"), RADIO(30, "单选"), CHECK_BOX(40, "复选框"),;

    ViewModeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Getter
    private Integer code;
    @Getter
    private String desc;

    private static Map<Integer, ViewModeEnum> initMaps = Maps.newLinkedHashMap();

    static {
        for (ViewModeEnum an : ViewModeEnum.values()) {
            initMaps.put(an.getCode(), an);
        }
    }

    /**
     * 获取转换后的map对象
     * 
     * @return
     */
    public static Map<Integer, ViewModeEnum> getInitDataMaps() {
        return initMaps;
    }

    /**
     * 获取某个枚举值实例信息
     * 
     * @param code
     * @return
     */
    public static ViewModeEnum getInstance(Integer code) {
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

    /**
     * @param desc the desc to set
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

}
