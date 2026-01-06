package com.calcifer.weight.entity.enums;

public enum ActionEnum implements ICodeEnum {
    QUERY(1, "查询"),
    EXPORT(2, "导出"),
    ;

    private final Integer code;
    private final String msg;

    ActionEnum(Integer code, String msg) {
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
