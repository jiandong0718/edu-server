package com.edu.notification.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

/**
 * 通知预览DTO
 */
@Data
public class NotificationPreviewDTO {

    /**
     * 发送方式：batch-批量，group-分组，campus-校区，class-班级
     */
    @NotBlank(message = "发送方式不能为空")
    private String sendType;

    /**
     * 接收人ID列表（批量发送时使用）
     */
    private List<Long> receiverIds;

    /**
     * 接收人类型（分组、校区发送时使用）
     */
    private String receiverType;

    /**
     * 筛选条件（分组发送时使用）
     */
    private Map<String, Object> filterConditions;

    /**
     * 校区ID列表（校区发送时使用）
     */
    private List<Long> campusIds;

    /**
     * 班级ID列表（班级发送时使用）
     */
    private List<Long> classIds;
}
