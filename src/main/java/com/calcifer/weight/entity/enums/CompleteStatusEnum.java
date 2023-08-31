package com.calcifer.weight.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CompleteStatusEnum implements ICodeEnum {
    UNCOMPLETED(1, "未完成记录"),
    COMPLETED(2, "已完成记录"),
    ;

    private final int code;
    private final String msg;

    CompleteStatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    @JsonValue
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
