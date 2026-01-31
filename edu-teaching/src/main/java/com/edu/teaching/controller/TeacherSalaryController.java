package com.edu.teaching.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.R;
import com.edu.teaching.domain.dto.TeacherSalaryDTO;
import com.edu.teaching.domain.vo.TeacherSalaryVO;
import com.edu.teaching.service.TeacherSalaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教师课酬配置控制器
 */
@Tag(name = "教师课酬配置")
@RestController
@RequestMapping("/teaching/teacher/salary")
@RequiredArgsConstructor
public class TeacherSalaryController {

    private final TeacherSalaryService salaryService;

    @Operation(summary = "分页查询课酬配置列表")
    @GetMapping("/page")
    public R<Page<TeacherSalaryVO>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "教师ID") @RequestParam(required = false) Long teacherId,
            @Parameter(description = "课程ID") @RequestParam(required = false) Long courseId,
            @Parameter(description = "班级类型") @RequestParam(required = false) String classType,
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId) {
        return R.ok(salaryService.pageQuery(pageNum, pageSize, teacherId, courseId, classType, campusId));
    }

    @Operation(summary = "获取教师课酬配置列表")
    @GetMapping("/list")
    public R<List<TeacherSalaryVO>> list(@Parameter(description = "教师ID") @RequestParam Long teacherId) {
        return R.ok(salaryService.getByTeacherId(teacherId));
    }

    @Operation(summary = "获取教师当前有效的课酬配置列表")
    @GetMapping("/current")
    public R<List<TeacherSalaryVO>> getCurrentValid(@Parameter(description = "教师ID") @RequestParam Long teacherId) {
        return R.ok(salaryService.getCurrentValidSalaries(teacherId));
    }

    @Operation(summary = "获取课酬配置详情")
    @GetMapping("/{id}")
    public R<TeacherSalaryVO> getById(@Parameter(description = "课酬配置ID") @PathVariable Long id) {
        return R.ok(salaryService.getDetailById(id));
    }

    @Operation(summary = "获取教师指定课程和班级类型的有效课酬配置")
    @GetMapping("/effective")
    public R<TeacherSalaryVO> getEffective(
            @Parameter(description = "教师ID") @RequestParam Long teacherId,
            @Parameter(description = "课程ID") @RequestParam(required = false) Long courseId,
            @Parameter(description = "班级类型") @RequestParam(required = false) String classType) {
        return R.ok(salaryService.getEffectiveSalary(teacherId, courseId, classType));
    }

    @Operation(summary = "新增课酬配置")
    @PostMapping
    public R<Long> add(@Validated @RequestBody TeacherSalaryDTO dto) {
        return R.ok(salaryService.addSalary(dto));
    }

    @Operation(summary = "修改课酬配置")
    @PutMapping
    public R<Boolean> update(@Validated @RequestBody TeacherSalaryDTO dto) {
        return R.ok(salaryService.updateSalary(dto));
    }

    @Operation(summary = "删除课酬配置")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@Parameter(description = "课酬配置ID") @PathVariable Long id) {
        return R.ok(salaryService.deleteSalary(id));
    }

    @Operation(summary = "批量删除课酬配置")
    @DeleteMapping("/batch")
    public R<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        return R.ok(salaryService.deleteBatch(ids));
    }

    @Operation(summary = "获取课酬历史记录")
    @GetMapping("/history")
    public R<List<TeacherSalaryVO>> getHistory(
            @Parameter(description = "教师ID") @RequestParam Long teacherId,
            @Parameter(description = "课程ID") @RequestParam(required = false) Long courseId,
            @Parameter(description = "班级类型") @RequestParam(required = false) String classType) {
        return R.ok(salaryService.getSalaryHistory(teacherId, courseId, classType));
    }
}
