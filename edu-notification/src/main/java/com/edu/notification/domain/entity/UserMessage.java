package com.edu.notification.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户消息实体（站内信收件箱）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("msg_user_message")
public class UserMessage extends BaseEntity {

    /**
     * 通知ID
     */
    private Long notificationId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户类型：user-系统用户，student-学员，parent-家长
     */
    private String userType;

    /**
     * 是否已读
     */
    private Integer isRead;

    /**
     * 阅读时间
     */
    private LocalDateTime readTime;

    /**
     * 是否删除（用户删除）
     */
    private Integer isDeleted;

    // ========== 非数据库字段 ==========

    /**
     * 消息标题
     */
    @TableField(exist = false)
    private String title;

    /**
     * 消息内容
     */
    @TableField(exist = false)
    private String content;

    /**
     * 消息类型
     */
    @TableField(exist = false)
    private String type;

    /**
     * 发送时间
     */
    @TableField(exist = false)
    private LocalDateTime sendTime;
}
