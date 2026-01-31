package com.edu.notification.domain.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 通知发送记录查询DTO
 */
@Data
public class NotificationLogQueryDTO {

    /**
     * 通知类型：sms-短信，site-站内信，email-邮件，wechat-微信，push-推送
     */
    private String type;

    /**
     * 发送状态：pending-待发送，sending-发送中，success-成功，failed-失败
     */
    private String status;

    /**
     * 接收人（手机号/邮箱/用户ID/姓名）
     */
    private String receiver;

    /**
     * 开始日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;

    /**
     * 结束日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;

    /**
     * 校区ID
     */
    private Long campusId;

    /**
     * 业务类型
     */
    private String bizType;

    /**
     * 模板编码
     */
    private String templateCode;
}
