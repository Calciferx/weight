package com.calcifer.weight.autoweigh;

import com.calcifer.weight.entity.enums.ICodeEnum;

public enum WeighStatusEnum implements ICodeEnum {
    WAIT(1, "等待车辆驶入"),
    READING_PLATE_NUM(2, "读取车牌号"),
    WAIT_ENTER(3, "等待车辆上称"),
    ENTERING(4, "车辆正在上称"),
    WAIT_CARD(5, "等待司机刷卡"),
    ON_WEIGH(6, "开始称重"),
    WEIGHED(7, "称重完成"),
    LEAVING_WEIGH(8, "正在下称"),
    LEFT_WEIGH(9, "已下称"),
    LEAVING(10, "正在驶离"),
    ;

    private final int code;
    private final String msg;

    WeighStatusEnum(int code, String msg) {
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
