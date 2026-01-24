package com.calcifer.weight.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum WSCodeEnum implements ICodeEnum {
    SUCCESS(0, "SUCCESS"),//系统消息/系统返回消息
    INFRA(1, "红外状态"),
    SYSTEM_STATUS(2, "系统状态"),
    WEIGH_LOG(5, "称重日志"),
    TRUCK_INFO(6, "车辆信息"),
    RT_WEIGH_NUM(7, "称重KG"),
    WEIGH_INFO(8, "称重记录"),
    AUTO_WEIGHT_BUTTON(10, "自动过磅按钮状态控制")
    ;
    private final Integer code;
    private final String msg;

    WSCodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}