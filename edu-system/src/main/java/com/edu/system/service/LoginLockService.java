package com.edu.system.service;

/**
 * 登录锁定服务
 */
public interface LoginLockService {

    /**
     * 记录登录失败
     * @param username 用户名
     * @return 当前失败次数
     */
    int recordLoginFailure(String username);

    /**
     * 检查账号是否被锁定
     * @param username 用户名
     * @return 是否被锁定
     */
    boolean isLocked(String username);

    /**
     * 获取剩余失败次数
     * @param username 用户名
     * @return 剩余可尝试次数
     */
    int getRemainingAttempts(String username);

    /**
     * 获取锁定剩余时间（秒）
     * @param username 用户名
     * @return 剩余锁定时间（秒），未锁定返回0
     */
    long getLockRemainingTime(String username);

    /**
     * 清除登录失败记录
     * @param username 用户名
     */
    void clearLoginFailure(String username);

    /**
     * 解锁账号（管理员手动解锁）
     * @param username 用户名
     */
    void unlockAccount(String username);

    /**
     * 获取失败次数
     * @param username 用户名
     * @return 失败次数
     */
    int getFailureCount(String username);
}
