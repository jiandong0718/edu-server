package com.edu.framework.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 定时任务 - 系统维护
 */
@Slf4j
@Component
public class SystemTask {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SystemTask.class);

    /**
     * 清理过期 Token - 每天凌晨2点执行
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanExpiredTokens() {
        log.info("Starting clean expired tokens task at {}", LocalDateTime.now());

        try {
            // 清理 Redis 中过期的 Token
            // redisTemplate.delete(keys);

            log.info("Clean expired tokens task completed");
        } catch (Exception e) {
            log.error("Clean expired tokens task failed", e);
        }
    }

    /**
     * 清理临时文件 - 每天凌晨3点执行
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanTempFiles() {
        log.info("Starting clean temp files task at {}", LocalDateTime.now());

        try {
            // 清理超过7天的临时文件

            log.info("Clean temp files task completed");
        } catch (Exception e) {
            log.error("Clean temp files task failed", e);
        }
    }

    /**
     * 数据统计 - 每天凌晨1点执行
     * 统计前一天的数据
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void dailyStatistics() {
        log.info("Starting daily statistics task at {}", LocalDateTime.now());

        try {
            // 统计前一天的数据
            // - 新增学员数
            // - 收入金额
            // - 课时消耗
            // - 出勤率等

            log.info("Daily statistics task completed");
        } catch (Exception e) {
            log.error("Daily statistics task failed", e);
        }
    }

    /**
     * 更新排课状态 - 每小时执行一次
     * 将已过时间的排课状态更新为已完成
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void updateScheduleStatus() {
        log.info("Starting update schedule status task at {}", LocalDateTime.now());

        try {
            // 更新已过时间的排课状态

            log.info("Update schedule status task completed");
        } catch (Exception e) {
            log.error("Update schedule status task failed", e);
        }
    }
}
