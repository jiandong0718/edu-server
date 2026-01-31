package com.edu.teaching.controller;

import com.edu.common.core.Result;
import com.edu.teaching.domain.dto.AttendanceStatsQueryDTO;
import com.edu.teaching.domain.dto.TeacherAttendanceStatsQueryDTO;
import com.edu.teaching.service.AttendanceService;
import com.edu.teaching.service.TeacherAttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * 考勤统计控制器
 * 提供学员和教师的综合考勤统计功能
 */
@Tag(name = "考勤统计", description = "学员和教师考勤统计分析")
@RestController
@RequestMapping("/teaching/attendance-stats")
@RequiredArgsConstructor
public class AttendanceStatsController {

    private final AttendanceService attendanceService;
    private final TeacherAttendanceService teacherAttendanceService;

    /**
     * 学员考勤统计（按班级）
     */
    @Operation(summary = "学员考勤统计（按班级）", description = "统计指定班级在时间范围内的学员考勤情况")
    @GetMapping("/student/by-class")
    public Result<Map<String, Object>> getStudentStatsByClass(
            @Parameter(description = "班级ID") @RequestParam Long classId,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        Map<String, Object> stats = attendanceService.getClassAttendanceStats(classId, startDate, endDate);
        return Result.success(stats);
    }

    /**
     * 学员考勤统计（按学员）
     */
    @Operation(summary = "学员考勤统计（按学员）", description = "统计指定学员在时间范围内的考勤情况")
    @GetMapping("/student/by-student")
    public Result<Map<String, Object>> getStudentStatsByStudent(
            @Parameter(description = "学员ID") @RequestParam Long studentId,
            @Parameter(description = "班级ID") @RequestParam(required = false) Long classId,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        Map<String, Object> stats = attendanceService.getAttendanceStats(studentId, classId, startDate, endDate);
        return Result.success(stats);
    }

    /**
     * 学员考勤统计（按时间段）
     */
    @Operation(summary = "学员考勤统计（按时间段）", description = "统计指定时间段内的学员考勤情况，支持多维度筛选")
    @GetMapping("/student/by-period")
    public Result<Map<String, Object>> getStudentStatsByPeriod(AttendanceStatsQueryDTO query) {
        Map<String, Object> stats = attendanceService.getAttendanceStats(
                query.getStudentId(),
                query.getClassId(),
                query.getStartDate(),
                query.getEndDate()
        );
        return Result.success(stats);
    }

    /**
     * 教师考勤统计（按教师）
     */
    @Operation(summary = "教师考勤统计（按教师）", description = "统计指定教师在时间范围内的考勤情况")
    @GetMapping("/teacher/by-teacher")
    public Result<Map<String, Object>> getTeacherStatsByTeacher(
            @Parameter(description = "教师ID") @RequestParam Long teacherId,
            @Parameter(description = "班级ID") @RequestParam(required = false) Long classId,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        Map<String, Object> stats = teacherAttendanceService.getTeacherAttendanceStats(teacherId, classId, startDate, endDate);
        return Result.success(stats);
    }

    /**
     * 教师考勤统计（按班级）
     */
    @Operation(summary = "教师考勤统计（按班级）", description = "统计指定班级在时间范围内的教师考勤情况")
    @GetMapping("/teacher/by-class")
    public Result<Map<String, Object>> getTeacherStatsByClass(
            @Parameter(description = "班级ID") @RequestParam Long classId,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        Map<String, Object> stats = teacherAttendanceService.getClassTeacherAttendanceStats(classId, startDate, endDate);
        return Result.success(stats);
    }

    /**
     * 教师考勤统计（按时间段）
     */
    @Operation(summary = "教师考勤统计（按时间段）", description = "统计指定时间段内的教师考勤情况，支持多维度筛选")
    @GetMapping("/teacher/by-period")
    public Result<Map<String, Object>> getTeacherStatsByPeriod(TeacherAttendanceStatsQueryDTO query) {
        Map<String, Object> stats = teacherAttendanceService.getTeacherAttendanceStats(
                query.getTeacherId(),
                query.getClassId(),
                query.getStartDate(),
                query.getEndDate()
        );
        return Result.success(stats);
    }

    /**
     * 综合考勤统计
     */
    @Operation(summary = "综合考勤统计", description = "获取学员和教师的综合考勤统计数据")
    @GetMapping("/comprehensive")
    public Result<Map<String, Object>> getComprehensiveStats(
            @Parameter(description = "班级ID") @RequestParam(required = false) Long classId,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        Map<String, Object> result = new HashMap<>();

        // 学员考勤统计
        if (classId != null) {
            Map<String, Object> studentStats = attendanceService.getClassAttendanceStats(classId, startDate, endDate);
            result.put("studentStats", studentStats);

            // 教师考勤统计
            Map<String, Object> teacherStats = teacherAttendanceService.getClassTeacherAttendanceStats(classId, startDate, endDate);
            result.put("teacherStats", teacherStats);
        }

        return Result.success(result);
    }

    /**
     * 出勤率对比
     */
    @Operation(summary = "出勤率对比", description = "对比学员和教师的出勤率")
    @GetMapping("/attendance-rate-comparison")
    public Result<Map<String, Object>> getAttendanceRateComparison(
            @Parameter(description = "班级ID") @RequestParam(required = false) Long classId,
            @Parameter(description = "学员ID") @RequestParam(required = false) Long studentId,
            @Parameter(description = "教师ID") @RequestParam(required = false) Long teacherId,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        Map<String, Object> result = new HashMap<>();

        // 学员出勤率
        if (studentId != null) {
            Map<String, Object> studentStats = attendanceService.getAttendanceStats(studentId, classId, startDate, endDate);
            result.put("studentAttendanceRate", studentStats.get("attendanceRate"));
            result.put("studentStats", studentStats);
        } else if (classId != null) {
            Map<String, Object> classStudentStats = attendanceService.getClassAttendanceStats(classId, startDate, endDate);
            result.put("classStudentAttendanceRate", classStudentStats.get("attendanceRate"));
            result.put("classStudentStats", classStudentStats);
        }

        // 教师出勤率
        if (teacherId != null) {
            Double teacherRate = teacherAttendanceService.calculateAttendanceRate(teacherId, startDate, endDate);
            result.put("teacherAttendanceRate", teacherRate);

            Map<String, Object> teacherStats = teacherAttendanceService.getTeacherAttendanceStats(teacherId, classId, startDate, endDate);
            result.put("teacherStats", teacherStats);
        } else if (classId != null) {
            Map<String, Object> classTeacherStats = teacherAttendanceService.getClassTeacherAttendanceStats(classId, startDate, endDate);
            result.put("classTeacherAttendanceRate", classTeacherStats.get("attendanceRate"));
            result.put("classTeacherStats", classTeacherStats);
        }

        return Result.success(result);
    }

    /**
     * 考勤异常统计
     */
    @Operation(summary = "考勤异常统计", description = "统计迟到、早退、缺勤等异常情况")
    @GetMapping("/abnormal-stats")
    public Result<Map<String, Object>> getAbnormalStats(
            @Parameter(description = "班级ID") @RequestParam(required = false) Long classId,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        Map<String, Object> result = new HashMap<>();

        // 学员异常统计
        if (classId != null) {
            Map<String, Object> studentStats = attendanceService.getClassAttendanceStats(classId, startDate, endDate);
            Map<String, Object> studentAbnormal = new HashMap<>();
            studentAbnormal.put("late", studentStats.get("late"));
            studentAbnormal.put("absent", studentStats.get("absent"));
            studentAbnormal.put("leave", studentStats.get("leave"));
            result.put("studentAbnormal", studentAbnormal);

            // 教师异常统计
            Map<String, Object> teacherStats = teacherAttendanceService.getClassTeacherAttendanceStats(classId, startDate, endDate);
            Map<String, Object> teacherAbnormal = new HashMap<>();
            teacherAbnormal.put("late", teacherStats.get("late"));
            teacherAbnormal.put("earlyLeave", teacherStats.get("earlyLeave"));
            teacherAbnormal.put("absent", teacherStats.get("absent"));
            teacherAbnormal.put("leave", teacherStats.get("leave"));
            teacherAbnormal.put("totalLateMinutes", teacherStats.get("totalLateMinutes"));
            teacherAbnormal.put("totalEarlyLeaveMinutes", teacherStats.get("totalEarlyLeaveMinutes"));
            result.put("teacherAbnormal", teacherAbnormal);
        }

        return Result.success(result);
    }
}
