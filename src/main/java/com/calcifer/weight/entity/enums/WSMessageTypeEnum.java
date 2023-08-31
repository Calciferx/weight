package com.calcifer.weight.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum WSMessageTypeEnum implements ICodeEnum {
    INFR_NORTH_OUT(1, "北车检红外"),
    INFR_NORTH_IN(2, "北计量红外"),
    INFR_SOUTH_IN(3, "南计量红外"),
    INFR_SOUTH_OUT(4, "南车检红外"),
    weigh_log(5, "称重日志"),
    TruckINFO(6, "车辆信息"),
    weigh_status(7, "称重状态"),
    weight(8, "毛重、净重、皮重"),
    truck_and_weight(9, "车辆及重量信息"),
    ;
    private final int code;
    private final String msg;

    WSMessageTypeEnum(int code, String msg) {
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
