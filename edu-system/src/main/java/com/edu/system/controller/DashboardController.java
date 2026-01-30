package com.edu.system.controller;

import com.edu.common.core.Result;
import com.edu.system.domain.vo.DashboardVO;
import com.edu.system.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据看板控制器
 */
@RestController
@RequestMapping("/system/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * 获取数据看板
     */
    @GetMapping
    public Result<DashboardVO> getDashboard(@RequestParam(required = false) Long campusId) {
        return Result.success(dashboardService.getDashboard(campusId));
    }

    /**
     * 获取学员统计
     */
    @GetMapping("/student")
    public Result<DashboardVO.StudentStats> getStudentStats(@RequestParam(required = false) Long campusId) {
        return Result.success(dashboardService.getStudentStats(campusId));
    }

    /**
     * 获取财务统计
     */
    @GetMapping("/finance")
    public Result<DashboardVO.FinanceStats> getFinanceStats(@RequestParam(required = false) Long campusId) {
        return Result.success(dashboardService.getFinanceStats(campusId));
    }

    /**
     * 获取教学统计
     */
    @GetMapping("/teaching")
    public Result<DashboardVO.TeachingStats> getTeachingStats(@RequestParam(required = false) Long campusId) {
        return Result.success(dashboardService.getTeachingStats(campusId));
    }

    /**
     * 获取营销统计
     */
    @GetMapping("/marketing")
    public Result<DashboardVO.MarketingStats> getMarketingStats(@RequestParam(required = false) Long campusId) {
        return Result.success(dashboardService.getMarketingStats(campusId));
    }
}
