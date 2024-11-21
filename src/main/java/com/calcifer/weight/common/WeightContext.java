package com.calcifer.weight.common;

import com.calcifer.weight.entity.dto.User;

import java.util.concurrent.ConcurrentHashMap;

public class WeightContext {
    /**
     * 红外被遮挡或称上有东西时重置该时间，即不开始计时
     */
    public static long lastStatusChange;

    public static final ConcurrentHashMap<String, String> NAME_TOKEN_MAP = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, User> TOKEN_USER_MAP = new ConcurrentHashMap<>();
}
