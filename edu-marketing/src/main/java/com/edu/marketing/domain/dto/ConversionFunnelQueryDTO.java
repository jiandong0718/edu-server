package com.edu.marketing.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 招生转化漏斗查询DTO
 *
 * @author edu
 * @since 2024-01-31
 */
@Data
@Schema(description = "招生转化漏斗查询参数")
public class ConversionFunnelQueryDTO {

    @Schema(description = "校区ID")
    private Long campusId;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "顾问ID")
    private Long advisorId;
}
