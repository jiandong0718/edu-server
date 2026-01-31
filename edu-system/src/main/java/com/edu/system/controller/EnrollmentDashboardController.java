package com.edu.system.controller;

import com.edu.common.core.Result;
import com.edu.system.domain.vo.EnrollmentDashboardVO;
import com.edu.system.service.EnrollmentDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 招生数据看板控制器
 */
@Tag(name = "招生数据看板")
@RestController
@RequestMapping("/dashboard/enrollment")
@RequiredArgsConstructor
public class EnrollmentDashboardController {

    private final EnrollmentDashboardService enrollmentDashboardService;

    /**
     * 获取招生数据概览
     */
    @Operation(summary = "获取招生数据概览", description = "获取线索、试听、转化等核心指标")
    @GetMapping("/overview")
    public Result<EnrollmentDashboardVO.Overview> getOverview(
            @Parameter(description = "校区ID，不传则查询所有校区") @RequestParam(required = false) Long campusId,
            @Parameter(description = "时间范围：today/week/month/custom") @RequestParam(defaultValue = "month") String timeRange,
            @Parameter(description = "开始日期（自定义时间范围时使用）") @RequestParam(required = false) String startDate,
            @Parameter(description = "结束日期（自定义时间范围时使用）") @RequestParam(required = false) String endDate) {
        return Result.success(enrollmentDashboardService.getOverview(campusId, timeRange, startDate, endDate));
    }

    /**
     * 获取招生趋势数据
     */
    @Operation(summary = "获取招生趋势", description = "获取按日期统计的招生趋势数据")
    @GetMapping("/trend")
    public Result<EnrollmentDashboardVO.Trend> getTrend(
            @Parameter(description = "校区ID，不传则查询所有校区") @RequestParam(required = false) Long campusId,
            @Parameter(description = "天数") @RequestParam(defaultValue = "30") Integer days) {
        return Result.success(enrollmentDashboardService.getTrend(campusId, days));
    }

    /**
     * 获取转化漏斗数据
     */
    @Operation(summary = "获取转化漏斗", description = "获取从线索到成交的转化漏斗数据")
    @GetMapping("/funnel")
    public Result<EnrollmentDashboardVO.Funnel> getFunnel(
            @Parameter(description = "校区ID，不传则查询所有校区") @RequestParam(required = false) Long campusId,
            @Parameter(description = "时间范围：today/week/month/custom") @RequestParam(defaultValue = "month") String timeRange,
            @Parameter(description = "开始日期（自定义时间范围时使用）") @RequestParam(required = false) String startDate,
            @Parameter(description = "结束日期（自定义时间范围时使用）") @RequestParam(required = false) String endDate) {
        return Result.success(enrollmentDashboardService.getFunnel(campusId, timeRange, startDate, endDate));
    }

    /**
     * 获取线索来源分布
     */
    @Operation(summary = "获取线索来源分布", description = "获取各渠道线索数量和占比")
    @GetMapping("/source")
    public Result<EnrollmentDashboardVO.SourceDistribution> getSourceDistribution(
            @Parameter(description = "校区ID，不传则查询所有校区") @RequestParam(required = false) Long campusId,
            @Parameter(description = "时间范围：today/week/month/custom") @RequestParam(defaultValue = "month") String timeRange,
            @Parameter(description = "开始日期（自定义时间范围时使用）") @RequestParam(required = false) String startDate,
            @Parameter(description = "结束日期（自定义时间范围时使用）") @RequestParam(required = false) String endDate) {
        return Result.success(enrollmentDashboardService.getSourceDistribution(campusId, timeRange, startDate, endDate));
    }

    /**
     * 获取顾问排行榜
     */
    @Operation(summary = "获取顾问排行榜", description = "获取顾问业绩排行（TOP 10）")
    @GetMapping("/advisor-ranking")
    public Result<EnrollmentDashboardVO.AdvisorRanking> getAdvisorRanking(
            @Parameter(description = "校区ID，不传则查询所有校区") @RequestParam(required = false) Long campusId,
            @Parameter(description = "时间范围：today/week/month/custom") @RequestParam(defaultValue = "month") String timeRange,
            @Parameter(description = "开始日期（自定义时间范围时使用）") @RequestParam(required = false) String startDate,
            @Parameter(description = "结束日期（自定义时间范围时使用）") @RequestParam(required = false) String endDate,
            @Parameter(description = "排行数量") @RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(enrollmentDashboardService.getAdvisorRanking(campusId, timeRange, startDate, endDate, limit));
    }
}
