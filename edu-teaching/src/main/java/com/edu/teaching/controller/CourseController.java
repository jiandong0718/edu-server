package com.edu.teaching.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.R;
import com.edu.teaching.domain.entity.Course;
import com.edu.teaching.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程管理控制器
 */
@Tag(name = "课程管理")
@RestController
@RequestMapping("/teaching/course")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "分页查询课程列表")
    @GetMapping("/page")
    public R<Page<Course>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer status) {
        Page<Course> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null, Course::getName, name)
                .eq(type != null, Course::getType, type)
                .eq(status != null, Course::getStatus, status)
                .orderByAsc(Course::getSortOrder);
        courseService.page(page, wrapper);
        return R.ok(page);
    }

    @Operation(summary = "获取所有上架课程")
    @GetMapping("/list")
    public R<List<Course>> list() {
        List<Course> list = courseService.list(new LambdaQueryWrapper<Course>()
                .eq(Course::getStatus, 1)
                .orderByAsc(Course::getSortOrder));
        return R.ok(list);
    }

    @Operation(summary = "获取课程详情")
    @GetMapping("/{id}")
    public R<Course> getById(@PathVariable Long id) {
        return R.ok(courseService.getById(id));
    }

    @Operation(summary = "新增课程")
    @PostMapping
    public R<Boolean> add(@RequestBody Course course) {
        if (!courseService.checkCodeUnique(course.getCode(), null)) {
            return R.fail("课程编码已存在");
        }
        return R.ok(courseService.save(course));
    }

    @Operation(summary = "修改课程")
    @PutMapping
    public R<Boolean> update(@RequestBody Course course) {
        if (!courseService.checkCodeUnique(course.getCode(), course.getId())) {
            return R.fail("课程编码已存在");
        }
        return R.ok(courseService.updateById(course));
    }

    @Operation(summary = "删除课程")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(courseService.removeById(id));
    }

    @Operation(summary = "上架课程")
    @PutMapping("/{id}/publish")
    public R<Boolean> publish(@PathVariable Long id) {
        return R.ok(courseService.publish(id));
    }

    @Operation(summary = "下架课程")
    @PutMapping("/{id}/unpublish")
    public R<Boolean> unpublish(@PathVariable Long id) {
        return R.ok(courseService.unpublish(id));
    }
}
