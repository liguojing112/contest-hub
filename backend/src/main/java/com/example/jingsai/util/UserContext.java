package com.example.jingsai.util;

import lombok.Data;

/**
 * 用户上下文 - 用于存储当前登录用户信息
 */
public class UserContext {

    private static final ThreadLocal<UserInfo> USER_THREAD_LOCAL = new ThreadLocal<>();

    @Data
    public static class UserInfo {
        private Long userId;
        private String userName;
        private String name;
        private String userRole;
        private String token;
    }

    /**
     * 设置当前用户
     */
    public static void setCurrentUser(UserInfo userInfo) {
        USER_THREAD_LOCAL.set(userInfo);
    }

    /**
     * 获取当前用户
     */
    public static UserInfo getCurrentUser() {
        return USER_THREAD_LOCAL.get();
    }

    /**
     * 清除当前用户
     */
    public static void clear() {
        USER_THREAD_LOCAL.remove();
    }
}