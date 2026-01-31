package com.edu.teaching.controller;

import com.edu.common.core.R;
import com.edu.teaching.domain.dto.BatchScheduleDTO;
import com.edu.teaching.domain.dto.CancelScheduleDTO;
import com.edu.teaching.domain.dto.ScheduleConflictCheckDTO;
import com.edu.teaching.domain.dto.SubstituteTeacherDTO;
import com.edu.teaching.domain.entity.Schedule;
import com.edu.teaching.domain.vo.BatchScheduleResultVO;
import com.edu.teaching.domain.vo.ScheduleConflictVO;
import com.edu.teaching.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 排课管理控制器
 */
@Tag(name = "排课管理")
@RestController
@RequestMapping("/teaching/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Operation(summary = "查询课表")
    @GetMapping("/list")
    public R<List<Schedule>> list(
            @RequestParam(required = false) Long campusId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) Long teacherId,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Long classroomId) {
        return R.ok(scheduleService.getScheduleList(campusId, startDate, endDate, teacherId, classId, classroomId));
    }

    @Operation(summary = "获取排课详情")
    @GetMapping("/{id}")
    public R<Schedule> getById(@PathVariable Long id) {
        return R.ok(scheduleService.getById(id));
    }

    @Operation(summary = "创建排课")
    @PostMapping
    public R<Boolean> create(@RequestBody Schedule schedule) {
        return R.ok(scheduleService.createSchedule(schedule));
    }

    @Operation(summary = "批量排课（简单版本）")
    @PostMapping("/batch")
    public R<Boolean> batchCreate(@RequestBody BatchScheduleRequest request) {
        return R.ok(scheduleService.batchCreateSchedule(
                request.getClassId(),
                request.getStartDate(),
                request.getEndDate(),
                request.getWeekdays(),
                request.getStartTime(),
                request.getEndTime()
        ));
    }

    @Operation(summary = "批量排课（增强版本）", description = "支持按规则自动生成排课计划，支持跳过节假日、设置总课次等")
    @PostMapping("/batch-enhanced")
    public R<BatchScheduleResultVO> batchCreateEnhanced(@Valid @RequestBody BatchScheduleDTO batchScheduleDTO) {
        return R.ok(scheduleService.batchCreateScheduleEnhanced(batchScheduleDTO));
    }

    @Operation(summary = "调课")
    @PutMapping("/{id}/reschedule")
    public R<Boolean> reschedule(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate newDate,
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime newStartTime,
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime newEndTime,
            @RequestParam(required = false) Long newClassroomId) {
        return R.ok(scheduleService.reschedule(id, newDate, newStartTime, newEndTime, newClassroomId));
    }

    @Operation(summary = "代课（简单版本）", description = "仅更换教师，保留向后兼容")
    @PutMapping("/{id}/substitute")
    public R<Boolean> substitute(@PathVariable Long id, @RequestParam Long newTeacherId) {
        return R.ok(scheduleService.substitute(id, newTeacherId));
    }

    @Operation(summary = "代课（增强版本）", description = "支持代课原因、备注和通知功能")
    @PostMapping("/substitute")
    public R<Boolean> substituteTeacher(@Valid @RequestBody SubstituteTeacherDTO dto) {
        return R.ok(scheduleService.substituteTeacher(dto));
    }

    @Operation(summary = "取消课程（简单版本）", description = "仅更新状态，保留向后兼容")
    @PutMapping("/{id}/cancel")
    public R<Boolean> cancel(@PathVariable Long id) {
        return R.ok(scheduleService.cancelSchedule(id));
    }

    @Operation(summary = "停课（增强版本）", description = "支持停课原因、补课安排和通知功能")
    @PostMapping("/cancel")
    public R<Boolean> cancelScheduleWithDetails(@Valid @RequestBody CancelScheduleDTO dto) {
        return R.ok(scheduleService.cancelScheduleWithDetails(dto));
    }

    @Operation(summary = "检查教师是否可用")
    @GetMapping("/check-teacher-available")
    public R<Boolean> checkTeacherAvailable(
            @RequestParam Long teacherId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate scheduleDate,
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime startTime,
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime endTime,
            @RequestParam(required = false) Long excludeScheduleId) {
        return R.ok(scheduleService.isTeacherAvailable(teacherId, scheduleDate, startTime, endTime, excludeScheduleId));
    }

    @Operation(summary = "检查排课冲突（详细版本）", description = "检查教师、教室、学员时间冲突，以及教师可用时间和教室状态")
    @PostMapping("/check-conflict")
    public R<ScheduleConflictVO> checkConflict(@Valid @RequestBody ScheduleConflictCheckDTO checkDTO) {
        return R.ok(scheduleService.checkConflictDetail(checkDTO));
    }

    @Data
    public static class BatchScheduleRequest {
        private Long classId;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDate;
        private List<Integer> weekdays; // 1-7 表示周一到周日
        @DateTimeFormat(pattern = "HH:mm")
        private LocalTime startTime;
        @DateTimeFormat(pattern = "HH:mm")
        private LocalTime endTime;
    }
}
