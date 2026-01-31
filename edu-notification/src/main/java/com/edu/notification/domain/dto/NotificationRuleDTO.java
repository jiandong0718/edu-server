package com.edu.notification.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 通知规则DTO
 */
@Data
@Schema(description = "通知规则DTO")
public class NotificationRuleDTO {

    @Schema(description = "规则名称")
    @NotBlank(message = "规则名称不能为空")
    private String ruleName;

    @Schema(description = "规则编码")
    @NotBlank(message = "规则编码不能为空")
    private String ruleCode;

    @Schema(description = "规则描述")
    private String description;

    @Schema(description = "事件类型")
    @NotBlank(message = "事件类型不能为空")
    private String eventType;

    @Schema(description = "触发条件，JSON格式")
    private String triggerCondition;

    @Schema(description = "通知类型，多个用逗号分隔")
    @NotBlank(message = "通知类型不能为空")
    private String notificationType;

    @Schema(description = "消息模板ID")
    private Long templateId;

    @Schema(description = "接收人类型，多个用逗号分隔")
    @NotBlank(message = "接收人类型不能为空")
    private String receiverType;

    @Schema(description = "发送时间类型")
    @NotBlank(message = "发送时间类型不能为空")
    private String sendTimeType;

    @Schema(description = "发送时间配置，JSON格式")
    private String sendTimeConfig;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "优先级")
    @NotNull(message = "优先级不能为空")
    private Integer priority;

    @Schema(description = "校区ID")
    private Long campusId;
}
