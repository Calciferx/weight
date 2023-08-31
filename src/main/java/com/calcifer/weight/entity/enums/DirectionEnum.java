package com.calcifer.weight.entity.enums;

public enum DirectionEnum implements ICodeEnum {
    FORWARD(1, "正向"),
    REVERSE(2, "反向"),
    ;

    private final int code;
    private final String msg;

    DirectionEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
