package com.edu.teaching.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * 批量排课结果VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "批量排课结果VO")
public class BatchScheduleResultVO {

    @Schema(description = "成功创建的排课数量")
    private Integer successCount;

    @Schema(description = "跳过的日期数量")
    private Integer skippedCount;

    @Schema(description = "失败的日期数量")
    private Integer failedCount;

    @Schema(description = "创建的排课ID列表")
    private List<Long> scheduleIds;

    @Schema(description = "跳过的日期列表（节假日或冲突）")
    private List<SkippedDateInfo> skippedDates;

    @Schema(description = "失败的日期列表")
    private List<FailedDateInfo> failedDates;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "跳过的日期信息")
    public static class SkippedDateInfo {
        @Schema(description = "日期")
        private LocalDate date;

        @Schema(description = "跳过原因")
        private String reason;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "失败的日期信息")
    public static class FailedDateInfo {
        @Schema(description = "日期")
        private LocalDate date;

        @Schema(description = "失败原因")
        private String reason;
    }
}
