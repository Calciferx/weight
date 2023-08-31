package com.calcifer.weight.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RecordTypeEnum implements ICodeEnum {
    CURRENT_DAY(1, "本日记录"),
    CURRENT_WEEK(2, "本周记录"),
    CURRENT_MONTH(3, "本月记录"),
    CURRENT_QUARTER(4, "本季记录"),
    CURRENT_YEAR(5, "本年记录"),
    ;

    private final int code;
    private final String msg;

    RecordTypeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    @JsonValue
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
