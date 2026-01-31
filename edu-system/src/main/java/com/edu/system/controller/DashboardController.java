package com.edu.system.controller;

import com.edu.common.core.Result;
import com.edu.system.domain.vo.DashboardVO;
import com.edu.system.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据看板控制器
 */
@Tag(name = "数据看板")
@RestController
@RequestMapping("/system/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * 获取数据看板
     */
    @Operation(summary = "获取完整数据看板", description = "获取包含学员、财务、教学、营销的完整数据看板统计")
    @GetMapping
    public Result<DashboardVO> getDashboard(
            @Parameter(description = "校区ID，不传则查询所有校区") @RequestParam(required = false) Long campusId) {
        return Result.success(dashboardService.getDashboard(campusId));
    }

    /**
     * 获取学员统计
     */
    @Operation(summary = "获取学员统计", description = "获取学员数量、状态分布等统计信息")
    @GetMapping("/student")
    public Result<DashboardVO.StudentStats> getStudentStats(
            @Parameter(description = "校区ID，不传则查询所有校区") @RequestParam(required = false) Long campusId) {
        return Result.success(dashboardService.getStudentStats(campusId));
    }

    /**
     * 获取财务统计
     */
    @Operation(summary = "获取财务统计", description = "获取收入、退费、欠费、收款方式分布、收入趋势等统计信息")
    @GetMapping("/finance")
    public Result<DashboardVO.FinanceStats> getFinanceStats(
            @Parameter(description = "校区ID，不传则查询所有校区") @RequestParam(required = false) Long campusId) {
        return Result.success(dashboardService.getFinanceStats(campusId));
    }

    /**
     * 获取教学统计
     */
    @Operation(summary = "获取教学统计", description = "获取课节、班级、教师、学员、出勤率等统计信息")
    @GetMapping("/teaching")
    public Result<DashboardVO.TeachingStats> getTeachingStats(
            @Parameter(description = "校区ID，不传则查询所有校区") @RequestParam(required = false) Long campusId) {
        return Result.success(dashboardService.getTeachingStats(campusId));
    }

    /**
     * 获取营销统计
     */
    @Operation(summary = "获取营销统计", description = "获取线索、试听、转化率、来源分布、趋势等统计信息")
    @GetMapping("/marketing")
    public Result<DashboardVO.MarketingStats> getMarketingStats(
            @Parameter(description = "校区ID，不传则查询所有校区") @RequestParam(required = false) Long campusId) {
        return Result.success(dashboardService.getMarketingStats(campusId));
    }
}
