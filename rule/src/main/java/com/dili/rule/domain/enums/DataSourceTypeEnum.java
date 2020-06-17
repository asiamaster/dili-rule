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
 * @date 2020/5/13 16:36
 */
public enum DataSourceTypeEnum {

    /**
     * 本地数据
     */
    LOCAL("local","本地数据"),
    /**
     * 远程数据
     */
    REMOTE("remote","远程数据"),
    ;
    @Getter
    private String code;
    @Getter
    private String desc;

    DataSourceTypeEnum(String code,String desc) {
        this.code = code;
        this.desc = desc;
    }

    private static Map<String,DataSourceTypeEnum> initMaps = Maps.newLinkedHashMap();

    static {
        for (DataSourceTypeEnum anEnum : DataSourceTypeEnum.values()) {
            initMaps.put(anEnum.getCode(),anEnum);
        }
    }

    /**
     * 获取转换后的map对象
     * @return
     */
    public static Map<String, DataSourceTypeEnum> getInitDataMaps() {
        return initMaps;
    }

    /**
     * 获取某个枚举值实例信息
     * @param code
     * @return
     */
    public static DataSourceTypeEnum getInstance(String code) {
        return initMaps.get(code);
    }
    

    /**
     * @param code the code to set
     */
    public String getCode( ) {
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
