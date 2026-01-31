package com.edu.finance.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 催缴提醒DTO
 */
@Data
@Schema(description = "催缴提醒DTO")
public class CollectionReminderDTO {

    /**
     * 合同ID列表
     */
    @Schema(description = "合同ID列表", required = true)
    @NotEmpty(message = "合同ID列表不能为空")
    private List<Long> contractIds;

    /**
     * 提醒方式：sms-短信，message-站内信，both-两者都发
     */
    @Schema(description = "提醒方式", example = "both")
    private String reminderType = "both";

    /**
     * 自定义提醒内容（可选）
     */
    @Schema(description = "自定义提醒内容")
    private String customMessage;
}
