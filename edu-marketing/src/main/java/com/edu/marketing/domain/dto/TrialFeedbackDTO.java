package com.edu.marketing.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 试听反馈请求DTO
 *
 * @author edu
 * @since 2024-01-30
 */
@Data
@Schema(description = "试听反馈请求")
public class TrialFeedbackDTO {

    @Schema(description = "试听记录ID", required = true)
    private Long trialId;

    @Schema(description = "试听反馈", required = true)
    private String feedback;

    @Schema(description = "评分（1-5）", required = true)
    private Integer rating;

    @Schema(description = "备注")
    private String remark;
}
