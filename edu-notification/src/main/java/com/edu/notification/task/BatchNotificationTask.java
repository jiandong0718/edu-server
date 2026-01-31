package com.edu.notification.task;

import com.edu.notification.domain.entity.Notification;
import com.edu.notification.domain.entity.NotificationLog;
import com.edu.notification.domain.vo.BatchSendResultVO;
import com.edu.notification.service.NotificationLogService;
import com.edu.notification.service.NotificationService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 批量通知发送任务
 */
@Slf4j
@Data
public class BatchNotificationTask {

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 通知列表
     */
    private List<Notification> notifications;

    /**
     * 总数
     */
    private Integer totalCount;

    /**
     * 成功数
     */
    private AtomicInteger successCount = new AtomicInteger(0);

    /**
     * 失败数
     */
    private AtomicInteger failedCount = new AtomicInteger(0);

    /**
     * 任务状态
     */
    private String status = "pending";

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 错误信息列表
     */
    private List<String> errors = new ArrayList<>();

    /**
     * 通知服务
     */
    private NotificationService notificationService;

    /**
     * 通知日志服务
     */
    private NotificationLogService notificationLogService;

    /**
     * 每批发送数量
     */
    private static final int BATCH_SIZE = 100;

    /**
     * 最大重试次数
     */
    private static final int MAX_RETRY = 3;

    public BatchNotificationTask(List<Notification> notifications,
                                  NotificationService notificationService,
                                  NotificationLogService notificationLogService) {
        this.taskId = UUID.randomUUID().toString();
        this.notifications = notifications;
        this.totalCount = notifications.size();
        this.notificationService = notificationService;
        this.notificationLogService = notificationLogService;
    }

    /**
     * 执行批量发送
     */
    public CompletableFuture<BatchSendResultVO> execute() {
        return CompletableFuture.supplyAsync(() -> {
            this.status = "sending";
            this.startTime = LocalDateTime.now();

            try {
                // 分批发送
                for (int i = 0; i < notifications.size(); i += BATCH_SIZE) {
                    int end = Math.min(i + BATCH_SIZE, notifications.size());
                    List<Notification> batch = notifications.subList(i, end);
                    sendBatch(batch);
                }

                this.status = "completed";
            } catch (Exception e) {
                log.error("Batch notification task failed: {}", taskId, e);
                this.status = "failed";
                errors.add("任务执行失败: " + e.getMessage());
            } finally {
                this.endTime = LocalDateTime.now();
            }

            return buildResult();
        });
    }

    /**
     * 发送一批通知
     */
    private void sendBatch(List<Notification> batch) {
        for (Notification notification : batch) {
            sendWithRetry(notification, 0);
        }
    }

    /**
     * 发送通知（带重试）
     */
    private void sendWithRetry(Notification notification, int retryCount) {
        try {
            // 保存通知记录
            notificationService.save(notification);

            // 执行发送
            boolean success = doSend(notification);

            if (success) {
                successCount.incrementAndGet();
                // 记录成功日志
                saveLog(notification, "success", null);
            } else {
                if (retryCount < MAX_RETRY) {
                    // 重试
                    log.info("Retry sending notification, attempt: {}", retryCount + 1);
                    sendWithRetry(notification, retryCount + 1);
                } else {
                    failedCount.incrementAndGet();
                    String error = "发送失败，已重试" + MAX_RETRY + "次";
                    errors.add(error);
                    saveLog(notification, "failed", error);
                }
            }
        } catch (Exception e) {
            log.error("Failed to send notification", e);
            failedCount.incrementAndGet();
            String error = "发送异常: " + e.getMessage();
            errors.add(error);
            saveLog(notification, "failed", error);
        }
    }

    /**
     * 执行发送
     */
    private boolean doSend(Notification notification) {
        try {
            String channel = notification.getChannel();

            switch (channel) {
                case "site":
                    return sendSiteMessage(notification);
                case "sms":
                    return sendSms(notification);
                case "wechat":
                    return sendWechat(notification);
                case "email":
                    return sendEmail(notification);
                default:
                    log.warn("Unknown notification channel: {}", channel);
                    return false;
            }
        } catch (Exception e) {
            log.error("Failed to send notification", e);
            return false;
        }
    }

    /**
     * 发送站内信
     */
    private boolean sendSiteMessage(Notification notification) {
        // TODO: 实现站内信发送逻辑
        log.info("Sending site message to user: {}", notification.getReceiverId());
        notification.setSendStatus("sent");
        notification.setSendTime(LocalDateTime.now());
        notificationService.updateById(notification);
        return true;
    }

    /**
     * 发送短信
     */
    private boolean sendSms(Notification notification) {
        // TODO: 实现短信发送逻辑
        log.info("Sending SMS to user: {}", notification.getReceiverId());
        notification.setSendStatus("sent");
        notification.setSendTime(LocalDateTime.now());
        notificationService.updateById(notification);
        return true;
    }

    /**
     * 发送微信消息
     */
    private boolean sendWechat(Notification notification) {
        // TODO: 实现微信消息发送逻辑
        log.info("Sending WeChat message to user: {}", notification.getReceiverId());
        notification.setSendStatus("sent");
        notification.setSendTime(LocalDateTime.now());
        notificationService.updateById(notification);
        return true;
    }

    /**
     * 发送邮件
     */
    private boolean sendEmail(Notification notification) {
        // TODO: 实现邮件发送逻辑
        log.info("Sending email to user: {}", notification.getReceiverId());
        notification.setSendStatus("sent");
        notification.setSendTime(LocalDateTime.now());
        notificationService.updateById(notification);
        return true;
    }

    /**
     * 保存发送日志
     */
    private void saveLog(Notification notification, String status, String errorMessage) {
        try {
            NotificationLog log = new NotificationLog();
            log.setNotificationId(notification.getId());
            log.setReceiverId(notification.getReceiverId());
            log.setChannel(notification.getChannel());
            log.setType(notification.getType());
            log.setTitle(notification.getTitle());
            log.setContent(notification.getContent());
            log.setStatus(status);
            log.setFailReason(errorMessage);
            log.setSendTime(LocalDateTime.now());
            notificationLogService.save(log);
        } catch (Exception e) {
            log.error("Failed to save notification log", e);
        }
    }

    /**
     * 构建结果
     */
    private BatchSendResultVO buildResult() {
        BatchSendResultVO result = new BatchSendResultVO();
        result.setTaskId(taskId);
        result.setTotalCount(totalCount);
        result.setSuccessCount(successCount.get());
        result.setFailedCount(failedCount.get());
        result.setStatus(status);

        if (totalCount > 0) {
            result.setProgress((successCount.get() + failedCount.get()) * 100 / totalCount);
        } else {
            result.setProgress(100);
        }

        if (!errors.isEmpty()) {
            result.setErrorMessage(String.join("; ", errors));
        }

        return result;
    }

    /**
     * 获取当前进度
     */
    public BatchSendResultVO getProgress() {
        return buildResult();
    }

    /**
     * 取消任务
     */
    public void cancel() {
        this.status = "cancelled";
        this.endTime = LocalDateTime.now();
    }
}
