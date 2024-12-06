package com.calcifer.weight.common;

import com.calcifer.weight.entity.dto.ModBusDeviceSerialSort;
import com.calcifer.weight.entity.dto.User;

import java.util.concurrent.ConcurrentHashMap;

public class WeightContext {
    /**
     * 红外被遮挡或称上有东西时重置该时间，即不开始计时
     */
    public static long lastStatusChange;

    public static final ConcurrentHashMap<String, String> NAME_TOKEN_MAP = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, User> TOKEN_USER_MAP = new ConcurrentHashMap<>();

    /**
     * 进方向设备
     */
    public static ModBusDeviceSerialSort front;
    /**
     * 出方向设备
     */
    public static ModBusDeviceSerialSort back;

    public static void reverseDirection() {
        ModBusDeviceSerialSort temp = front;
        front = back;
        back = temp;
    }

    /**
     * 临时存储刷卡读到的供应商名称
     */
    public static String supplierName;
    /**
     * 临时存储刷卡读到的物料名称
     */
    public static String materialName;

}
