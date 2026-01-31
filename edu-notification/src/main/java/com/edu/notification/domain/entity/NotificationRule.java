package com.edu.notification.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 通知规则实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_notification_rule")
public class NotificationRule extends BaseEntity {

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 规则编码
     */
    private String ruleCode;

    /**
     * 规则描述
     */
    private String description;

    /**
     * 事件类型
     */
    private String eventType;

    /**
     * 触发条件，JSON格式
     */
    private String triggerCondition;

    /**
     * 通知类型：SMS-短信, EMAIL-邮件, WECHAT-微信, SYSTEM-站内信，多个用逗号分隔
     */
    private String notificationType;

    /**
     * 消息模板ID
     */
    private Long templateId;

    /**
     * 接收人类型：STUDENT-学员, PARENT-家长, TEACHER-教师, ADVISOR-顾问, ADMIN-管理员，多个用逗号分隔
     */
    private String receiverType;

    /**
     * 发送时间类型：IMMEDIATE-立即, SCHEDULED-定时, DELAYED-延迟
     */
    private String sendTimeType;

    /**
     * 发送时间配置，JSON格式
     */
    private String sendTimeConfig;

    /**
     * 状态：ACTIVE-启用, INACTIVE-禁用
     */
    private String status;

    /**
     * 优先级，数字越大优先级越高
     */
    private Integer priority;

    /**
     * 校区ID，null表示全部校区
     */
    private Long campusId;
}
