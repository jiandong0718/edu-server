package com.edu.notification.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.notification.domain.entity.MessageTemplate;
import com.edu.notification.domain.entity.Notification;
import com.edu.notification.domain.entity.UserMessage;
import com.edu.notification.mapper.NotificationMapper;
import com.edu.notification.service.MessageTemplateService;
import com.edu.notification.service.NotificationService;
import com.edu.notification.service.UserMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 通知服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    private final MessageTemplateService messageTemplateService;
    private final UserMessageService userMessageService;

    @Override
    public IPage<Notification> getNotificationPage(IPage<Notification> page, Notification query) {
        return baseMapper.selectNotificationPage(page, query);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean send(Notification notification) {
        notification.setSendStatus("pending");
        boolean saved = save(notification);

        if (saved) {
            // 异步发送
            doSend(notification);
        }

        return saved;
    }

    @Async
    protected void doSend(Notification notification) {
        try {
            String channel = notification.getChannel();

            switch (channel) {
                case "site":
                    sendSiteMessage(notification);
                    break;
                case "sms":
                    sendSms(notification);
                    break;
                case "wechat":
                    sendWechat(notification);
                    break;
                case "push":
                    sendPush(notification);
                    break;
                default:
                    log.warn("Unknown notification channel: {}", channel);
            }

            // 更新发送状态
            notification.setSendStatus("sent");
            notification.setSendTime(LocalDateTime.now());
            updateById(notification);

        } catch (Exception e) {
            log.error("Failed to send notification: {}", notification.getId(), e);
            notification.setSendStatus("failed");
            updateById(notification);
        }
    }

    /**
     * 发送站内信
     */
    private void sendSiteMessage(Notification notification) {
        // 创建用户消息记录
        if ("user".equals(notification.getReceiverType()) && notification.getReceiverId() != null) {
            UserMessage userMessage = new UserMessage();
            userMessage.setNotificationId(notification.getId());
            userMessage.setUserId(notification.getReceiverId());
            userMessage.setUserType("user");
            userMessage.setIsRead(0);
            userMessage.setIsDeleted(0);
            userMessageService.save(userMessage);
        }
        // TODO: 处理其他接收人类型（all, student, parent, teacher）
    }

    /**
     * 发送短信
     */
    private void sendSms(Notification notification) {
        // TODO: 集成短信服务商（阿里云、腾讯云等）
        log.info("Sending SMS to receiver: {}, content: {}", notification.getReceiverId(), notification.getContent());
    }

    /**
     * 发送微信消息
     */
    private void sendWechat(Notification notification) {
        // TODO: 集成微信公众号/小程序模板消息
        log.info("Sending WeChat message to receiver: {}", notification.getReceiverId());
    }

    /**
     * 发送APP推送
     */
    private void sendPush(Notification notification) {
        // TODO: 集成推送服务（极光、个推等）
        log.info("Sending push notification to receiver: {}", notification.getReceiverId());
    }

    @Override
    public boolean sendByTemplate(String templateCode, Long receiverId, String receiverType, Map<String, Object> params) {
        MessageTemplate template = messageTemplateService.getByCode(templateCode);
        if (template == null) {
            log.warn("Template not found: {}", templateCode);
            return false;
        }

        String content = messageTemplateService.renderContent(templateCode, params);

        Notification notification = new Notification();
        notification.setTitle(template.getTitle());
        notification.setContent(content);
        notification.setType(template.getType());
        notification.setChannel(template.getChannel());
        notification.setReceiverType(receiverType);
        notification.setReceiverId(receiverId);

        return send(notification);
    }

    @Override
    public boolean sendClassReminder(Long scheduleId) {
        // TODO: 查询排课信息，获取班级学员，发送上课提醒
        log.info("Sending class reminder for schedule: {}", scheduleId);
        return true;
    }

    @Override
    public boolean sendHomeworkNotice(Long homeworkId) {
        // TODO: 查询作业信息，获取班级学员，发送作业通知
        log.info("Sending homework notice for homework: {}", homeworkId);
        return true;
    }

    @Override
    public boolean sendPaymentReminder(Long contractId) {
        // TODO: 查询合同信息，发送缴费提醒
        log.info("Sending payment reminder for contract: {}", contractId);
        return true;
    }
}
