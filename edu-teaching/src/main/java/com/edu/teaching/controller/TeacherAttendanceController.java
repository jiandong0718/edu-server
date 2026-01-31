package com.edu.teaching.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.Result;
import com.edu.teaching.domain.dto.TeacherLeaveDTO;
import com.edu.teaching.domain.dto.TeacherSignInDTO;
import com.edu.teaching.domain.dto.TeacherSignOutDTO;
import com.edu.teaching.domain.entity.TeacherAttendance;
import com.edu.teaching.service.TeacherAttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 教师考勤管理控制器
 */
@Tag(name = "教师考勤管理", description = "教师考勤签到、签退、统计等功能")
@RestController
@RequestMapping("/teaching/teacher-attendance")
@RequiredArgsConstructor
public class TeacherAttendanceController {

    private final TeacherAttendanceService teacherAttendanceService;

    /**
     * 分页查询教师考勤列表
     */
    @Operation(summary = "分页查询教师考勤列表", description = "支持按排课、教师、状态等条件查询")
    @GetMapping("/page")
    public Result<IPage<TeacherAttendance>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            TeacherAttendance query) {
        IPage<TeacherAttendance> page = new Page<>(pageNum, pageSize);
        return Result.success(teacherAttendanceService.getTeacherAttendancePage(page, query));
    }

    /**
     * 获取排课教师考勤
     */
    @Operation(summary = "获取排课教师考勤", description = "查询指定排课的教师考勤记录")
    @GetMapping("/schedule/{scheduleId}")
    public Result<TeacherAttendance> getByScheduleId(
            @Parameter(description = "排课ID") @PathVariable Long scheduleId) {
        return Result.success(teacherAttendanceService.getByScheduleId(scheduleId));
    }

    /**
     * 教师签到
     */
    @Operation(summary = "教师签到", description = "记录教师的签到信息，自动判断迟到状态")
    @PostMapping("/sign-in")
    public Result<Void> signIn(@Validated @RequestBody TeacherSignInDTO dto) {
        teacherAttendanceService.signIn(dto);
        return Result.success();
    }

    /**
     * 教师签退
     */
    @Operation(summary = "教师签退", description = "记录教师的签退信息，自动判断早退状态")
    @PostMapping("/sign-out")
    public Result<Void> signOut(@Validated @RequestBody TeacherSignOutDTO dto) {
        teacherAttendanceService.signOut(dto);
        return Result.success();
    }

    /**
     * 教师请假
     */
    @Operation(summary = "教师请假", description = "记录教师请假信息")
    @PostMapping("/leave")
    public Result<Void> leave(@Validated @RequestBody TeacherLeaveDTO dto) {
        teacherAttendanceService.leave(dto);
        return Result.success();
    }

    /**
     * 更新考勤状态
     */
    @Operation(summary = "更新考勤状态", description = "修改已有考勤记录的状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(
            @Parameter(description = "考勤记录ID") @PathVariable Long id,
            @Parameter(description = "考勤状态") @RequestParam String status,
            @Parameter(description = "备注") @RequestParam(required = false) String remark) {
        teacherAttendanceService.updateStatus(id, status, remark);
        return Result.success();
    }

    /**
     * 查询教师考勤记录
     */
    @Operation(summary = "查询教师考勤记录", description = "查询指定教师在时间范围内的考勤记录")
    @GetMapping("/teacher/{teacherId}")
    public Result<List<TeacherAttendance>> getTeacherAttendance(
            @Parameter(description = "教师ID") @PathVariable Long teacherId,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.success(teacherAttendanceService.getTeacherAttendance(teacherId, startDate, endDate));
    }

    /**
     * 教师出勤统计
     */
    @Operation(summary = "教师出勤统计", description = "统计教师的出勤、缺勤、迟到、早退、请假次数及出勤率")
    @GetMapping("/stats/teacher/{teacherId}")
    public Result<Map<String, Object>> getTeacherStats(
            @Parameter(description = "教师ID") @PathVariable Long teacherId,
            @Parameter(description = "班级ID") @RequestParam(required = false) Long classId,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.success(teacherAttendanceService.getTeacherAttendanceStats(teacherId, classId, startDate, endDate));
    }

    /**
     * 班级教师出勤统计
     */
    @Operation(summary = "班级教师出勤统计", description = "统计班级教师整体的出勤情况")
    @GetMapping("/stats/class/{classId}")
    public Result<Map<String, Object>> getClassTeacherStats(
            @Parameter(description = "班级ID") @PathVariable Long classId,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.success(teacherAttendanceService.getClassTeacherAttendanceStats(classId, startDate, endDate));
    }

    /**
     * 计算教师出勤率
     */
    @Operation(summary = "计算教师出勤率", description = "计算指定教师在时间范围内的出勤率")
    @GetMapping("/attendance-rate/{teacherId}")
    public Result<Double> calculateAttendanceRate(
            @Parameter(description = "教师ID") @PathVariable Long teacherId,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.success(teacherAttendanceService.calculateAttendanceRate(teacherId, startDate, endDate));
    }

    /**
     * 教师考勤按日统计
     */
    @Operation(summary = "教师考勤按日统计", description = "按日统计教师考勤情况，包含出勤、缺勤、迟到、早退等数据")
    @GetMapping("/stats/daily/{teacherId}")
    public Result<List<Map<String, Object>>> getDailyStats(
            @Parameter(description = "教师ID") @PathVariable Long teacherId,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.success(teacherAttendanceService.getDailyStats(teacherId, startDate, endDate));
    }

    /**
     * 教师考勤按周统计
     */
    @Operation(summary = "教师考勤按周统计", description = "按周统计教师考勤情况，包含出勤、缺勤、迟到、早退等数据")
    @GetMapping("/stats/weekly/{teacherId}")
    public Result<List<Map<String, Object>>> getWeeklyStats(
            @Parameter(description = "教师ID") @PathVariable Long teacherId,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.success(teacherAttendanceService.getWeeklyStats(teacherId, startDate, endDate));
    }

    /**
     * 教师考勤按月统计
     */
    @Operation(summary = "教师考勤按月统计", description = "按月统计教师考勤情况，包含出勤、缺勤、迟到、早退等数据")
    @GetMapping("/stats/monthly/{teacherId}")
    public Result<List<Map<String, Object>>> getMonthlyStats(
            @Parameter(description = "教师ID") @PathVariable Long teacherId,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.success(teacherAttendanceService.getMonthlyStats(teacherId, startDate, endDate));
    }

    /**
     * 教师考勤汇总统计
     */
    @Operation(summary = "教师考勤汇总统计", description = "获取教师考勤汇总数据，包含总次数、出勤率、迟到率、早退率等")
    @GetMapping("/stats/summary/{teacherId}")
    public Result<Map<String, Object>> getAttendanceSummary(
            @Parameter(description = "教师ID") @PathVariable Long teacherId,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.success(teacherAttendanceService.getAttendanceSummary(teacherId, startDate, endDate));
    }
}
