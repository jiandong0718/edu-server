package com.edu.notification.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 批量通知DTO
 */
@Data
public class BatchNotificationDTO {

    /**
     * 模板ID
     */
    private Long templateId;

    /**
     * 通知类型：system-系统通知，class-上课提醒，homework-作业通知，payment-缴费提醒，activity-活动通知
     */
    @NotBlank(message = "通知类型不能为空")
    private String notificationType;

    /**
     * 发送渠道：site-站内信，sms-短信，wechat-微信，email-邮件
     */
    @NotBlank(message = "发送渠道不能为空")
    private String channel;

    /**
     * 接收人ID列表
     */
    @NotEmpty(message = "接收人列表不能为空")
    private List<Long> receiverIds;

    /**
     * 消息标题
     */
    @NotBlank(message = "消息标题不能为空")
    private String title;

    /**
     * 消息内容
     */
    @NotBlank(message = "消息内容不能为空")
    private String content;

    /**
     * 模板参数
     */
    private Map<String, Object> params;

    /**
     * 发送时间（可选，为空则立即发送）
     */
    private LocalDateTime sendTime;

    /**
     * 备注
     */
    private String remark;
}
