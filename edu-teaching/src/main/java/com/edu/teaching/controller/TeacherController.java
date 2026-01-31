package com.edu.teaching.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.R;
import com.edu.common.exception.BusinessException;
import com.edu.teaching.domain.entity.Teacher;
import com.edu.teaching.service.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教师管理控制器
 */
@Tag(name = "教师管理")
@RestController
@RequestMapping("/teaching/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @Operation(summary = "分页查询教师列表")
    @GetMapping("/page")
    public R<Page<Teacher>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String teacherNo,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long campusId) {
        Page<Teacher> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null, Teacher::getName, name)
                .like(teacherNo != null, Teacher::getTeacherNo, teacherNo)
                .like(phone != null, Teacher::getPhone, phone)
                .eq(status != null, Teacher::getStatus, status)
                .eq(campusId != null, Teacher::getCampusId, campusId)
                .orderByDesc(Teacher::getCreateTime);
        teacherService.page(page, wrapper);
        return R.ok(page);
    }

    @Operation(summary = "获取教师列表")
    @GetMapping("/list")
    public R<List<Teacher>> list(@RequestParam(required = false) Long campusId) {
        if (campusId != null) {
            return R.ok(teacherService.getTeachersByCampusId(campusId));
        }
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teacher::getStatus, "active")
                .orderByAsc(Teacher::getTeacherNo);
        return R.ok(teacherService.list(wrapper));
    }

    @Operation(summary = "获取教师详情")
    @GetMapping("/{id}")
    public R<Teacher> getById(@PathVariable Long id) {
        return R.ok(teacherService.getById(id));
    }

    @Operation(summary = "新增教师")
    @PostMapping
    public R<Boolean> add(@RequestBody Teacher teacher) {
        if (!teacherService.checkTeacherNoUnique(teacher.getTeacherNo(), null)) {
            throw new BusinessException("教师编号已存在");
        }
        return R.ok(teacherService.save(teacher));
    }

    @Operation(summary = "修改教师")
    @PutMapping
    public R<Boolean> update(@RequestBody Teacher teacher) {
        if (!teacherService.checkTeacherNoUnique(teacher.getTeacherNo(), teacher.getId())) {
            throw new BusinessException("教师编号已存在");
        }
        return R.ok(teacherService.updateById(teacher));
    }

    @Operation(summary = "删除教师")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(teacherService.removeById(id));
    }

    @Operation(summary = "批量删除教师")
    @DeleteMapping("/batch")
    public R<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        return R.ok(teacherService.removeByIds(ids));
    }

    @Operation(summary = "修改教师状态（已废弃，请使用 /teaching/teacher/status/change 接口）")
    @Deprecated
    @PutMapping("/{id}/status")
    public R<Boolean> updateStatus(@PathVariable Long id, @RequestParam String status) {
        // 验证状态值
        if (!List.of("active", "on_leave", "resigned").contains(status)) {
            throw new BusinessException("无效的状态值");
        }
        return R.ok(teacherService.updateStatus(id, status));
    }

    @Operation(summary = "根据用户ID获取教师信息")
    @GetMapping("/user/{userId}")
    public R<Teacher> getByUserId(@PathVariable Long userId) {
        return R.ok(teacherService.getByUserId(userId));
    }
}
