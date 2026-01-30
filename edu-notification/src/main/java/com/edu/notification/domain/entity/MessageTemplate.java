package com.edu.notification.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 消息模板实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("msg_template")
public class MessageTemplate extends BaseEntity {

    /**
     * 模板编码
     */
    private String code;

    /**
     * 模板名称
     */
    private String name;

    /**
     * 消息类型：system-系统通知，class-上课提醒，homework-作业通知，payment-缴费提醒，activity-活动通知
     */
    private String type;

    /**
     * 发送渠道：site-站内信，sms-短信，wechat-微信，push-APP推送
     */
    private String channel;

    /**
     * 模板标题
     */
    private String title;

    /**
     * 模板内容（支持变量占位符，如 ${studentName}）
     */
    private String content;

    /**
     * 短信模板ID（第三方平台）
     */
    private String smsTemplateId;

    /**
     * 微信模板ID
     */
    private String wechatTemplateId;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
