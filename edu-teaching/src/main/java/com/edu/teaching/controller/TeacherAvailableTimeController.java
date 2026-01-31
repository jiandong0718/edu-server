package com.edu.teaching.controller;

import com.edu.common.core.R;
import com.edu.teaching.domain.dto.BatchSaveAvailableTimeDTO;
import com.edu.teaching.domain.dto.TeacherAvailableTimeDTO;
import com.edu.teaching.domain.entity.TeacherAvailableTime;
import com.edu.teaching.domain.vo.TeacherAvailableTimeVO;
import com.edu.teaching.service.TeacherAvailableTimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教师可用时间配置控制器
 */
@Tag(name = "教师可用时间配置")
@RestController
@RequestMapping("/teaching/teacher/available-time")
@RequiredArgsConstructor
public class TeacherAvailableTimeController {

    private final TeacherAvailableTimeService availableTimeService;

    @Operation(summary = "获取教师可用时间列表")
    @GetMapping("/list")
    public R<List<TeacherAvailableTime>> list(
            @Parameter(description = "教师ID", required = true)
            @RequestParam Long teacherId) {
        return R.ok(availableTimeService.getByTeacherId(teacherId));
    }

    @Operation(summary = "获取教师可用时间列表（VO）")
    @GetMapping("/list-vo")
    public R<List<TeacherAvailableTimeVO>> listVO(
            @Parameter(description = "教师ID", required = true)
            @RequestParam Long teacherId) {
        return R.ok(availableTimeService.getByTeacherIdVO(teacherId));
    }

    @Operation(summary = "获取可用时间详情")
    @GetMapping("/{id}")
    public R<TeacherAvailableTime> getById(
            @Parameter(description = "可用时间ID", required = true)
            @PathVariable Long id) {
        return R.ok(availableTimeService.getById(id));
    }

    @Operation(summary = "新增可用时间")
    @PostMapping
    public R<Boolean> add(@Valid @RequestBody TeacherAvailableTimeDTO dto) {
        return R.ok(availableTimeService.addWithValidation(dto));
    }

    @Operation(summary = "修改可用时间")
    @PutMapping
    public R<Boolean> update(@Valid @RequestBody TeacherAvailableTimeDTO dto) {
        return R.ok(availableTimeService.updateWithValidation(dto));
    }

    @Operation(summary = "删除可用时间")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(
            @Parameter(description = "可用时间ID", required = true)
            @PathVariable Long id) {
        return R.ok(availableTimeService.removeById(id));
    }

    @Operation(summary = "批量保存教师可用时间（旧版本）",
               description = "先删除该教师的所有可用时间配置，再批量保存新的配置。保持向后兼容。")
    @PostMapping("/batch")
    public R<Boolean> batchSave(
            @Parameter(description = "教师ID", required = true)
            @RequestParam Long teacherId,
            @RequestBody List<TeacherAvailableTime> timeList) {
        return R.ok(availableTimeService.batchSave(teacherId, timeList));
    }

    @Operation(summary = "批量保存教师可用时间（新版本）",
               description = "先删除该教师的所有可用时间配置，再批量保存新的配置。包含时间验证和冲突检测。")
    @PostMapping("/batch-save")
    public R<Boolean> batchSaveWithValidation(@Valid @RequestBody BatchSaveAvailableTimeDTO dto) {
        return R.ok(availableTimeService.batchSaveWithValidation(dto));
    }

    @Operation(summary = "检查教师在指定时间是否可用",
               description = "用于排课时检查教师时间是否可用")
    @GetMapping("/check-available")
    public R<Boolean> checkAvailable(
            @Parameter(description = "教师ID", required = true)
            @RequestParam Long teacherId,
            @Parameter(description = "星期几：1-7（1表示周一）", required = true)
            @RequestParam Integer dayOfWeek,
            @Parameter(description = "开始时间（HH:mm）", required = true)
            @RequestParam String startTime,
            @Parameter(description = "结束时间（HH:mm）", required = true)
            @RequestParam String endTime) {
        boolean available = availableTimeService.isTeacherAvailable(teacherId, dayOfWeek, startTime, endTime);
        return R.ok(available);
    }

    @Operation(summary = "启用可用时间")
    @PutMapping("/{id}/enable")
    public R<Boolean> enable(
            @Parameter(description = "可用时间ID", required = true)
            @PathVariable Long id) {
        TeacherAvailableTime entity = new TeacherAvailableTime();
        entity.setId(id);
        entity.setStatus(1);
        return R.ok(availableTimeService.updateById(entity));
    }

    @Operation(summary = "禁用可用时间")
    @PutMapping("/{id}/disable")
    public R<Boolean> disable(
            @Parameter(description = "可用时间ID", required = true)
            @PathVariable Long id) {
        TeacherAvailableTime entity = new TeacherAvailableTime();
        entity.setId(id);
        entity.setStatus(0);
        return R.ok(availableTimeService.updateById(entity));
    }
}
