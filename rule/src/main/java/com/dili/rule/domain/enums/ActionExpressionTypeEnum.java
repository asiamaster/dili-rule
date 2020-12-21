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
public enum ActionExpressionTypeEnum {

    SIMPLE(1, "简单计算"),
    COMPLEX(2, "阶梯计算"),
    MATCH_CONDITION(3, "匹配条件"),
    // SELECT_MULTI(30, "下拉多选"),
    /*** 以下格式暂未支持 ***/
//    TABLE_SINGLE(20, "表格单选"),
//    SELECT(25, "选择列表"),
//    RADIO(30, "单选"),
//    CHECK_BOX(40, "复选框"),
    ;

    ActionExpressionTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private Integer code;
    private String desc;

    public   boolean equalsToCode(Integer code){
        return this.getCode().equals(code);
    }
    public   static Optional<ActionExpressionTypeEnum> fromCode(Integer code){
        return StreamEx.of(ActionExpressionTypeEnum.values()).filterBy(ActionExpressionTypeEnum::getCode, code).findFirst();
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
