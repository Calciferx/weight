package com.calcifer.weight.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum WSCodeEnum implements ICodeEnum {
    exportMsg(0, "导出成功"),
    MSGType_1(1111, "SUCCESS"),//系统消息/系统返回消息
    INFR_NORTH_OUT(1, "北车检红外"),
    INFR_NORTH_IN(2, "北计量红外"),
    INFR_SOUTH_IN(3, "南计量红外"),
    INFR_SOUTH_OUT(4, "南车检红外"),
    WEIGH_LOG(5, "称重日志"),
    TRUCK_INFO(6, "车辆信息"),
    RT_WEIGH_NUM(7, "称重KG"),
    WEIGH_INFO(8, "毛重、净重、皮重"),
    TRUCK_AND_WEIGHT(9, "车辆及重量信息"),
    READ_CARD_NUM(10, "读取卡片信息"),
    ;
    private final int code;
    private final String msg;

    WSCodeEnum(int code, String msg) {
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
