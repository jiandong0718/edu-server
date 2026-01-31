package com.edu.system.controller;

import com.edu.common.core.Result;
import com.edu.system.domain.vo.TeachingDashboardVO;
import com.edu.system.service.TeachingDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * 教学数据看板控制器
 */
@Tag(name = "教学数据看板", description = "教学数据看板相关接口")
@RestController
@RequestMapping("/system/dashboard/teaching")
@RequiredArgsConstructor
public class TeachingDashboardController {

    private final TeachingDashboardService teachingDashboardService;

    /**
     * 获取教学数据概览
     */
    @Operation(summary = "获取教学数据概览", description = "获取学员、班级、教师统计及教学质量指标")
    @GetMapping("/overview")
    public Result<TeachingDashboardVO.Overview> getOverview(
            @Parameter(description = "校区ID，不传则查询所有校区") @RequestParam(required = false) Long campusId) {
        return Result.success(teachingDashboardService.getOverview(campusId));
    }

    /**
     * 获取考勤率趋势
     */
    @Operation(summary = "获取考勤率趋势", description = "获取指定时间范围内的考勤率趋势数据，默认查询最近7天")
    @GetMapping("/attendance-rate")
    public Result<List<TeachingDashboardVO.AttendanceRateItem>> getAttendanceRate(
            @Parameter(description = "校区ID，不传则查询所有校区") @RequestParam(required = false) Long campusId,
            @Parameter(description = "开始日期，格式：yyyy-MM-dd") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期，格式：yyyy-MM-dd") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.success(teachingDashboardService.getAttendanceRateTrend(campusId, startDate, endDate));
    }

    /**
     * 获取班级统计
     */
    @Operation(summary = "获取班级统计", description = "获取班级详细统计信息，包括学员数、出勤率、课节进度等")
    @GetMapping("/class-stats")
    public Result<List<TeachingDashboardVO.ClassStatsItem>> getClassStats(
            @Parameter(description = "校区ID，不传则查询所有校区") @RequestParam(required = false) Long campusId,
            @Parameter(description = "班级状态：pending-待开班，ongoing-进行中，completed-已结业，suspended-已暂停") @RequestParam(required = false) String status) {
        return Result.success(teachingDashboardService.getClassStats(campusId, status));
    }

    /**
     * 获取教师统计
     */
    @Operation(summary = "获取教师统计", description = "获取教师工作量统计，包括负责班级数、课节数、学员数等")
    @GetMapping("/teacher-stats")
    public Result<List<TeachingDashboardVO.TeacherStatsItem>> getTeacherStats(
            @Parameter(description = "校区ID，不传则查询所有校区") @RequestParam(required = false) Long campusId) {
        return Result.success(teachingDashboardService.getTeacherStats(campusId));
    }

    /**
     * 获取课程消耗统计
     */
    @Operation(summary = "获取课程消耗统计", description = "获取各课程的课时消耗情况，按消耗率排序")
    @GetMapping("/course-consumption")
    public Result<List<TeachingDashboardVO.CourseConsumptionItem>> getCourseConsumption(
            @Parameter(description = "校区ID，不传则查询所有校区") @RequestParam(required = false) Long campusId,
            @Parameter(description = "返回数量限制，默认10") @RequestParam(required = false, defaultValue = "10") Integer limit) {
        return Result.success(teachingDashboardService.getCourseConsumption(campusId, limit));
    }

    /**
     * 获取班级状态分布
     */
    @Operation(summary = "获取班级状态分布", description = "获取各状态班级的数量和占比")
    @GetMapping("/class-status-distribution")
    public Result<List<TeachingDashboardVO.ClassStatusDistribution>> getClassStatusDistribution(
            @Parameter(description = "校区ID，不传则查询所有校区") @RequestParam(required = false) Long campusId) {
        return Result.success(teachingDashboardService.getClassStatusDistribution(campusId));
    }
}
