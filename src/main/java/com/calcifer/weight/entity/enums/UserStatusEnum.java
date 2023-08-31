package com.calcifer.weight.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum UserStatusEnum implements ICodeEnum {
    FORBIDDEN(0, "账户被禁用"),
    FORBIDDEN1(1, "账户被禁用"),
    FORBIDDEN2(2, "账户被禁用"),
    FORBIDDEN3(3, "账户被禁用"),
    ;

    private final int code;
    private final String msg;

    UserStatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @JsonValue
    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
