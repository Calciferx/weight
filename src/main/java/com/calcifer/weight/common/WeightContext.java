package com.calcifer.weight.common;

import com.calcifer.weight.entity.dto.UserDTO;

import java.util.concurrent.ConcurrentHashMap;

public class WeightContext {
    /**
     * 红外被遮挡或称上有东西时重置该时间，即不开始计时
     */
    public static long lastStatusChange;

    public static final ThreadLocal<SessionInfo> CURRENT_SESSION = ThreadLocal.withInitial(SessionInfo::new);
}
