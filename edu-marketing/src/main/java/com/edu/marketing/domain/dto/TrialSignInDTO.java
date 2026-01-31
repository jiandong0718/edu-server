package com.edu.marketing.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 试听签到请求DTO
 *
 * @author edu
 * @since 2024-01-30
 */
@Data
@Schema(description = "试听签到请求")
public class TrialSignInDTO {

    @Schema(description = "试听记录ID", required = true)
    private Long trialId;

    @Schema(description = "签到状态：attended-已到场，absent-未到场", required = true)
    private String status;

    @Schema(description = "备注")
    private String remark;
}
