package com.edu.notification.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 通知发送记录VO
 */
@Data
public class NotificationLogVO {

    /**
     * ID
     */
    private Long id;

    /**
     * 通知ID
     */
    private Long notificationId;

    /**
     * 通知类型：sms-短信，site-站内信，email-邮件，wechat-微信，push-推送
     */
    private String type;

    /**
     * 接收人（手机号/邮箱/用户ID）
     */
    private String receiver;

    /**
     * 接收人姓名
     */
    private String receiverName;

    /**
     * 接收人ID
     */
    private Long receiverId;

    /**
     * 通知标题
     */
    private String title;

    /**
     * 通知内容
     */
    private String content;

    /**
     * 发送状态：pending-待发送，sending-发送中，success-成功，failed-失败
     */
    private String status;

    /**
     * 发送时间
     */
    private LocalDateTime sendTime;

    /**
     * 失败原因
     */
    private String failReason;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 校区ID
     */
    private Long campusId;

    /**
     * 校区名称
     */
    private String campusName;

    /**
     * 业务类型
     */
    private String bizType;

    /**
     * 业务ID
     */
    private Long bizId;

    /**
     * 模板编码
     */
    private String templateCode;

    /**
     * 第三方平台消息ID
     */
    private String thirdPartyId;

    /**
     * 发送成本（元）
     */
    private BigDecimal cost;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
