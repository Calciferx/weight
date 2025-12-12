package com.calcifer.weight.autoweigh;

import com.calcifer.weight.entity.enums.ICodeEnum;

public enum WeighStatusEnum implements ICodeEnum {
    WAIT(1, "等待车辆驶入"),
    WAIT_CARD(2, "等待司机刷卡"),
    WAIT_ENTER(2, "等待车辆上称"),
    ENTERING(3, "车辆正在上称"),
    ON_WEIGH(4, "车辆已上称"),
    WEIGHED(5, "称重完成"),
    LEAVING_WEIGH(6, "正在下称"),
    LEFT_WEIGH(7, "已下称"),
    LEAVING(8, "正在驶离"),
    STOP_WAIT(9, "停止自动计量")
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
