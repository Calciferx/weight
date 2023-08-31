package com.calcifer.weight.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RespCodeEnum implements ICodeEnum {
    SUCCESS(1111, "请求成功"),
    ERROR(0, "操作失败"),
    OrderERROR(22222, "单据已存在"),
    DEBUG(1007, "异常"),
    IS_NOT_LOGIN_ERROR(1008, "用户未登录"),
    IS_NOT_USERNAME_ERROR(1010, "用户不存在"),
    USER_LOCK_ERROR(1011, "用户被禁用"),
    PASSWORD_ERROR(1009, "密码错误"),
    ERP_ENTER_ERROR(2000, "T+回写失败"),
    SERVICE_ERROR(500, "接口请求"),
    PAGE_ERROR(404, "页面错误"),
    ;

    private final int code;
    private final String msg;

    RespCodeEnum(int code, String msg) {
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
