package com.edu.teaching.event.listener;

import com.edu.teaching.event.LeaveApprovedEvent;
import com.edu.teaching.event.MakeupArrangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 请假和补课事件监听器
 * 用于发送通知消息
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LeaveEventListener {

    // TODO: 注入通知服务
    // private final NotificationService notificationService;

    /**
     * 监听请假审批事件
     */
    @Async
    @EventListener
    public void handleLeaveApproved(LeaveApprovedEvent event) {
        log.info("请假审批事件: 请假申请ID={}, 学员ID={}, 审批结果={}",
                event.getLeaveRequestId(),
                event.getStudentId(),
                event.getApproved() ? "批准" : "拒绝");

        // TODO: 发送通知给学员
        // String message = event.getApproved()
        //     ? "您的请假申请已批准"
        //     : "您的请假申请已被拒绝，原因：" + event.getRemark();
        // notificationService.sendToStudent(event.getStudentId(), "请假审批通知", message);
    }

    /**
     * 监听补课安排事件
     */
    @Async
    @EventListener
    public void handleMakeupArranged(MakeupArrangedEvent event) {
        log.info("补课安排事件: 补课记录ID={}, 学员ID={}, 补课排课ID={}",
                event.getMakeupLessonId(),
                event.getStudentId(),
                event.getMakeupScheduleId());

        // TODO: 发送通知给学员
        // String message = "您的补课已安排，请按时参加";
        // notificationService.sendToStudent(event.getStudentId(), "补课安排通知", message);

        // TODO: 发送通知给教师
        // 获取补课排课的教师ID，发送通知
    }
}
