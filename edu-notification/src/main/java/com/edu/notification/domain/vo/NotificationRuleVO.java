package com.edu.notification.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知规则VO
 */
@Data
@Schema(description = "通知规则VO")
public class NotificationRuleVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "规则名称")
    private String ruleName;

    @Schema(description = "规则编码")
    private String ruleCode;

    @Schema(description = "规则描述")
    private String description;

    @Schema(description = "事件类型")
    private String eventType;

    @Schema(description = "触发条件，JSON格式")
    private String triggerCondition;

    @Schema(description = "通知类型")
    private String notificationType;

    @Schema(description = "消息模板ID")
    private Long templateId;

    @Schema(description = "接收人类型")
    private String receiverType;

    @Schema(description = "发送时间类型")
    private String sendTimeType;

    @Schema(description = "发送时间配置")
    private String sendTimeConfig;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "优先级")
    private Integer priority;

    @Schema(description = "校区ID")
    private Long campusId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
