package com.edu.teaching.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.Result;
import com.edu.teaching.domain.dto.AttendanceSignInDTO;
import com.edu.teaching.domain.dto.BatchAttendanceSignInDTO;
import com.edu.teaching.domain.dto.BatchAttendanceWithStatusDTO;
import com.edu.teaching.domain.entity.Attendance;
import com.edu.teaching.service.AttendanceService;
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
 * 考勤管理控制器
 */
@Tag(name = "考勤管理", description = "学员考勤签到、统计等功能")
@RestController
@RequestMapping("/teaching/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    /**
     * 分页查询考勤列表
     */
    @Operation(summary = "分页查询考勤列表", description = "支持按排课、学员、状态等条件查询")
    @GetMapping("/page")
    public Result<IPage<Attendance>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            Attendance query) {
        IPage<Attendance> page = new Page<>(pageNum, pageSize);
        return Result.success(attendanceService.getAttendancePage(page, query));
    }

    /**
     * 获取排课考勤列表
     */
    @Operation(summary = "获取排课考勤列表", description = "查询指定排课的所有学员考勤记录")
    @GetMapping("/schedule/{scheduleId}")
    public Result<List<Attendance>> getByScheduleId(
            @Parameter(description = "排课ID") @PathVariable Long scheduleId) {
        return Result.success(attendanceService.getByScheduleId(scheduleId));
    }

    /**
     * 初始化排课考勤
     */
    @Operation(summary = "初始化排课考勤", description = "为班级所有学员创建考勤记录")
    @PostMapping("/init/{scheduleId}")
    public Result<Void> initAttendance(
            @Parameter(description = "排课ID") @PathVariable Long scheduleId) {
        attendanceService.initAttendance(scheduleId);
        return Result.success();
    }

    /**
     * 单个学员签到
     */
    @Operation(summary = "单个学员签到", description = "记录单个学员的签到信息，自动判断迟到状态")
    @PostMapping("/sign-in")
    public Result<Void> signIn(@Validated @RequestBody AttendanceSignInDTO dto) {
        attendanceService.signIn(dto);
        return Result.success();
    }

    /**
     * 单个学员签到（简化版）
     */
    @Operation(summary = "单个学员签到（简化版）", description = "使用URL参数进行签到")
    @PostMapping("/sign-in/simple")
    public Result<Void> signInSimple(
            @Parameter(description = "排课ID") @RequestParam Long scheduleId,
            @Parameter(description = "学员ID") @RequestParam Long studentId,
            @Parameter(description = "考勤状态") @RequestParam String status,
            @Parameter(description = "备注") @RequestParam(required = false) String remark) {
        attendanceService.signIn(scheduleId, studentId, status, remark);
        return Result.success();
    }

    /**
     * 批量签到（统一状态）
     */
    @Operation(summary = "批量签到（统一状态）", description = "为多个学员批量签到，使用相同的考勤状态")
    @PostMapping("/batch-sign-in")
    public Result<Void> batchSignIn(@Validated @RequestBody BatchAttendanceSignInDTO dto) {
        attendanceService.batchSignIn(dto);
        return Result.success();
    }

    /**
     * 批量签到（不同状态）
     */
    @Operation(summary = "批量签到（不同状态）", description = "为多个学员批量签到，可为每个学员设置不同的考勤状态")
    @PostMapping("/batch-sign-in-with-status")
    public Result<Void> batchSignInWithStatus(@Validated @RequestBody BatchAttendanceWithStatusDTO dto) {
        attendanceService.batchSignInWithStatus(dto);
        return Result.success();
    }

    /**
     * 批量签到（简化版）
     */
    @Operation(summary = "批量签到（简化版）", description = "使用URL参数进行批量签到")
    @PostMapping("/batch-sign-in/simple")
    public Result<Void> batchSignInSimple(
            @Parameter(description = "排课ID") @RequestParam Long scheduleId,
            @Parameter(description = "学员ID列表") @RequestParam List<Long> studentIds,
            @Parameter(description = "考勤状态") @RequestParam String status) {
        attendanceService.batchSignIn(scheduleId, studentIds, status);
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
        attendanceService.updateStatus(id, status, remark);
        return Result.success();
    }

    /**
     * 查询学员考勤记录
     */
    @Operation(summary = "查询学员考勤记录", description = "查询指定学员在时间范围内的考勤记录")
    @GetMapping("/student/{studentId}")
    public Result<List<Attendance>> getStudentAttendance(
            @Parameter(description = "学员ID") @PathVariable Long studentId,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.success(attendanceService.getStudentAttendance(studentId, startDate, endDate));
    }

    /**
     * 学员出勤统计
     */
    @Operation(summary = "学员出勤统计", description = "统计学员的出勤、缺勤、迟到、请假次数及出勤率")
    @GetMapping("/stats/student/{studentId}")
    public Result<Map<String, Object>> getStudentStats(
            @Parameter(description = "学员ID") @PathVariable Long studentId,
            @Parameter(description = "班级ID") @RequestParam(required = false) Long classId,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.success(attendanceService.getAttendanceStats(studentId, classId, startDate, endDate));
    }

    /**
     * 班级出勤统计
     */
    @Operation(summary = "班级出勤统计", description = "统计班级整体的出勤情况")
    @GetMapping("/stats/class/{classId}")
    public Result<Map<String, Object>> getClassStats(
            @Parameter(description = "班级ID") @PathVariable Long classId,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.success(attendanceService.getClassAttendanceStats(classId, startDate, endDate));
    }

    /**
     * 自动判断考勤状态
     */
    @Operation(summary = "自动判断考勤状态", description = "根据当前时间和课程时间自动判断考勤状态（出勤/迟到）")
    @GetMapping("/determine-status/{scheduleId}")
    public Result<String> determineStatus(
            @Parameter(description = "排课ID") @PathVariable Long scheduleId) {
        String status = attendanceService.determineAttendanceStatus(scheduleId);
        return Result.success(status);
    }

    /**
     * 综合考勤统计
     */
    @Operation(summary = "综合考勤统计", description = "获取考勤统计数据，包括按班级和按学员的统计")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics(
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "班级ID") @RequestParam(required = false) Long classId,
            @Parameter(description = "学员ID") @RequestParam(required = false) Long studentId) {
        return Result.success(attendanceService.getComprehensiveStats(startDate, endDate, classId, studentId));
    }
}
