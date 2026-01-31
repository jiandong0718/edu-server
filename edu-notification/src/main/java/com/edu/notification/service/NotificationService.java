package com.edu.notification.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.notification.domain.dto.*;
import com.edu.notification.domain.entity.Notification;
import com.edu.notification.domain.vo.BatchSendResultVO;
import com.edu.notification.domain.vo.NotificationPreviewVO;

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

    /**
     * 批量发送通知
     */
    BatchSendResultVO sendBatchNotification(BatchNotificationDTO dto);

    /**
     * 按分组发送通知
     */
    BatchSendResultVO sendToGroup(GroupNotificationDTO dto);

    /**
     * 按校区发送通知
     */
    BatchSendResultVO sendToCampus(CampusNotificationDTO dto);

    /**
     * 按班级发送通知
     */
    BatchSendResultVO sendToClass(ClassNotificationDTO dto);

    /**
     * 预览接收人列表
     */
    NotificationPreviewVO previewReceivers(NotificationPreviewDTO dto);

    /**
     * 获取发送进度
     */
    BatchSendResultVO getSendProgress(String taskId);

    /**
     * 取消发送
     */
    boolean cancelSend(String taskId);
}
