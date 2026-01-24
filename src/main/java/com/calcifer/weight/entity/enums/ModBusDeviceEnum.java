package com.calcifer.weight.entity.enums;

import static com.calcifer.weight.service.DeviceService.*;

public enum ModBusDeviceEnum implements ICodeEnum {
    FRONT_BARRIER_ON(BARRIER_FRONT_ON, "前道闸（开）"),
    FRONT_BARRIER_OFF(BARRIER_FRONT_OFF, "前道闸（关）"),
    BACK_BARRIER_ON(BARRIER_BACK_ON, "后道闸（开）"),
    BACK_BARRIER_OFF(BARRIER_BACK_OFF, "后道闸（关）"),
    FRONT_LIGHT(LIGHT_FRONT, "进端红绿灯"),
    BACK_LIGHT(LIGHT_BACK, "出端红绿灯"),
    ;

    private final Integer code;
    private final String msg;

    ModBusDeviceEnum(Integer code, String msg) {
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
