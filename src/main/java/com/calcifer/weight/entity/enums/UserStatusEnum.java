package com.calcifer.weight.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum UserStatusEnum implements ICodeEnum {
    FORBIDDEN(0, "账户已禁用"),
    ACTIVATED(1, "账户已启用"),
    ;

    private final Integer code;
    private final String msg;

    UserStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @JsonValue
    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
