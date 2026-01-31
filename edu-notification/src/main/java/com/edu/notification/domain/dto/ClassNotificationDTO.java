package com.edu.notification.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 班级通知DTO
 */
@Data
public class ClassNotificationDTO {

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
     * 班级ID列表
     */
    @NotEmpty(message = "班级列表不能为空")
    private List<Long> classIds;

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
