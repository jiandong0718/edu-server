package com.edu.notification.task;

import com.edu.notification.event.ClassRemindEvent;
import com.edu.notification.event.ContractExpireEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 通知定时任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduledTask {

    private final ApplicationEventPublisher eventPublisher;

    /**
     * 上课提醒任务
     * 每小时执行一次，检查即将开始的课程
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void classRemindTask() {
        log.info("开始执行上课提醒任务");
        try {
            // TODO: 查询即将开始的课程（未来1小时内）
            // 这里需要调用教学模块的服务获取课程信息
            // 示例代码：
            // List<ClassSchedule> schedules = classScheduleService.getUpcomingClasses(1);
            // for (ClassSchedule schedule : schedules) {
            //     Map<String, Object> eventData = new HashMap<>();
            //     eventData.put("className", schedule.getClassName());
            //     eventData.put("startTime", schedule.getStartTime());
            //     eventData.put("teacherName", schedule.getTeacherName());
            //     eventData.put("studentName", schedule.getStudentName());
            //
            //     ClassRemindEvent event = new ClassRemindEvent(
            //         this, eventData, schedule.getCampusId(), schedule.getId()
            //     );
            //     eventPublisher.publishEvent(event);
            // }

            log.info("上课提醒任务执行完成");
        } catch (Exception e) {
            log.error("上课提醒任务执行失败", e);
        }
    }

    /**
     * 合同到期提醒任务
     * 每天凌晨1点执行，检查即将到期的合同
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void contractExpireTask() {
        log.info("开始执行合同到期提醒任务");
        try {
            // TODO: 查询即将到期的合同（未来7天内）
            // 这里需要调用财务模块的服务获取合同信息
            // 示例代码：
            // List<Contract> contracts = contractService.getExpiringContracts(7);
            // for (Contract contract : contracts) {
            //     Map<String, Object> eventData = new HashMap<>();
            //     eventData.put("contractNo", contract.getContractNo());
            //     eventData.put("studentName", contract.getStudentName());
            //     eventData.put("endDate", contract.getEndDate());
            //     eventData.put("daysRemaining", ChronoUnit.DAYS.between(LocalDateTime.now(), contract.getEndDate()));
            //
            //     ContractExpireEvent event = new ContractExpireEvent(
            //         this, eventData, contract.getCampusId(), contract.getId()
            //     );
            //     eventPublisher.publishEvent(event);
            // }

            log.info("合同到期提醒任务执行完成");
        } catch (Exception e) {
            log.error("合同到期提醒任务执行失败", e);
        }
    }

    /**
     * 课时不足检查任务
     * 每天早上8点执行，检查课时不足的学员
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void classHourLowTask() {
        log.info("开始执行课时不足检查任务");
        try {
            // TODO: 查询课时不足的学员（剩余课时 <= 5）
            // 这里需要调用财务模块的服务获取课时账户信息
            // 示例代码：
            // List<ClassHourAccount> accounts = classHourAccountService.getLowBalanceAccounts(5);
            // for (ClassHourAccount account : accounts) {
            //     Map<String, Object> eventData = new HashMap<>();
            //     eventData.put("studentName", account.getStudentName());
            //     eventData.put("remainingClassHours", account.getRemainingClassHours());
            //     eventData.put("courseName", account.getCourseName());
            //
            //     ClassHourLowEvent event = new ClassHourLowEvent(
            //         this, eventData, account.getCampusId(), account.getId()
            //     );
            //     eventPublisher.publishEvent(event);
            // }

            log.info("课时不足检查任务执行完成");
        } catch (Exception e) {
            log.error("课时不足检查任务执行失败", e);
        }
    }
}
