package com.edu.framework.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 定时任务 - 通知提醒
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationTask {

    // private final NotificationService notificationService;
    // private final ScheduleService scheduleService;
    // private final ContractService contractService;

    /**
     * 上课提醒 - 每天早上8点执行
     * 提醒当天有课的学员
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void sendClassReminder() {
        log.info("Starting class reminder task at {}", LocalDateTime.now());

        try {
            // 查询今天的排课
            LocalDate today = LocalDate.now();
            // List<Schedule> schedules = scheduleService.getSchedulesByDate(today);

            // for (Schedule schedule : schedules) {
            //     // 获取班级学员
            //     // 发送上课提醒
            //     notificationService.sendClassReminder(schedule.getId());
            // }

            log.info("Class reminder task completed");
        } catch (Exception e) {
            log.error("Class reminder task failed", e);
        }
    }

    /**
     * 课前提醒 - 每小时执行一次
     * 提醒1小时后有课的学员
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void sendClassPreReminder() {
        log.info("Starting class pre-reminder task at {}", LocalDateTime.now());

        try {
            // 查询1小时后开始的课程
            // 发送提醒

            log.info("Class pre-reminder task completed");
        } catch (Exception e) {
            log.error("Class pre-reminder task failed", e);
        }
    }

    /**
     * 缴费提醒 - 每天早上9点执行
     * 提醒合同即将到期的学员
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendPaymentReminder() {
        log.info("Starting payment reminder task at {}", LocalDateTime.now());

        try {
            // 查询7天内到期的合同
            // LocalDate expireDate = LocalDate.now().plusDays(7);
            // List<Contract> contracts = contractService.getExpiringContracts(expireDate);

            // for (Contract contract : contracts) {
            //     notificationService.sendPaymentReminder(contract.getId());
            // }

            log.info("Payment reminder task completed");
        } catch (Exception e) {
            log.error("Payment reminder task failed", e);
        }
    }

    /**
     * 课时不足提醒 - 每天早上10点执行
     * 提醒剩余课时不足5节的学员
     */
    @Scheduled(cron = "0 0 10 * * ?")
    public void sendLowClassHourReminder() {
        log.info("Starting low class hour reminder task at {}", LocalDateTime.now());

        try {
            // 查询剩余课时不足5节的学员
            // 发送提醒

            log.info("Low class hour reminder task completed");
        } catch (Exception e) {
            log.error("Low class hour reminder task failed", e);
        }
    }

    /**
     * 作业截止提醒 - 每天下午3点执行
     * 提醒明天截止的作业
     */
    @Scheduled(cron = "0 0 15 * * ?")
    public void sendHomeworkDeadlineReminder() {
        log.info("Starting homework deadline reminder task at {}", LocalDateTime.now());

        try {
            // 查询明天截止的作业
            // 发送提醒

            log.info("Homework deadline reminder task completed");
        } catch (Exception e) {
            log.error("Homework deadline reminder task failed", e);
        }
    }

    /**
     * 生日祝福 - 每天早上9点执行
     * 给当天生日的学员发送祝福
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendBirthdayWish() {
        log.info("Starting birthday wish task at {}", LocalDateTime.now());

        try {
            // 查询今天生日的学员
            // 发送生日祝福

            log.info("Birthday wish task completed");
        } catch (Exception e) {
            log.error("Birthday wish task failed", e);
        }
    }
}
