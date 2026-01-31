package com.edu.student.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.R;
import com.edu.student.domain.entity.StudentTag;
import com.edu.student.service.StudentTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学员标签管理控制器
 *
 * @author edu
 * @since 2024-01-30
 */
@Tag(name = "学员标签管理")
@RestController
@RequestMapping("/student/tag")
@RequiredArgsConstructor
public class StudentTagController {

    private final StudentTagService studentTagService;

    @Operation(summary = "分页查询标签列表")
    @GetMapping("/page")
    public R<Page<StudentTag>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "标签名称") @RequestParam(required = false) String name,
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId) {
        Page<StudentTag> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<StudentTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null, StudentTag::getName, name)
                .eq(campusId != null, StudentTag::getCampusId, campusId)
                .orderByAsc(StudentTag::getSortOrder)
                .orderByDesc(StudentTag::getCreateTime);
        studentTagService.page(page, wrapper);
        return R.ok(page);
    }

    @Operation(summary = "获取标签列表")
    @GetMapping("/list")
    public R<List<StudentTag>> list(
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId) {
        LambdaQueryWrapper<StudentTag> wrapper = new LambdaQueryWrapper<>();
        if (campusId != null) {
            wrapper.and(w -> w.eq(StudentTag::getCampusId, campusId).or().isNull(StudentTag::getCampusId));
        }
        wrapper.orderByAsc(StudentTag::getSortOrder);
        return R.ok(studentTagService.list(wrapper));
    }

    @Operation(summary = "获取标签详情")
    @GetMapping("/{id}")
    public R<StudentTag> getById(@Parameter(description = "标签ID") @PathVariable Long id) {
        return R.ok(studentTagService.getById(id));
    }

    @Operation(summary = "新增标签")
    @PostMapping
    public R<Boolean> add(@RequestBody StudentTag tag) {
        return R.ok(studentTagService.save(tag));
    }

    @Operation(summary = "修改标签")
    @PutMapping
    public R<Boolean> update(@RequestBody StudentTag tag) {
        return R.ok(studentTagService.updateById(tag));
    }

    @Operation(summary = "删除标签")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@Parameter(description = "标签ID") @PathVariable Long id) {
        return R.ok(studentTagService.removeById(id));
    }

    @Operation(summary = "批量删除标签")
    @DeleteMapping("/batch")
    public R<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        return R.ok(studentTagService.removeByIds(ids));
    }
}
