package com.calcifer.weight.autoweigh;

import com.calcifer.weight.entity.enums.ICodeEnum;

public enum WeighStatusEnum implements ICodeEnum {
    WAIT(-1, "等待车辆驶入"),
    TRUCK_FOUND(0, "发现车辆，等待司机刷卡"),
    CARD_READ(1, "已刷卡，等待车辆上称"),
    ENTERING(2, "车辆正在上称"),
    WEIGHING(3, "车辆已上称，正在称重"),
    WEIGHED(4, "称重完成"),
    EXITING(5, "车辆正在下称"),
    EXITED(6, "车辆已下称"),
    TRUCK_LEAVING(7, "车辆正在驶离"),
    STOP_WAIT(-2, "停止自动计量")
    ;

    private final Integer code;
    private final String msg;

    WeighStatusEnum(Integer code, String msg) {
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
