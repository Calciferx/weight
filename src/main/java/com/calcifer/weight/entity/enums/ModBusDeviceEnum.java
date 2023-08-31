package com.calcifer.weight.entity.enums;

public enum ModBusDeviceEnum implements ICodeEnum {
    FRONT_BARRIER_ON(2, "前道闸（开）"),
    FRONT_BARRIER_OFF(3, "前道闸（关）"),
    BACK_BARRIER_ON(7, "后道闸（开）"),
    BACK_BARRIER_OFF(6, "后道闸（关）"),
    FRONT_LIGHT(4, "进端红绿灯"),
    BACK_LIGHT(5, "出端红绿灯"),
    ;

    private final int code;
    private final String msg;

    ModBusDeviceEnum(int code, String msg) {
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
