package com.calcifer.weight.entity.enums;

import com.calcifer.weight.common.Constant;

public enum ModBusDeviceEnum implements ICodeEnum {
    FRONT_BARRIER_ON(Constant.BARRIER1_ON, "前道闸（开）"),
    FRONT_BARRIER_OFF(Constant.BARRIER1_OFF, "前道闸（关）"),
    BACK_BARRIER_ON(Constant.BARRIER2_ON, "后道闸（开）"),
    BACK_BARRIER_OFF(Constant.BARRIER2_OFF, "后道闸（关）"),
    FRONT_LIGHT(Constant.LIGHT1, "进端红绿灯"),
    BACK_LIGHT(Constant.LIGHT2, "出端红绿灯"),
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
