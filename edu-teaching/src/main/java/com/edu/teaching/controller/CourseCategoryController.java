package com.edu.teaching.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.R;
import com.edu.common.exception.BusinessException;
import com.edu.teaching.domain.entity.CourseCategory;
import com.edu.teaching.service.CourseCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程分类管理控制器
 *
 * @author edu
 * @since 2024-01-30
 */
@Tag(name = "课程分类管理")
@RestController
@RequestMapping("/teaching/course-category")
@RequiredArgsConstructor
public class CourseCategoryController {

    private final CourseCategoryService courseCategoryService;

    @Operation(summary = "分页查询分类列表")
    @GetMapping("/page")
    public R<Page<CourseCategory>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "分类名称") @RequestParam(required = false) String name,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId) {
        Page<CourseCategory> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<CourseCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null && !name.isEmpty(), CourseCategory::getName, name)
                .eq(status != null, CourseCategory::getStatus, status)
                .eq(campusId != null, CourseCategory::getCampusId, campusId)
                .orderByAsc(CourseCategory::getSortOrder)
                .orderByDesc(CourseCategory::getCreateTime);
        courseCategoryService.page(page, wrapper);
        return R.ok(page);
    }

    @Operation(summary = "获取分类树")
    @GetMapping("/tree")
    public R<List<CourseCategory>> tree(
            @Parameter(description = "状态（不传则查询所有）") @RequestParam(required = false) Integer status,
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId) {
        LambdaQueryWrapper<CourseCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(status != null, CourseCategory::getStatus, status)
                .eq(campusId != null, CourseCategory::getCampusId, campusId)
                .orderByAsc(CourseCategory::getSortOrder)
                .orderByAsc(CourseCategory::getId);

        List<CourseCategory> allCategories = courseCategoryService.list(wrapper);
        List<CourseCategory> tree = buildTree(allCategories, 0L);
        return R.ok(tree);
    }

    @Operation(summary = "获取分类列表")
    @GetMapping("/list")
    public R<List<CourseCategory>> list(
            @Parameter(description = "父分类ID") @RequestParam(required = false) Long parentId,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId) {
        LambdaQueryWrapper<CourseCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(parentId != null, CourseCategory::getParentId, parentId)
                .eq(status != null, CourseCategory::getStatus, status)
                .eq(campusId != null, CourseCategory::getCampusId, campusId)
                .orderByAsc(CourseCategory::getSortOrder);
        return R.ok(courseCategoryService.list(wrapper));
    }

    @Operation(summary = "获取分类详情")
    @GetMapping("/{id}")
    public R<CourseCategory> getById(@Parameter(description = "分类ID") @PathVariable Long id) {
        CourseCategory category = courseCategoryService.getById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        return R.ok(category);
    }

    @Operation(summary = "新增分类")
    @PostMapping
    public R<Boolean> add(@Validated @RequestBody CourseCategory category) {
        // 校验分类名称
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new BusinessException("分类名称不能为空");
        }

        // 检查名称唯一性
        if (!courseCategoryService.checkNameUnique(category.getName(), category.getParentId(), null)) {
            throw new BusinessException("同级分类名称已存在");
        }

        // 设置默认值
        if (category.getParentId() == null) {
            category.setParentId(0L);
        }
        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        if (category.getSortOrder() == null) {
            category.setSortOrder(0);
        }

        // 如果有父分类，验证父分类是否存在
        if (category.getParentId() != 0L) {
            CourseCategory parent = courseCategoryService.getById(category.getParentId());
            if (parent == null) {
                throw new BusinessException("父分类不存在");
            }
        }

        return R.ok(courseCategoryService.save(category));
    }

    @Operation(summary = "修改分类")
    @PutMapping
    public R<Boolean> update(@Validated @RequestBody CourseCategory category) {
        // 校验ID
        if (category.getId() == null) {
            throw new BusinessException("分类ID不能为空");
        }

        // 验证分类是否存在
        CourseCategory existCategory = courseCategoryService.getById(category.getId());
        if (existCategory == null) {
            throw new BusinessException("分类不存在");
        }

        // 校验分类名称
        if (category.getName() != null && !category.getName().trim().isEmpty()) {
            // 检查名称唯一性
            if (!courseCategoryService.checkNameUnique(category.getName(), category.getParentId(), category.getId())) {
                throw new BusinessException("同级分类名称已存在");
            }
        }

        // 如果修改了父分类，验证父分类是否存在且不能设置为自己或自己的子分类
        if (category.getParentId() != null && !category.getParentId().equals(existCategory.getParentId())) {
            if (category.getParentId().equals(category.getId())) {
                throw new BusinessException("不能将自己设置为父分类");
            }
            if (category.getParentId() != 0L) {
                CourseCategory parent = courseCategoryService.getById(category.getParentId());
                if (parent == null) {
                    throw new BusinessException("父分类不存在");
                }
            }
        }

        return R.ok(courseCategoryService.updateById(category));
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@Parameter(description = "分类ID") @PathVariable Long id) {
        return R.ok(courseCategoryService.deleteCategory(id));
    }

    @Operation(summary = "批量删除分类")
    @DeleteMapping("/batch")
    public R<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要删除的分类");
        }
        return R.ok(courseCategoryService.deleteCategoryBatch(ids));
    }

    @Operation(summary = "更新分类状态")
    @PutMapping("/{id}/status")
    public R<Boolean> updateStatus(
            @Parameter(description = "分类ID") @PathVariable Long id,
            @Parameter(description = "状态：0-禁用，1-启用") @RequestParam Integer status) {
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException("状态值无效");
        }

        CourseCategory category = courseCategoryService.getById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }

        category.setStatus(status);
        return R.ok(courseCategoryService.updateById(category));
    }

    @Operation(summary = "更新分类排序")
    @PutMapping("/{id}/sort")
    public R<Boolean> updateSort(
            @Parameter(description = "分类ID") @PathVariable Long id,
            @Parameter(description = "排序值") @RequestParam Integer sortOrder) {
        CourseCategory category = courseCategoryService.getById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }

        category.setSortOrder(sortOrder);
        return R.ok(courseCategoryService.updateById(category));
    }

    /**
     * 构建树形结构
     *
     * @param categories 所有分类列表
     * @param parentId 父分类ID
     * @return 树形结构列表
     */
    private List<CourseCategory> buildTree(List<CourseCategory> categories, Long parentId) {
        return categories.stream()
                .filter(category -> {
                    Long pid = category.getParentId() != null ? category.getParentId() : 0L;
                    return pid.equals(parentId);
                })
                .peek(category -> {
                    List<CourseCategory> children = buildTree(categories, category.getId());
                    category.setChildren(children.isEmpty() ? null : children);
                })
                .toList();
    }
}
