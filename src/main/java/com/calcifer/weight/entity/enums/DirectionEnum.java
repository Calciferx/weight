package com.calcifer.weight.entity.enums;

public enum DirectionEnum implements ICodeEnum {
    FORWARD(1, "正向"),
    REVERSE(2, "反向"),
    ;

    private final Integer code;
    private final String msg;

    DirectionEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
