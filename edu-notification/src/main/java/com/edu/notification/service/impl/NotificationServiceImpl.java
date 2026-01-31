package com.edu.notification.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.notification.domain.dto.*;
import com.edu.notification.domain.entity.MessageTemplate;
import com.edu.notification.domain.entity.Notification;
import com.edu.notification.domain.entity.UserMessage;
import com.edu.notification.domain.vo.BatchSendResultVO;
import com.edu.notification.domain.vo.NotificationPreviewVO;
import com.edu.notification.mapper.NotificationMapper;
import com.edu.notification.service.MessageTemplateService;
import com.edu.notification.service.NotificationLogService;
import com.edu.notification.service.NotificationService;
import com.edu.notification.service.UserMessageService;
import com.edu.notification.task.BatchNotificationTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通知服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    private final MessageTemplateService messageTemplateService;
    private final UserMessageService userMessageService;
    private final NotificationLogService notificationLogService;

    /**
     * 任务缓存
     */
    private static final Map<String, BatchNotificationTask> TASK_CACHE = new ConcurrentHashMap<>();

    /**
     * 批量发送限制
     */
    private static final int MAX_BATCH_SIZE = 1000;

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

    @Override
    public BatchSendResultVO sendBatchNotification(BatchNotificationDTO dto) {
        // 验证接收人数量
        if (dto.getReceiverIds().size() > MAX_BATCH_SIZE) {
            throw new IllegalArgumentException("单次批量发送最多支持" + MAX_BATCH_SIZE + "人");
        }

        // 去重接收人
        Set<Long> uniqueReceiverIds = new HashSet<>(dto.getReceiverIds());

        // 创建通知列表
        List<Notification> notifications = new ArrayList<>();
        for (Long receiverId : uniqueReceiverIds) {
            Notification notification = new Notification();
            notification.setTitle(dto.getTitle());
            notification.setContent(dto.getContent());
            notification.setType(dto.getNotificationType());
            notification.setChannel(dto.getChannel());
            notification.setReceiverType("user");
            notification.setReceiverId(receiverId);
            notification.setSendStatus("pending");
            notification.setScheduledTime(dto.getSendTime());
            notification.setRemark(dto.getRemark());
            notifications.add(notification);
        }

        // 创建批量发送任务
        BatchNotificationTask task = new BatchNotificationTask(
                notifications, this, notificationLogService);

        // 缓存任务
        TASK_CACHE.put(task.getTaskId(), task);

        // 异步执行
        task.execute().thenAccept(result -> {
            log.info("Batch notification task completed: {}", result);
            // 任务完成后，保留1小时供查询
            scheduleTaskCleanup(task.getTaskId(), 3600000);
        });

        return task.getProgress();
    }

    @Override
    public BatchSendResultVO sendToGroup(GroupNotificationDTO dto) {
        // 根据筛选条件查询接收人列表
        List<Long> receiverIds = queryReceiverIdsByGroup(dto.getReceiverType(), dto.getFilterConditions());

        if (receiverIds.isEmpty()) {
            throw new IllegalArgumentException("未找到符合条件的接收人");
        }

        // 转换为批量发送DTO
        BatchNotificationDTO batchDTO = new BatchNotificationDTO();
        batchDTO.setTemplateId(dto.getTemplateId());
        batchDTO.setNotificationType(dto.getNotificationType());
        batchDTO.setChannel(dto.getChannel());
        batchDTO.setReceiverIds(receiverIds);
        batchDTO.setTitle(dto.getTitle());
        batchDTO.setContent(dto.getContent());
        batchDTO.setParams(dto.getParams());
        batchDTO.setSendTime(dto.getSendTime());
        batchDTO.setRemark(dto.getRemark());

        return sendBatchNotification(batchDTO);
    }

    @Override
    public BatchSendResultVO sendToCampus(CampusNotificationDTO dto) {
        // 根据校区和接收人类型查询接收人列表
        List<Long> receiverIds = queryReceiverIdsByCampus(dto.getCampusIds(), dto.getReceiverType());

        if (receiverIds.isEmpty()) {
            throw new IllegalArgumentException("未找到符合条件的接收人");
        }

        // 转换为批量发送DTO
        BatchNotificationDTO batchDTO = new BatchNotificationDTO();
        batchDTO.setTemplateId(dto.getTemplateId());
        batchDTO.setNotificationType(dto.getNotificationType());
        batchDTO.setChannel(dto.getChannel());
        batchDTO.setReceiverIds(receiverIds);
        batchDTO.setTitle(dto.getTitle());
        batchDTO.setContent(dto.getContent());
        batchDTO.setParams(dto.getParams());
        batchDTO.setSendTime(dto.getSendTime());
        batchDTO.setRemark(dto.getRemark());

        return sendBatchNotification(batchDTO);
    }

    @Override
    public BatchSendResultVO sendToClass(ClassNotificationDTO dto) {
        // 根据班级查询学员列表
        List<Long> receiverIds = queryReceiverIdsByClass(dto.getClassIds());

        if (receiverIds.isEmpty()) {
            throw new IllegalArgumentException("未找到符合条件的接收人");
        }

        // 转换为批量发送DTO
        BatchNotificationDTO batchDTO = new BatchNotificationDTO();
        batchDTO.setTemplateId(dto.getTemplateId());
        batchDTO.setNotificationType(dto.getNotificationType());
        batchDTO.setChannel(dto.getChannel());
        batchDTO.setReceiverIds(receiverIds);
        batchDTO.setTitle(dto.getTitle());
        batchDTO.setContent(dto.getContent());
        batchDTO.setParams(dto.getParams());
        batchDTO.setSendTime(dto.getSendTime());
        batchDTO.setRemark(dto.getRemark());

        return sendBatchNotification(batchDTO);
    }

    @Override
    public NotificationPreviewVO previewReceivers(NotificationPreviewDTO dto) {
        List<Long> receiverIds = new ArrayList<>();

        switch (dto.getSendType()) {
            case "batch":
                receiverIds = dto.getReceiverIds();
                break;
            case "group":
                receiverIds = queryReceiverIdsByGroup(dto.getReceiverType(), dto.getFilterConditions());
                break;
            case "campus":
                receiverIds = queryReceiverIdsByCampus(dto.getCampusIds(), dto.getReceiverType());
                break;
            case "class":
                receiverIds = queryReceiverIdsByClass(dto.getClassIds());
                break;
            default:
                throw new IllegalArgumentException("不支持的发送方式: " + dto.getSendType());
        }

        // 查询接收人详细信息
        List<NotificationPreviewVO.ReceiverInfo> receivers = queryReceiverDetails(receiverIds);

        NotificationPreviewVO result = new NotificationPreviewVO();
        result.setReceivers(receivers);
        result.setTotalCount(receivers.size());

        return result;
    }

    @Override
    public BatchSendResultVO getSendProgress(String taskId) {
        BatchNotificationTask task = TASK_CACHE.get(taskId);
        if (task == null) {
            throw new IllegalArgumentException("任务不存在或已过期");
        }
        return task.getProgress();
    }

    @Override
    public boolean cancelSend(String taskId) {
        BatchNotificationTask task = TASK_CACHE.get(taskId);
        if (task == null) {
            throw new IllegalArgumentException("任务不存在或已过期");
        }
        task.cancel();
        return true;
    }

    /**
     * 根据分组条件查询接收人ID列表
     */
    private List<Long> queryReceiverIdsByGroup(String receiverType, Map<String, Object> filterConditions) {
        // TODO: 根据接收人类型和筛选条件查询用户ID
        // 这里需要调用对应模块的服务（edu-system, edu-student, edu-teaching）
        log.info("Querying receivers by group: type={}, conditions={}", receiverType, filterConditions);
        return new ArrayList<>();
    }

    /**
     * 根据校区查询接收人ID列表
     */
    private List<Long> queryReceiverIdsByCampus(List<Long> campusIds, String receiverType) {
        // TODO: 根据校区和接收人类型查询用户ID
        log.info("Querying receivers by campus: campusIds={}, type={}", campusIds, receiverType);
        return new ArrayList<>();
    }

    /**
     * 根据班级查询接收人ID列表
     */
    private List<Long> queryReceiverIdsByClass(List<Long> classIds) {
        // TODO: 根据班级查询学员ID
        log.info("Querying receivers by class: classIds={}", classIds);
        return new ArrayList<>();
    }

    /**
     * 查询接收人详细信息
     */
    private List<NotificationPreviewVO.ReceiverInfo> queryReceiverDetails(List<Long> receiverIds) {
        // TODO: 查询用户详细信息
        log.info("Querying receiver details: receiverIds=", receiverIds);
        return new ArrayList<>();
    }

    /**
     * 定时清理任务缓存
     */
    private void scheduleTaskCleanup(String taskId, long delayMillis) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                TASK_CACHE.remove(taskId);
                log.info("Task cache cleaned: ", taskId);
            }
        }, delayMillis);
    }
}
