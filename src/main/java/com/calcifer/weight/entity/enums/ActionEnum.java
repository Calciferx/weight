package com.calcifer.weight.entity.enums;

public enum ActionEnum implements ICodeEnum {
    QUERY(1, "查询"),
    EXPORT(2, "导出"),
    ;

    private final int code;
    private final String msg;

    ActionEnum(int code, String msg) {
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
