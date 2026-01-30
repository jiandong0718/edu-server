package com.edu.notification.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.notification.domain.entity.Notification;

import java.util.Map;

/**
 * 通知服务接口
 */
public interface NotificationService extends IService<Notification> {

    /**
     * 分页查询通知列表
     */
    IPage<Notification> getNotificationPage(IPage<Notification> page, Notification query);

    /**
     * 发送通知
     */
    boolean send(Notification notification);

    /**
     * 使用模板发送通知
     */
    boolean sendByTemplate(String templateCode, Long receiverId, String receiverType, Map<String, Object> params);

    /**
     * 发送上课提醒
     */
    boolean sendClassReminder(Long scheduleId);

    /**
     * 发送作业通知
     */
    boolean sendHomeworkNotice(Long homeworkId);

    /**
     * 发送缴费提醒
     */
    boolean sendPaymentReminder(Long contractId);
}
