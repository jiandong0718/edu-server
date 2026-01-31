package com.edu.notification.service;

import com.edu.notification.domain.dto.SmsBatchSendDTO;
import com.edu.notification.domain.dto.SmsSendDTO;
import com.edu.notification.domain.dto.SmsTemplateSendDTO;
import com.edu.notification.domain.vo.SmsBatchSendResultVO;
import com.edu.notification.domain.vo.SmsSendResultVO;

import java.util.Map;

/**
 * 短信服务接口
 */
public interface SmsService {

    /**
     * 发送单条短信
     *
     * @param dto 短信发送DTO
     * @return 发送结果
     */
    SmsSendResultVO sendSms(SmsSendDTO dto);

    /**
     * 批量发送短信
     *
     * @param dto 批量短信发送DTO
     * @return 批量发送结果
     */
    SmsBatchSendResultVO sendBatchSms(SmsBatchSendDTO dto);

    /**
     * 发送模板短信
     *
     * @param dto 模板短信发送DTO
     * @return 发送结果
     */
    SmsSendResultVO sendTemplateSms(SmsTemplateSendDTO dto);

    /**
     * 批量发送模板短信
     *
     * @param dto 模板短信发送DTO
     * @return 批量发送结果
     */
    SmsBatchSendResultVO sendBatchTemplateSms(SmsTemplateSendDTO dto);

    /**
     * 查询发送状态
     *
     * @param logId 发送记录ID
     * @return 发送状态信息
     */
    Map<String, Object> querySendStatus(Long logId);

    /**
     * 重试发送失败的短信
     *
     * @param logId 发送记录ID
     * @return 重试结果
     */
    SmsSendResultVO retrySend(Long logId);
}
