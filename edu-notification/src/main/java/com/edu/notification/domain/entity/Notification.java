package com.edu.notification.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 通知消息实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("msg_notification")
public class Notification extends BaseEntity {

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息类型：system-系统通知，class-上课提醒，homework-作业通知，payment-缴费提醒，activity-活动通知
     */
    private String type;

    /**
     * 发送渠道：site-站内信，sms-短信，wechat-微信，push-APP推送
     */
    private String channel;

    /**
     * 接收人类型：all-全部，student-学员，parent-家长，teacher-教师，user-指定用户
     */
    private String receiverType;

    /**
     * 接收人ID（当receiverType为user时）
     */
    private Long receiverId;

    /**
     * 校区ID（null表示全部校区）
     */
    private Long campusId;

    /**
     * 关联业务类型
     */
    private String bizType;

    /**
     * 关联业务ID
     */
    private Long bizId;

    /**
     * 发送状态：pending-待发送，sent-已发送，failed-发送失败
     */
    private String sendStatus;

    /**
     * 发送时间
     */
    private LocalDateTime sendTime;

    /**
     * 定时发送时间
     */
    private LocalDateTime scheduledTime;

    /**
     * 发送人ID
     */
    private Long senderId;

    /**
     * 备注
     */
    private String remark;

    // ========== 非数据库字段 ==========

    /**
     * 发送人姓名
     */
    @TableField(exist = false)
    private String senderName;

    /**
     * 接收人姓名
     */
    @TableField(exist = false)
    private String receiverName;
}
