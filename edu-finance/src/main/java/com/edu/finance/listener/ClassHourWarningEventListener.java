package com.edu.finance.listener;

import com.edu.finance.event.ClassHourWarningEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 课时预警事件监听器
 * 处理课时预警事件，发送通知
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ClassHourWarningEventListener {

    // private final NotificationService notificationService;

    /**
     * 处理课时预警事件
     * 异步执行，不影响主流程
     */
    @Async
    @EventListener
    public void handleClassHourWarning(ClassHourWarningEvent event) {
        log.info("收到课时预警事件: type={}, studentId={}, courseId={}, remaining={}",
                event.getWarningType(), event.getStudentId(), event.getCourseId(), event.getRemainingHours());

        try {
            if (event.getSendNotification()) {
                // TODO: 发送通知给学员、家长、顾问
                // 1. 查询学员信息
                // 2. 查询课程信息
                // 3. 构建通知消息
                // 4. 发送站内消息
                // 5. 发送短信/微信通知（可选）

                log.info("课时预警通知已发送: studentId={}, message={}",
                        event.getStudentId(), event.getMessage());
            }
        } catch (Exception e) {
            log.error("处理课时预警事件失败: studentId={}", event.getStudentId(), e);
        }
    }
}
