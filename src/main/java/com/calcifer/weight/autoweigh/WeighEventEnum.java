package com.calcifer.weight.autoweigh;

import com.calcifer.weight.entity.enums.ICodeEnum;

public enum WeighEventEnum implements ICodeEnum {
    TRUCK_FOUND(1, "车辆准备进入"), // 红外1被遮挡，发现来车
    READ_CARD(2, "读卡成功"), // 读卡成功，车辆开始上称
    CANCEL_ENTER(3, "取消进入"), // 未读卡成功，车辆离开
    ENTER(3, "车辆上称"), // 红外2遮挡，车辆正在上称
    ENTERED(4, "车辆已上称"), // 红外2由遮挡变为未遮挡，车辆已上称
    WEIGHED(5, "称重完成"),
    LEAVING_WEIGH(6, "车辆正在下称"), // 红外3遮挡，车辆正在下称
    LEFT_WEIGH(7, "车辆已下称"), //红外3未遮挡，车辆已下称
    LEAVING(8, "车辆正在驶离"), // 红外4遮挡，车辆正在驶离
    LEFT(9, "车辆完全驶离"), // 红外4未遮挡，车辆完全按驶离
    RESET(10, "重置系统"),
    STOP_WAIT(11, "停止自动计量"),
    START_WAIT(12, "开始自动计量"),
    ;

    private final int code;
    private final String msg;

    WeighEventEnum(int code, String msg) {
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
