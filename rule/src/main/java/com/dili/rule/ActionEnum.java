package com.dili.rule;

import one.util.streamex.StreamEx;

import java.util.Optional;

public enum ActionEnum {
    /**
     * 新增
     */
    INSERT(1, "新增"),
    /**
     * 更新
     */
    UPDATE(2, "更新"),
    /**
     * 复制
     */
    COPY(3, "复制"),
    ;

    private ActionEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    private Integer code;
    private String name;

    public Integer getCode() {
        return code;
    }

    public boolean equalsToCode(Integer code) {
        return this.code == code;
    }

    public static Optional<ActionEnum> fromCode(Integer code) {
        return StreamEx.of(ActionEnum.values()).filterBy(ActionEnum::getCode, code).findFirst();
    }

}
