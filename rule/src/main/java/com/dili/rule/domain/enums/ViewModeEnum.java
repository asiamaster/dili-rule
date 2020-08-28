package com.dili.rule.domain.enums;

import java.util.Map;
import java.util.Optional;

import com.google.common.collect.Maps;

import one.util.streamex.StreamEx;

/**
 * <B></B> <B>Copyright:本软件源代码版权归农丰时代科技有限公司及其研发团队所有,未经许可不得任意复制与传播.</B>
 * <B>农丰时代科技有限公司</B>
 *
 * @author yuehongbo
 * @date 2020/5/16 14:23
 */
public enum ViewModeEnum {

    TABLE_MULTI(10, "表格多选"),
    TREE_MULTI(20, "树多选"),
    SELECT_MULTI(30, "下拉多选"),
    /*** 以下格式暂未支持 ***/
//    TABLE_SINGLE(20, "表格单选"),
//    SELECT(25, "选择列表"),
//    RADIO(30, "单选"),
//    CHECK_BOX(40, "复选框"),
    ;

    ViewModeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private Integer code;
    private String desc;

    private static Map<Integer, ViewModeEnum> initMaps = Maps.newLinkedHashMap();

    static {
        for (ViewModeEnum an : ViewModeEnum.values()) {
            initMaps.put(an.getCode(), an);
        }
    }
    public   boolean equalsToCode(Integer code){
        return this.getCode().equals(code);
    }
    public   static Optional<ViewModeEnum> fromCode(Integer code){
        return StreamEx.of(ViewModeEnum.values()).filterBy(ViewModeEnum::getCode, code).findFirst();
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
     * @return Integer return the code
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
