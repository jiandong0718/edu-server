package com.edu.finance.task;

import com.edu.finance.domain.vo.ClassHourBalanceVO;
import com.edu.finance.event.ClassHourWarningEvent;
import com.edu.finance.service.ClassHourAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 课时预警定时任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ClassHourWarningTask {

    private final ClassHourAccountService classHourAccountService;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 课时余额不足预警 - 每天早上10点执行
     * 提醒剩余课时不足5节的学员
     */
    @Scheduled(cron = "0 0 10 * * ?")
    public void checkLowBalanceWarning() {
        log.info("开始执行课时余额不足预警任务: {}", LocalDateTime.now());

        try {
            // 查询余额不足的账户（阈值：5课时）
            BigDecimal threshold = BigDecimal.valueOf(5);
            List<ClassHourBalanceVO> warningAccounts = classHourAccountService.getWarningAccounts("low_balance", threshold);

            log.info("发现 {} 个课时余额不足的账户", warningAccounts.size());

            // 发布预警事件
            for (ClassHourBalanceVO account : warningAccounts) {
                ClassHourWarningEvent event = new ClassHourWarningEvent(
                        this,
                        "low_balance",
                        account.getAccountId(),
                        account.getStudentId(),
                        account.getCourseId(),
                        account.getRemainingHours(),
                        threshold,
                        String.format("学员课时余额不足，剩余%.2f课时，建议及时续费", account.getRemainingHours().doubleValue()),
                        true
                );
                eventPublisher.publishEvent(event);

                log.debug("发布课时预警事件: studentId={}, courseId={}, remaining={}",
                        account.getStudentId(), account.getCourseId(), account.getRemainingHours());
            }

            log.info("课时余额不足预警任务完成，共处理 {} 条预警", warningAccounts.size());
        } catch (Exception e) {
            log.error("课时余额不足预警任务执行失败", e);
        }
    }

    /**
     * 课时即将用完预警 - 每天早上10点30分执行
     * 提醒剩余课时少于2节的学员
     */
    @Scheduled(cron = "0 30 10 * * ?")
    public void checkCriticalBalanceWarning() {
        log.info("开始执行课时即将用完预警任务: {}", LocalDateTime.now());

        try {
            // 查询余额严重不足的账户（阈值：2课时）
            BigDecimal threshold = BigDecimal.valueOf(2);
            List<ClassHourBalanceVO> warningAccounts = classHourAccountService.getWarningAccounts("low_balance", threshold);

            log.info("发现 {} 个课时即将用完的账户", warningAccounts.size());

            // 发布预警事件
            for (ClassHourBalanceVO account : warningAccounts) {
                ClassHourWarningEvent event = new ClassHourWarningEvent(
                        this,
                        "critical_balance",
                        account.getAccountId(),
                        account.getStudentId(),
                        account.getCourseId(),
                        account.getRemainingHours(),
                        threshold,
                        String.format("学员课时即将用完，仅剩%.2f课时，请尽快续费", account.getRemainingHours().doubleValue()),
                        true
                );
                eventPublisher.publishEvent(event);

                log.debug("发布课时严重预警事件: studentId={}, courseId={}, remaining={}",
                        account.getStudentId(), account.getCourseId(), account.getRemainingHours());
            }

            log.info("课时即将用完预警任务完成，共处理 {} 条预警", warningAccounts.size());
        } catch (Exception e) {
            log.error("课时即将用完预警任务执行失败", e);
        }
    }

    /**
     * 课时统计报表 - 每天晚上23点执行
     * 生成当日课时使用统计
     */
    @Scheduled(cron = "0 0 23 * * ?")
    public void generateDailyStatistics() {
        log.info("开始生成课时统计报表: {}", LocalDateTime.now());

        try {
            // TODO: 实现统计报表生成逻辑
            // 1. 统计当日课时消耗情况
            // 2. 统计各校区课时使用情况
            // 3. 统计各课程消课情况
            // 4. 生成报表并发送给管理员

            log.info("课时统计报表生成完成");
        } catch (Exception e) {
            log.error("课时统计报表生成失败", e);
        }
    }
}
