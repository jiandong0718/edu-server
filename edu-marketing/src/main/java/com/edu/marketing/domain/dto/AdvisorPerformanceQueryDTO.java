package com.edu.marketing.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 顾问业绩查询DTO
 *
 * @author edu
 * @since 2024-01-31
 */
@Data
@Schema(description = "顾问业绩查询参数")
public class AdvisorPerformanceQueryDTO {

    @Schema(description = "校区ID")
    private Long campusId;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "顾问ID")
    private Long advisorId;

    @Schema(description = "排序字段：totalLeadCount-线索数，trialCount-试听数，conversionCount-成交数，conversionRate-转化率")
    private String orderBy;

    @Schema(description = "排序方向：asc-升序，desc-降序")
    private String orderDirection;

    @Schema(description = "返回记录数（用于排行榜）")
    private Integer limit;
}
