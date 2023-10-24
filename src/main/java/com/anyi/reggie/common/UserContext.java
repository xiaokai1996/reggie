package com.anyi.reggie.common;

public class UserContext {
    public static ThreadLocal<Long> threadLocalUserId = new ThreadLocal<>();

    public static Long getUserId() {
        return threadLocalUserId.get();
    }

    public static void setUserId(Long userId) {
        threadLocalUserId.set(userId);
    }
}

