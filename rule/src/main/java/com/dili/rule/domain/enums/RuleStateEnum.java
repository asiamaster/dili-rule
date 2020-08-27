package com.dili.rule.domain.enums;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * <B></B> <B>Copyright:本软件源代码版权归农丰时代科技有限公司及其研发团队所有,未经许可不得任意复制与传播.</B>
 * <B>农丰时代科技有限公司</B>
 *
 * @author yuehongbo
 * @date 2020/5/18 11:18
 */
public enum RuleStateEnum {

    UNAUDITED(10, "待审核"),
    NOT_PASS(20, "未通过"),
    ENABLED(30, "已启用"),
    UN_STARTED(40, "未开始"),
    DISABLED(50, "已禁用"),
    EXPIRED(60, "已到期"),
    OBSOLETE(70, "已作废"),
    ;

    RuleStateEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private Integer code;
    private String desc;

    private static Map<Integer, RuleStateEnum> initMaps = Maps.newLinkedHashMap();

    static {
        for (RuleStateEnum an : RuleStateEnum.values()) {
            initMaps.put(an.getCode(), an);
        }
    }

    /**
     * 获取转换后的map对象
     *
     * @return
     */
    public static Map<Integer, RuleStateEnum> getInitDataMaps() {
        return initMaps;
    }

    /**
     * 获取某个枚举值实例信息
     * 
     * @param code
     * @return
     */
    public static RuleStateEnum getInstance(Integer code) {
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
