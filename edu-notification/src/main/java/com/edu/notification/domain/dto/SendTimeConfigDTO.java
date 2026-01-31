package com.edu.notification.domain.dto;

import lombok.Data;

/**
 * 发送时间配置DTO
 */
@Data
public class SendTimeConfigDTO {

    /**
     * 类型：IMMEDIATE-立即, SCHEDULED-定时, DELAYED-延迟
     */
    private String type;

    /**
     * 延迟时间
     */
    private Integer delay;

    /**
     * 时间单位：MINUTES-分钟, HOURS-小时, DAYS-天
     */
    private String unit;

    /**
     * 是否在事件前发送（true-事件前，false-事件后）
     */
    private Boolean beforeEvent;

    /**
     * 定时发送的具体时间（格式：HH:mm）
     */
    private String scheduledTime;
}
