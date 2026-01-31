package com.edu.teaching.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.Result;
import com.edu.teaching.domain.dto.MakeupLessonDTO;
import com.edu.teaching.domain.entity.MakeupLesson;
import com.edu.teaching.service.MakeupLessonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 补课管理控制器
 */
@RestController
@RequestMapping("/teaching/makeup")
@RequiredArgsConstructor
@Tag(name = "补课管理", description = "补课记录管理接口")
public class MakeupLessonController {

    private final MakeupLessonService makeupLessonService;

    /**
     * 分页查询补课记录列表
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询补课记录列表")
    public Result<IPage<MakeupLesson>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "查询条件") MakeupLesson query) {
        IPage<MakeupLesson> page = new Page<>(pageNum, pageSize);
        return Result.success(makeupLessonService.getMakeupLessonPage(page, query));
    }

    /**
     * 获取补课记录详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取补课记录详情")
    public Result<MakeupLesson> getById(
            @Parameter(description = "补课记录ID") @PathVariable Long id) {
        return Result.success(makeupLessonService.getById(id));
    }

    /**
     * 安排补课
     */
    @PostMapping
    @Operation(summary = "安排补课")
    public Result<Void> arrange(@Valid @RequestBody MakeupLessonDTO dto) {
        MakeupLesson makeupLesson = new MakeupLesson();
        makeupLesson.setLeaveRequestId(dto.getLeaveRequestId());
        makeupLesson.setOriginalScheduleId(dto.getOriginalScheduleId());
        makeupLesson.setMakeupScheduleId(dto.getMakeupScheduleId());
        makeupLesson.setStudentId(dto.getStudentId());
        makeupLesson.setCampusId(dto.getCampusId());
        makeupLesson.setRemark(dto.getRemark());

        makeupLessonService.arrangeMakeup(makeupLesson);
        return Result.success();
    }

    /**
     * 完成补课
     */
    @PutMapping("/{id}/complete")
    @Operation(summary = "完成补课")
    public Result<Void> complete(
            @Parameter(description = "补课记录ID") @PathVariable Long id) {
        makeupLessonService.completeMakeup(id);
        return Result.success();
    }

    /**
     * 取消补课
     */
    @PutMapping("/{id}/cancel")
    @Operation(summary = "取消补课")
    public Result<Void> cancel(
            @Parameter(description = "补课记录ID") @PathVariable Long id) {
        makeupLessonService.cancelMakeup(id);
        return Result.success();
    }

    /**
     * 检查补课时间冲突
     */
    @GetMapping("/check-conflict")
    @Operation(summary = "检查补课时间冲突")
    public Result<Boolean> checkConflict(
            @Parameter(description = "学员ID") @RequestParam Long studentId,
            @Parameter(description = "补课排课ID") @RequestParam Long makeupScheduleId) {
        boolean hasConflict = makeupLessonService.checkMakeupConflict(studentId, makeupScheduleId);
        return Result.success(hasConflict);
    }
}
