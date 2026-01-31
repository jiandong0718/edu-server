package com.edu.system.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录统计 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "登录统计视图对象")
public class LoginStatisticsVO {

    @Schema(description = "今日登录总数")
    private Long todayLoginCount;

    @Schema(description = "今日登录成功数")
    private Long todaySuccessCount;

    @Schema(description = "今日登录失败数")
    private Long todayFailureCount;

    @Schema(description = "今日登录成功率（百分比）")
    private Double todaySuccessRate;

    @Schema(description = "异常IP数量（今日多次失败的IP）")
    private Long abnormalIpCount;

    @Schema(description = "总登录次数")
    private Long totalLoginCount;

    @Schema(description = "总成功次数")
    private Long totalSuccessCount;

    @Schema(description = "总失败次数")
    private Long totalFailureCount;

    @Schema(description = "总成功率（百分比）")
    private Double totalSuccessRate;
}
