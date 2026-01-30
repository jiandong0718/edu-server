package com.edu.teaching.controller;

import com.edu.common.core.R;
import com.edu.teaching.domain.entity.TeacherAvailableTime;
import com.edu.teaching.service.TeacherAvailableTimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    public R<List<TeacherAvailableTime>> list(@RequestParam Long teacherId) {
        return R.ok(availableTimeService.getByTeacherId(teacherId));
    }

    @Operation(summary = "获取可用时间详情")
    @GetMapping("/{id}")
    public R<TeacherAvailableTime> getById(@PathVariable Long id) {
        return R.ok(availableTimeService.getById(id));
    }

    @Operation(summary = "新增可用时间")
    @PostMapping
    public R<Boolean> add(@RequestBody TeacherAvailableTime availableTime) {
        return R.ok(availableTimeService.save(availableTime));
    }

    @Operation(summary = "修改可用时间")
    @PutMapping
    public R<Boolean> update(@RequestBody TeacherAvailableTime availableTime) {
        return R.ok(availableTimeService.updateById(availableTime));
    }

    @Operation(summary = "删除可用时间")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(availableTimeService.removeById(id));
    }

    @Operation(summary = "批量保存教师可用时间")
    @PostMapping("/batch")
    public R<Boolean> batchSave(@RequestParam Long teacherId, @RequestBody List<TeacherAvailableTime> timeList) {
        return R.ok(availableTimeService.batchSave(teacherId, timeList));
    }
}
