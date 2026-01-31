package com.edu.framework.security;

/**
 * 安全工具类
 * 用于获取当前登录用户信息
 */
public class SecurityUtils {

    /**
     * 获取当前登录用户ID
     */
    public static Long getUserId() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null ? loginUser.getUserId() : null;
    }

    /**
     * 获取当前登录用户名
     */
    public static String getUsername() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null ? loginUser.getUsername() : null;
    }

    /**
     * 获取当前登录用户信息
     */
    public static LoginUser getLoginUser() {
        return SecurityContextHolder.getLoginUser();
    }

    /**
     * 判断是否为管理员
     */
    public static boolean isAdmin() {
        LoginUser loginUser = getLoginUser();
        if (loginUser == null) {
            return false;
        }
        // 假设userId为1的是管理员，实际应该根据角色判断
        return Long.valueOf(1L).equals(loginUser.getUserId());
    }

    /**
     * 获取当前用户的校区ID
     */
    public static Long getCampusId() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null ? loginUser.getCampusId() : null;
    }
}
