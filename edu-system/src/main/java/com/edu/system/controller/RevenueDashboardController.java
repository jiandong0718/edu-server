package com.edu.system.controller;

import com.edu.common.core.Result;
import com.edu.system.domain.vo.RevenueDashboardVO;
import com.edu.system.service.RevenueDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 营收数据看板控制器
 */
@Tag(name = "营收数据看板")
@RestController
@RequestMapping("/system/dashboard/revenue")
@RequiredArgsConstructor
public class RevenueDashboardController {

    private final RevenueDashboardService revenueDashboardService;

    /**
     * 获取营收数据概览
     */
    @Operation(summary = "获取营收数据概览", description = "获取今日、本周、本月、本年收入，欠费统计，退费统计等")
    @GetMapping("/overview")
    public Result<RevenueDashboardVO.RevenueOverview> getRevenueOverview(
            @Parameter(description = "校区ID，不传则查询所有校区") @RequestParam(required = false) Long campusId) {
        return Result.success(revenueDashboardService.getRevenueOverview(campusId));
    }

    /**
     * 获取营收趋势
     */
    @Operation(summary = "获取营收趋势", description = "获取指定天数内的营收趋势数据")
    @GetMapping("/trend")
    public Result<List<RevenueDashboardVO.RevenueTrendItem>> getRevenueTrend(
            @Parameter(description = "校区ID，不传则查询所有校区") @RequestParam(required = false) Long campusId,
            @Parameter(description = "天数，默认30天") @RequestParam(required = false, defaultValue = "30") Integer days) {
        return Result.success(revenueDashboardService.getRevenueTrend(campusId, days));
    }

    /**
     * 获取收款方式分布
     */
    @Operation(summary = "获取收款方式分布", description = "获取指定时间范围内的收款方式分布统计")
    @GetMapping("/payment-method")
    public Result<List<RevenueDashboardVO.PaymentMethodItem>> getPaymentMethodDistribution(
            @Parameter(description = "校区ID，不传则查询所有校区") @RequestParam(required = false) Long campusId,
            @Parameter(description = "开始日期，格式：yyyy-MM-dd") @RequestParam(required = false) String startDate,
            @Parameter(description = "结束日期，格式：yyyy-MM-dd") @RequestParam(required = false) String endDate) {

        // 默认查询本月数据
        if (startDate == null || startDate.isEmpty()) {
            LocalDate now = LocalDate.now();
            startDate = now.withDayOfMonth(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        }
        if (endDate == null || endDate.isEmpty()) {
            endDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        }

        return Result.success(revenueDashboardService.getPaymentMethodDistribution(campusId, startDate, endDate));
    }

    /**
     * 获取欠费统计
     */
    @Operation(summary = "获取欠费统计", description = "获取欠费学员列表，按欠费金额降序排列")
    @GetMapping("/arrears")
    public Result<List<RevenueDashboardVO.ArrearsItem>> getArrearsList(
            @Parameter(description = "校区ID，不传则查询所有校区") @RequestParam(required = false) Long campusId,
            @Parameter(description = "限制数量，默认20") @RequestParam(required = false, defaultValue = "20") Integer limit) {
        return Result.success(revenueDashboardService.getArrearsList(campusId, limit));
    }

    /**
     * 获取课程营收排行
     */
    @Operation(summary = "获取课程营收排行", description = "获取指定时间范围内的课程营收排行TOP N")
    @GetMapping("/course-revenue")
    public Result<List<RevenueDashboardVO.CourseRevenueItem>> getCourseRevenueRanking(
            @Parameter(description = "校区ID，不传则查询所有校区") @RequestParam(required = false) Long campusId,
            @Parameter(description = "开始日期，格式：yyyy-MM-dd") @RequestParam(required = false) String startDate,
            @Parameter(description = "结束日期，格式：yyyy-MM-dd") @RequestParam(required = false) String endDate,
            @Parameter(description = "限制数量，默认10") @RequestParam(required = false, defaultValue = "10") Integer limit) {

        // 默认查询本月数据
        if (startDate == null || startDate.isEmpty()) {
            LocalDate now = LocalDate.now();
            startDate = now.withDayOfMonth(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        }
        if (endDate == null || endDate.isEmpty()) {
            endDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        }

        return Result.success(revenueDashboardService.getCourseRevenueRanking(campusId, startDate, endDate, limit));
    }
}
