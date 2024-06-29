package com.calcifer.weight.entity.enums;

import static com.calcifer.weight.service.DeviceService.*;

public enum ModBusDeviceEnum implements ICodeEnum {
    FRONT_BARRIER_ON(BARRIER1_ON, "前道闸（开）"),
    FRONT_BARRIER_OFF(BARRIER1_OFF, "前道闸（关）"),
    BACK_BARRIER_ON(BARRIER2_ON, "后道闸（开）"),
    BACK_BARRIER_OFF(BARRIER2_OFF, "后道闸（关）"),
    FRONT_LIGHT(LIGHT1, "进端红绿灯"),
    BACK_LIGHT(LIGHT2, "出端红绿灯"),
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
