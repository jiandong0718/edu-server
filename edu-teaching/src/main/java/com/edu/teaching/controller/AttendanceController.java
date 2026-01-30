package com.edu.teaching.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.Result;
import com.edu.teaching.domain.entity.Attendance;
import com.edu.teaching.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 考勤管理控制器
 */
@RestController
@RequestMapping("/teaching/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    /**
     * 分页查询考勤列表
     */
    @GetMapping("/page")
    public Result<IPage<Attendance>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            Attendance query) {
        IPage<Attendance> page = new Page<>(pageNum, pageSize);
        return Result.success(attendanceService.getAttendancePage(page, query));
    }

    /**
     * 获取排课考勤列表
     */
    @GetMapping("/schedule/{scheduleId}")
    public Result<List<Attendance>> getByScheduleId(@PathVariable Long scheduleId) {
        return Result.success(attendanceService.getByScheduleId(scheduleId));
    }

    /**
     * 初始化排课考勤
     */
    @PostMapping("/init/{scheduleId}")
    public Result<Void> initAttendance(@PathVariable Long scheduleId) {
        attendanceService.initAttendance(scheduleId);
        return Result.success();
    }

    /**
     * 单个签到
     */
    @PostMapping("/sign-in")
    public Result<Void> signIn(
            @RequestParam Long scheduleId,
            @RequestParam Long studentId,
            @RequestParam String status,
            @RequestParam(required = false) String remark) {
        attendanceService.signIn(scheduleId, studentId, status, remark);
        return Result.success();
    }

    /**
     * 批量签到
     */
    @PostMapping("/batch-sign-in")
    public Result<Void> batchSignIn(
            @RequestParam Long scheduleId,
            @RequestParam List<Long> studentIds,
            @RequestParam String status) {
        attendanceService.batchSignIn(scheduleId, studentIds, status);
        return Result.success();
    }

    /**
     * 更新考勤状态
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String remark) {
        attendanceService.updateStatus(id, status, remark);
        return Result.success();
    }

    /**
     * 查询学员考勤记录
     */
    @GetMapping("/student/{studentId}")
    public Result<List<Attendance>> getStudentAttendance(
            @PathVariable Long studentId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.success(attendanceService.getStudentAttendance(studentId, startDate, endDate));
    }

    /**
     * 学员出勤统计
     */
    @GetMapping("/stats/student/{studentId}")
    public Result<Map<String, Object>> getStudentStats(
            @PathVariable Long studentId,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.success(attendanceService.getAttendanceStats(studentId, classId, startDate, endDate));
    }

    /**
     * 班级出勤统计
     */
    @GetMapping("/stats/class/{classId}")
    public Result<Map<String, Object>> getClassStats(
            @PathVariable Long classId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.success(attendanceService.getClassAttendanceStats(classId, startDate, endDate));
    }
}
