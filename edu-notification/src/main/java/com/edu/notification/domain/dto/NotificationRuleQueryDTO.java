package com.edu.notification.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 通知规则查询DTO
 */
@Data
@Schema(description = "通知规则查询DTO")
public class NotificationRuleQueryDTO {

    @Schema(description = "规则名称")
    private String ruleName;

    @Schema(description = "规则编码")
    private String ruleCode;

    @Schema(description = "事件类型")
    private String eventType;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "校区ID")
    private Long campusId;
}
