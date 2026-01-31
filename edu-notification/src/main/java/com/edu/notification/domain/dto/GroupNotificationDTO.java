package com.edu.notification.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 分组通知DTO
 */
@Data
public class GroupNotificationDTO {

    /**
     * 模板ID
     */
    private Long templateId;

    /**
     * 通知类型
     */
    @NotBlank(message = "通知类型不能为空")
    private String notificationType;

    /**
     * 发送渠道
     */
    @NotBlank(message = "发送渠道不能为空")
    private String channel;

    /**
     * 接收人类型：STUDENT-学员，TEACHER-教师，ADVISOR-顾问
     */
    @NotBlank(message = "接收人类型不能为空")
    private String receiverType;

    /**
     * 筛选条件（JSON格式）
     * 例如：{"campusId": 1, "status": "active", "tags": ["VIP"]}
     */
    private Map<String, Object> filterConditions;

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
     * 发送时间（可选）
     */
    private LocalDateTime sendTime;

    /**
     * 备注
     */
    private String remark;
}
