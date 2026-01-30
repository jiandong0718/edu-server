package com.edu.teaching.controller;

import com.edu.common.core.R;
import com.edu.teaching.domain.entity.TeacherSalary;
import com.edu.teaching.service.TeacherSalaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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

    @Operation(summary = "获取教师课酬配置列表")
    @GetMapping("/list")
    public R<List<TeacherSalary>> list(@RequestParam Long teacherId) {
        return R.ok(salaryService.getByTeacherId(teacherId));
    }

    @Operation(summary = "获取课酬配置详情")
    @GetMapping("/{id}")
    public R<TeacherSalary> getById(@PathVariable Long id) {
        return R.ok(salaryService.getById(id));
    }

    @Operation(summary = "新增课酬配置")
    @PostMapping
    public R<Boolean> add(@RequestBody TeacherSalary salary) {
        return R.ok(salaryService.save(salary));
    }

    @Operation(summary = "修改课酬配置")
    @PutMapping
    public R<Boolean> update(@RequestBody TeacherSalary salary) {
        return R.ok(salaryService.updateById(salary));
    }

    @Operation(summary = "删除课酬配置")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(salaryService.removeById(id));
    }

    @Operation(summary = "获取教师指定课程的课酬配置")
    @GetMapping("/course")
    public R<TeacherSalary> getByCourse(@RequestParam Long teacherId, @RequestParam(required = false) Long courseId) {
        return R.ok(salaryService.getByCourseId(teacherId, courseId));
    }
}
