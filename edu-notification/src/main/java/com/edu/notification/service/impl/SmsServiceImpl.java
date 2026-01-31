package com.edu.notification.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.edu.common.exception.BusinessException;
import com.edu.notification.config.SmsProperties;
import com.edu.notification.domain.dto.SmsBatchSendDTO;
import com.edu.notification.domain.dto.SmsSendDTO;
import com.edu.notification.domain.dto.SmsTemplateSendDTO;
import com.edu.notification.domain.entity.NotificationLog;
import com.edu.notification.domain.vo.SmsBatchSendResultVO;
import com.edu.notification.domain.vo.SmsSendResultVO;
import com.edu.notification.mapper.NotificationLogMapper;
import com.edu.notification.service.SmsService;
import com.edu.notification.service.sms.SmsSender;
import com.edu.notification.service.sms.SmsSenderFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 短信服务实现类
 */
@Slf4j
@Service
public class SmsServiceImpl implements SmsService {

    private final SmsSenderFactory smsSenderFactory;
    private final NotificationLogMapper notificationLogMapper;
    private final SmsProperties smsProperties;

    public SmsServiceImpl(SmsSenderFactory smsSenderFactory,
                          NotificationLogMapper notificationLogMapper,
                          SmsProperties smsProperties) {
        this.smsSenderFactory = smsSenderFactory;
        this.notificationLogMapper = notificationLogMapper;
        this.smsProperties = smsProperties;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SmsSendResultVO sendSms(SmsSendDTO dto) {
        log.info("发送短信 - 手机号: {}, 内容: {}", dto.getPhone(), dto.getContent());

        // 创建发送记录
        NotificationLog log = createNotificationLog(dto.getPhone(), dto.getContent(),
                dto.getReceiverName(), dto.getReceiverId(), dto.getCampusId(),
                dto.getBizType(), dto.getBizId(), null, dto.getRemark());

        // 发送短信
        SmsSender sender = smsSenderFactory.getSmsSender();
        SmsSender.SmsResult result = sender.sendSms(dto.getPhone(), dto.getContent());

        // 更新发送记录
        updateNotificationLog(log, result);

        // 如果发送失败且未达到最大重试次数，进行重试
        if (!result.isSuccess() && log.getRetryCount() < smsProperties.getMaxRetryCount()) {
            retryAsync(log.getId());
        }

        return buildSendResultVO(log, result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SmsBatchSendResultVO sendBatchSms(SmsBatchSendDTO dto) {
        log.info("批量发送短信 - 手机号数量: {}, 内容: {}", dto.getPhones().size(), dto.getContent());

        List<SmsSendResultVO> details = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;

        SmsSender sender = smsSenderFactory.getSmsSender();

        for (String phone : dto.getPhones()) {
            // 创建发送记录
            NotificationLog log = createNotificationLog(phone, dto.getContent(),
                    null, null, dto.getCampusId(),
                    dto.getBizType(), dto.getBizId(), null, dto.getRemark());

            // 发送短信
            SmsSender.SmsResult result = sender.sendSms(phone, dto.getContent());

            // 更新发送记录
            updateNotificationLog(log, result);

            // 统计结果
            if (result.isSuccess()) {
                successCount++;
            } else {
                failCount++;
                // 如果发送失败且未达到最大重试次数，进行重试
                if (log.getRetryCount() < smsProperties.getMaxRetryCount()) {
                    retryAsync(log.getId());
                }
            }

            details.add(buildSendResultVO(log, result));
        }

        return SmsBatchSendResultVO.builder()
                .total(dto.getPhones().size())
                .successCount(successCount)
                .failCount(failCount)
                .details(details)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SmsSendResultVO sendTemplateSms(SmsTemplateSendDTO dto) {
        log.info("发送模板短信 - 手机号: {}, 模板: {}", dto.getPhone(), dto.getTemplateCode());

        if (!StringUtils.hasText(dto.getPhone())) {
            throw new BusinessException("手机号不能为空");
        }

        // 创建发送记录
        NotificationLog log = createNotificationLog(dto.getPhone(), "模板短信",
                dto.getReceiverName(), dto.getReceiverId(), dto.getCampusId(),
                dto.getBizType(), dto.getBizId(), dto.getTemplateCode(), dto.getRemark());

        // 发送短信
        SmsSender sender = smsSenderFactory.getSmsSender();
        SmsSender.SmsResult result = sender.sendTemplateSms(dto.getPhone(), dto.getTemplateCode(), dto.getParams());

        // 更新发送记录
        updateNotificationLog(log, result);

        // 如果发送失败且未达到最大重试次数，进行重试
        if (!result.isSuccess() && log.getRetryCount() < smsProperties.getMaxRetryCount()) {
            retryAsync(log.getId());
        }

        return buildSendResultVO(log, result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SmsBatchSendResultVO sendBatchTemplateSms(SmsTemplateSendDTO dto) {
        log.info("批量发送模板短信 - 手机号数量: {}, 模板: {}", dto.getPhones().size(), dto.getTemplateCode());

        if (dto.getPhones() == null || dto.getPhones().isEmpty()) {
            throw new BusinessException("手机号列表不能为空");
        }

        List<SmsSendResultVO> details = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;

        SmsSender sender = smsSenderFactory.getSmsSender();

        for (String phone : dto.getPhones()) {
            // 创建发送记录
            NotificationLog log = createNotificationLog(phone, "模板短信",
                    null, null, dto.getCampusId(),
                    dto.getBizType(), dto.getBizId(), dto.getTemplateCode(), dto.getRemark());

            // 发送短信
            SmsSender.SmsResult result = sender.sendTemplateSms(phone, dto.getTemplateCode(), dto.getParams());

            // 更新发送记录
            updateNotificationLog(log, result);

            // 统计结果
            if (result.isSuccess()) {
                successCount++;
            } else {
                failCount++;
                // 如果发送失败且未达到最大重试次数，进行重试
                if (log.getRetryCount() < smsProperties.getMaxRetryCount()) {
                    retryAsync(log.getId());
                }
            }

            details.add(buildSendResultVO(log, result));
        }

        return SmsBatchSendResultVO.builder()
                .total(dto.getPhones().size())
                .successCount(successCount)
                .failCount(failCount)
                .details(details)
                .build();
    }

    @Override
    public Map<String, Object> querySendStatus(Long logId) {
        log.info("查询短信发送状态 - 记录ID: {}", logId);

        NotificationLog notificationLog = notificationLogMapper.selectById(logId);
        if (notificationLog == null) {
            throw new BusinessException("发送记录不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("logId", notificationLog.getId());
        result.put("phone", notificationLog.getReceiver());
        result.put("status", notificationLog.getStatus());
        result.put("sendTime", notificationLog.getSendTime());
        result.put("failReason", notificationLog.getFailReason());
        result.put("retryCount", notificationLog.getRetryCount());
        result.put("thirdPartyId", notificationLog.getThirdPartyId());

        // 如果有第三方消息ID，查询第三方平台状态
        if (StringUtils.hasText(notificationLog.getThirdPartyId())) {
            try {
                SmsSender sender = smsSenderFactory.getSmsSender();
                String thirdPartyStatus = sender.querySendStatus(notificationLog.getThirdPartyId());
                result.put("thirdPartyStatus", thirdPartyStatus);
            } catch (Exception e) {
                log.error("查询第三方平台状态失败", e);
                result.put("thirdPartyStatus", "查询失败");
            }
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SmsSendResultVO retrySend(Long logId) {
        log.info("重试发送短信 - 记录ID: {}", logId);

        NotificationLog notificationLog = notificationLogMapper.selectById(logId);
        if (notificationLog == null) {
            throw new BusinessException("发送记录不存在");
        }

        if (!"failed".equals(notificationLog.getStatus())) {
            throw new BusinessException("只能重试失败的记录");
        }

        if (notificationLog.getRetryCount() >= smsProperties.getMaxRetryCount()) {
            throw new BusinessException("已达到最大重试次数");
        }

        // 更新重试次数
        notificationLog.setRetryCount(notificationLog.getRetryCount() + 1);
        notificationLog.setStatus("sending");
        notificationLogMapper.updateById(notificationLog);

        // 重新发送
        SmsSender sender = smsSenderFactory.getSmsSender();
        SmsSender.SmsResult result;

        if (StringUtils.hasText(notificationLog.getTemplateCode())) {
            // 模板短信
            result = sender.sendTemplateSms(notificationLog.getReceiver(),
                    notificationLog.getTemplateCode(), new HashMap<>());
        } else {
            // 普通短信
            result = sender.sendSms(notificationLog.getReceiver(), notificationLog.getContent());
        }

        // 更新发送记录
        updateNotificationLog(notificationLog, result);

        return buildSendResultVO(notificationLog, result);
    }

    /**
     * 创建通知发送记录
     */
    private NotificationLog createNotificationLog(String phone, String content,
                                                   String receiverName, Long receiverId,
                                                   Long campusId, String bizType, Long bizId,
                                                   String templateCode, String remark) {
        NotificationLog log = new NotificationLog();
        log.setType("sms");
        log.setReceiver(phone);
        log.setReceiverName(receiverName);
        log.setReceiverId(receiverId);
        log.setContent(content);
        log.setStatus("sending");
        log.setRetryCount(0);
        log.setCampusId(campusId);
        log.setBizType(bizType);
        log.setBizId(bizId);
        log.setTemplateCode(templateCode);
        log.setRemark(remark);
        log.setCreateTime(LocalDateTime.now());

        notificationLogMapper.insert(log);
        return log;
    }

    /**
     * 更新通知发送记录
     */
    private void updateNotificationLog(NotificationLog log, SmsSender.SmsResult result) {
        log.setStatus(result.isSuccess() ? "success" : "failed");
        log.setSendTime(LocalDateTime.now());
        log.setThirdPartyId(result.getThirdPartyId());
        log.setFailReason(result.getFailReason());

        if (result.getCost() != null) {
            log.setCost(new BigDecimal(result.getCost()));
        }

        notificationLogMapper.updateById(log);
    }

    /**
     * 构建发送结果VO
     */
    private SmsSendResultVO buildSendResultVO(NotificationLog log, SmsSender.SmsResult result) {
        return SmsSendResultVO.builder()
                .logId(log.getId())
                .phone(log.getReceiver())
                .status(log.getStatus())
                .thirdPartyId(log.getThirdPartyId())
                .failReason(log.getFailReason())
                .sendTime(log.getSendTime())
                .cost(log.getCost() != null ? log.getCost().toString() : null)
                .build();
    }

    /**
     * 异步重试
     */
    @Async
    public void retryAsync(Long logId) {
        try {
            // 延迟一段时间后重试
            Thread.sleep(5000);
            retrySend(logId);
        } catch (Exception e) {
            log.error("异步重试失败 - 记录ID: {}", logId, e);
        }
    }
}
