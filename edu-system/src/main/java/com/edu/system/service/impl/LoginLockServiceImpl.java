package com.edu.system.service.impl;

import com.edu.system.service.LoginLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 登录锁定服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginLockServiceImpl implements LoginLockService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 登录失败次数 Redis Key 前缀
     */
    private static final String LOGIN_FAIL_KEY_PREFIX = "login:fail:";

    /**
     * 登录锁定 Redis Key 前缀
     */
    private static final String LOGIN_LOCK_KEY_PREFIX = "login:lock:";

    /**
     * 最大失败次数
     */
    private static final int MAX_FAIL_COUNT = 5;

    /**
     * 锁定时长（分钟）
     */
    private static final int LOCK_DURATION_MINUTES = 30;

    /**
     * 失败记录过期时间（分钟）- 与锁定时长一致
     */
    private static final int FAIL_RECORD_EXPIRE_MINUTES = 30;

    @Override
    public int recordLoginFailure(String username) {
        String failKey = LOGIN_FAIL_KEY_PREFIX + username;

        // 增加失败次数
        Long failCount = redisTemplate.opsForValue().increment(failKey);

        if (failCount == null) {
            failCount = 1L;
        }

        // 设置过期时间（首次失败时设置）
        if (failCount == 1) {
            redisTemplate.expire(failKey, FAIL_RECORD_EXPIRE_MINUTES, TimeUnit.MINUTES);
        }

        // 如果达到最大失败次数，锁定账号
        if (failCount >= MAX_FAIL_COUNT) {
            lockAccount(username);
        }

        log.info("用户 {} 登录失败，当前失败次数: {}", username, failCount);
        return failCount.intValue();
    }

    @Override
    public boolean isLocked(String username) {
        String lockKey = LOGIN_LOCK_KEY_PREFIX + username;
        return Boolean.TRUE.equals(redisTemplate.hasKey(lockKey));
    }

    @Override
    public int getRemainingAttempts(String username) {
        int failCount = getFailureCount(username);
        int remaining = MAX_FAIL_COUNT - failCount;
        return Math.max(0, remaining);
    }

    @Override
    public long getLockRemainingTime(String username) {
        if (!isLocked(username)) {
            return 0;
        }

        String lockKey = LOGIN_LOCK_KEY_PREFIX + username;
        Long expire = redisTemplate.getExpire(lockKey, TimeUnit.SECONDS);
        return expire != null && expire > 0 ? expire : 0;
    }

    @Override
    public void clearLoginFailure(String username) {
        String failKey = LOGIN_FAIL_KEY_PREFIX + username;
        String lockKey = LOGIN_LOCK_KEY_PREFIX + username;

        redisTemplate.delete(failKey);
        redisTemplate.delete(lockKey);

        log.info("清除用户 {} 的登录失败记录", username);
    }

    @Override
    public void unlockAccount(String username) {
        String lockKey = LOGIN_LOCK_KEY_PREFIX + username;
        String failKey = LOGIN_FAIL_KEY_PREFIX + username;

        redisTemplate.delete(lockKey);
        redisTemplate.delete(failKey);

        log.info("管理员手动解锁用户: {}", username);
    }

    @Override
    public int getFailureCount(String username) {
        String failKey = LOGIN_FAIL_KEY_PREFIX + username;
        Object count = redisTemplate.opsForValue().get(failKey);

        if (count == null) {
            return 0;
        }

        if (count instanceof Integer) {
            return (Integer) count;
        } else if (count instanceof Long) {
            return ((Long) count).intValue();
        } else if (count instanceof String) {
            try {
                return Integer.parseInt((String) count);
            } catch (NumberFormatException e) {
                log.error("解析失败次数出错: {}", count, e);
                return 0;
            }
        }

        return 0;
    }

    /**
     * 锁定账号
     */
    private void lockAccount(String username) {
        String lockKey = LOGIN_LOCK_KEY_PREFIX + username;

        // 设置锁定标记，过期时间为锁定时长
        redisTemplate.opsForValue().set(lockKey, System.currentTimeMillis(),
                LOCK_DURATION_MINUTES, TimeUnit.MINUTES);

        log.warn("用户 {} 因登录失败次数过多被锁定 {} 分钟", username, LOCK_DURATION_MINUTES);
    }
}
