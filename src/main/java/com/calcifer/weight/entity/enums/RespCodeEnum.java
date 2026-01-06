package com.calcifer.weight.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RespCodeEnum implements ICodeEnum {
    SUCCESS(0, "请求成功"),
    FAILED(1001, "操作失败"),
    EXCEPTION(1002, "操作异常"),

    OrderERROR(22222, "单据已存在"),
    IS_NOT_LOGIN_ERROR(1008, "用户未登录"),
    IS_NOT_USERNAME_ERROR(1010, "用户不存在"),
    USER_LOCK_ERROR(1011, "用户被禁用"),
    PASSWORD_ERROR(1009, "密码错误"),
    ERP_ENTER_ERROR(2000, "T+回写失败"),
    SERVICE_ERROR(500, "接口请求"),
    PAGE_ERROR(404, "页面错误"),
    ;

    private final Integer code;
    private final String msg;

    RespCodeEnum(Integer code, String msg) {
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
