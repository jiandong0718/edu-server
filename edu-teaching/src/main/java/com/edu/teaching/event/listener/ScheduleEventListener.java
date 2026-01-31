package com.edu.teaching.event.listener;

import com.edu.teaching.event.CancelScheduleEvent;
import com.edu.teaching.event.SubstituteTeacherEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 排课事件监听器
 * 用于处理代课和停课事件，发送通知给相关人员
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduleEventListener {

    // TODO: 注入通知服务
    // private final NotificationService notificationService;

    /**
     * 监听代课事件
     * 异步处理，不影响主流程
     */
    @Async
    @EventListener
    public void handleSubstituteTeacherEvent(SubstituteTeacherEvent event) {
        log.info("收到代课事件，排课ID: {}, 原教师: {}, 代课教师: {}",
                event.getScheduleId(),
                event.getOriginalTeacherName(),
                event.getSubstituteTeacherName());

        try {
            // 1. 通知原教师
            sendNotificationToTeacher(
                    event.getOriginalTeacherId(),
                    "代课通知",
                    String.format("您在 %s %s-%s 的课程已由 %s 老师代课。原因：%s",
                            event.getScheduleDate(),
                            event.getStartTime(),
                            event.getEndTime(),
                            event.getSubstituteTeacherName(),
                            event.getReason() != null ? event.getReason() : "无")
            );

            // 2. 通知代课教师
            sendNotificationToTeacher(
                    event.getSubstituteTeacherId(),
                    "代课安排",
                    String.format("您需要在 %s %s-%s 代替 %s 老师上课。",
                            event.getScheduleDate(),
                            event.getStartTime(),
                            event.getEndTime(),
                            event.getOriginalTeacherName())
            );

            // 3. 通知班级学员及家长
            sendNotificationToClassStudents(
                    event.getClassId(),
                    "教师变更通知",
                    String.format("您的课程（%s %s-%s）教师由 %s 变更为 %s。",
                            event.getScheduleDate(),
                            event.getStartTime(),
                            event.getEndTime(),
                            event.getOriginalTeacherName(),
                            event.getSubstituteTeacherName())
            );

            // 4. 通知教务管理员
            sendNotificationToAdmins(
                    event.getCampusId(),
                    "代课记录",
                    String.format("排课ID %d 已完成代课操作，原教师：%s，代课教师：%s",
                            event.getScheduleId(),
                            event.getOriginalTeacherName(),
                            event.getSubstituteTeacherName())
            );

            log.info("代课通知发送完成，排课ID: {}", event.getScheduleId());

        } catch (Exception e) {
            log.error("处理代课事件失败，排课ID: {}", event.getScheduleId(), e);
        }
    }

    /**
     * 监听停课事件
     * 异步处理，不影响主流程
     */
    @Async
    @EventListener
    public void handleCancelScheduleEvent(CancelScheduleEvent event) {
        log.info("收到停课事件，排课ID: {}, 停课原因: {}, 是否补课: {}",
                event.getScheduleId(),
                event.getCancelReason(),
                event.getNeedMakeup());

        try {
            // 构建通知内容
            StringBuilder message = new StringBuilder();
            message.append(String.format("您的课程（%s %s-%s）已停课。",
                    event.getScheduleDate(),
                    event.getStartTime(),
                    event.getEndTime()));
            message.append("\n停课原因：").append(event.getCancelReason());

            if (Boolean.TRUE.equals(event.getNeedMakeup())) {
                message.append(String.format("\n补课安排：%s %s-%s",
                        event.getMakeupDate(),
                        event.getMakeupStartTime(),
                        event.getMakeupEndTime()));
            }

            // 1. 通知教师
            sendNotificationToTeacher(
                    event.getTeacherId(),
                    "停课通知",
                    message.toString()
            );

            // 2. 通知班级学员及家长
            sendNotificationToClassStudents(
                    event.getClassId(),
                    "停课通知",
                    message.toString()
            );

            // 3. 通知教务管理员
            sendNotificationToAdmins(
                    event.getCampusId(),
                    "停课记录",
                    String.format("排课ID %d 已停课，教师：%s，原因：%s，是否补课：%s",
                            event.getScheduleId(),
                            event.getTeacherName(),
                            event.getCancelReason(),
                            event.getNeedMakeup() ? "是" : "否")
            );

            log.info("停课通知发送完成，排课ID: {}", event.getScheduleId());

        } catch (Exception e) {
            log.error("处理停课事件失败，排课ID: {}", event.getScheduleId(), e);
        }
    }

    /**
     * 发送通知给教师
     */
    private void sendNotificationToTeacher(Long teacherId, String title, String content) {
        log.info("发送通知给教师，教师ID: {}, 标题: {}", teacherId, title);
        // TODO: 调用通知服务发送通知
        // notificationService.sendToTeacher(teacherId, title, content);
    }

    /**
     * 发送通知给班级学员
     */
    private void sendNotificationToClassStudents(Long classId, String title, String content) {
        log.info("发送通知给班级学员，班级ID: {}, 标题: {}", classId, title);
        // TODO: 调用通知服务发送通知
        // notificationService.sendToClassStudents(classId, title, content);
    }

    /**
     * 发送通知给管理员
     */
    private void sendNotificationToAdmins(Long campusId, String title, String content) {
        log.info("发送通知给管理员，校区ID: {}, 标题: {}", campusId, title);
        // TODO: 调用通知服务发送通知
        // notificationService.sendToAdmins(campusId, title, content);
    }
}
