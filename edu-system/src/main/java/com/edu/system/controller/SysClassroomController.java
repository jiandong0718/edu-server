package com.edu.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.R;
import com.edu.system.domain.entity.SysClassroom;
import com.edu.system.service.SysClassroomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教室管理控制器
 */
@Tag(name = "教室管理")
@RestController
@RequestMapping("/system/classroom")
@RequiredArgsConstructor
public class SysClassroomController {

    private final SysClassroomService classroomService;

    @Operation(summary = "分页查询教室列表")
    @GetMapping("/page")
    public R<Page<SysClassroom>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long campusId,
            @RequestParam(required = false) Integer status) {
        Page<SysClassroom> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysClassroom> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null, SysClassroom::getName, name)
                .eq(campusId != null, SysClassroom::getCampusId, campusId)
                .eq(status != null, SysClassroom::getStatus, status)
                .orderByAsc(SysClassroom::getSortOrder);
        classroomService.page(page, wrapper);
        return R.ok(page);
    }

    @Operation(summary = "获取教室列表")
    @GetMapping("/list")
    public R<List<SysClassroom>> list(@RequestParam(required = false) Long campusId) {
        LambdaQueryWrapper<SysClassroom> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(campusId != null, SysClassroom::getCampusId, campusId)
                .eq(SysClassroom::getStatus, 1)
                .orderByAsc(SysClassroom::getSortOrder);
        return R.ok(classroomService.list(wrapper));
    }

    @Operation(summary = "获取教室详情")
    @GetMapping("/{id}")
    public R<SysClassroom> getById(@PathVariable Long id) {
        return R.ok(classroomService.getById(id));
    }

    @Operation(summary = "新增教室")
    @PostMapping
    public R<Boolean> add(@RequestBody SysClassroom classroom) {
        if (!classroomService.checkNameUnique(classroom.getName(), classroom.getCampusId(), null)) {
            return R.fail("该校区下教室名称已存在");
        }
        return R.ok(classroomService.save(classroom));
    }

    @Operation(summary = "修改教室")
    @PutMapping
    public R<Boolean> update(@RequestBody SysClassroom classroom) {
        if (!classroomService.checkNameUnique(classroom.getName(), classroom.getCampusId(), classroom.getId())) {
            return R.fail("该校区下教室名称已存在");
        }
        return R.ok(classroomService.updateById(classroom));
    }

    @Operation(summary = "删除教室")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(classroomService.removeById(id));
    }

    @Operation(summary = "批量删除教室")
    @DeleteMapping("/batch")
    public R<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        return R.ok(classroomService.removeByIds(ids));
    }

    @Operation(summary = "修改状态")
    @PutMapping("/{id}/status")
    public R<Boolean> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        SysClassroom classroom = new SysClassroom();
        classroom.setId(id);
        classroom.setStatus(status);
        return R.ok(classroomService.updateById(classroom));
    }
}
