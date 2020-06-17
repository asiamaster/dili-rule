package com.dili.rule.domain.enums;

import com.google.common.collect.Maps;
import lombok.Getter;

import java.util.Map;

/**
 * <B>条件类型枚举定义</B>
 * <B>Copyright:本软件源代码版权归农丰时代科技有限公司及其研发团队所有,未经许可不得任意复制与传播.</B>
 * <B>农丰时代科技有限公司</B>
 *
 * @author yuehongbo
 * @date 2020/5/15 15:30
 */
public enum ConditionTypeEnum {

    /**
     * 等于
     */
    EQUALS(100,"等于"),
    /**
     * 范围
     */
    BETWEEN(200,"范围"),
    /**
     * 包含
     */
    IN(300,"包含"),
    ;
    ConditionTypeEnum(Integer code,String desc) {
        this.code=code;
        this.desc=desc;
    }
    @Getter
    private Integer code;
    @Getter
    private String desc;

    private static Map<Integer,ConditionTypeEnum> initMaps = Maps.newLinkedHashMap();

    static {
        for (ConditionTypeEnum anEnum : ConditionTypeEnum.values()){
            initMaps.put(anEnum.getCode(),anEnum);
        }
    }

    /**
     * 获取转换后的map对象
     * @return
     */
    public static Map<Integer, ConditionTypeEnum> getInitDataMaps() {
        return initMaps;
    }

    /**
     * 获取某个枚举值实例信息
     * @param code
     * @return
     */
    public static ConditionTypeEnum getInstance(Integer code) {
        return initMaps.get(code);
    }

    /**
     * @return the code
     */
    public Integer getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(Integer code) {
        this.code = code;
    }

    /**
     * @return the desc
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
