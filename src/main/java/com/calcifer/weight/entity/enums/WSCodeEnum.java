package com.calcifer.weight.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum WSCodeEnum implements ICodeEnum {
    MSGType_1(1111, "SUCCESS"),//系统消息/系统返回消息
    MSGType_2(2222, "心跳消息/心跳放回的消息"),
    MSGType_3(3333, ""),
    MSGType_4(4444, "错误消息/错误返回的消息"),
    MSGType_5(5555, ""),
    exportMsg(0, "导出成功"),
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
