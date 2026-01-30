package com.edu.framework.security;

/**
 * 安全上下文持有者
 * 用于在当前线程中存储和获取登录用户信息
 */
public class SecurityContextHolder {

    private static final ThreadLocal<LoginUser> LOGIN_USER_HOLDER = new ThreadLocal<>();

    /**
     * 设置登录用户
     */
    public static void setLoginUser(LoginUser loginUser) {
        LOGIN_USER_HOLDER.set(loginUser);
    }

    /**
     * 获取登录用户
     */
    public static LoginUser getLoginUser() {
        return LOGIN_USER_HOLDER.get();
    }

    /**
     * 获取用户ID
     */
    public static Long getUserId() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null ? loginUser.getUserId() : null;
    }

    /**
     * 获取用户名
     */
    public static String getUsername() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null ? loginUser.getUsername() : null;
    }

    /**
     * 获取校区ID
     */
    public static Long getCampusId() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null ? loginUser.getCampusId() : null;
    }

    /**
     * 清除登录用户
     */
    public static void clear() {
        LOGIN_USER_HOLDER.remove();
    }
}
