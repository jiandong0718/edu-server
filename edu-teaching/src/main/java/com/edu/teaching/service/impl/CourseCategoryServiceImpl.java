package com.edu.teaching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.exception.BusinessException;
import com.edu.teaching.domain.entity.Course;
import com.edu.teaching.domain.entity.CourseCategory;
import com.edu.teaching.mapper.CourseCategoryMapper;
import com.edu.teaching.mapper.CourseMapper;
import com.edu.teaching.service.CourseCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 课程分类服务实现
 *
 * @author edu
 * @since 2024-01-30
 */
@Service
@RequiredArgsConstructor
public class CourseCategoryServiceImpl extends ServiceImpl<CourseCategoryMapper, CourseCategory> implements CourseCategoryService {

    private final CourseMapper courseMapper;

    @Override
    public List<CourseCategory> getCategoryTree() {
        // 查询所有启用的分类
        List<CourseCategory> allCategories = list(new LambdaQueryWrapper<CourseCategory>()
                .orderByAsc(CourseCategory::getSortOrder)
                .orderByAsc(CourseCategory::getId));

        // 构建树形结构
        return buildTree(allCategories, 0L);
    }

    @Override
    public boolean checkNameUnique(String name, Long parentId, Long excludeId) {
        LambdaQueryWrapper<CourseCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseCategory::getName, name)
                .eq(CourseCategory::getParentId, parentId != null ? parentId : 0L);
        if (excludeId != null) {
            wrapper.ne(CourseCategory::getId, excludeId);
        }
        return count(wrapper) == 0;
    }

    @Override
    public boolean hasChildren(Long categoryId) {
        long count = count(new LambdaQueryWrapper<CourseCategory>()
                .eq(CourseCategory::getParentId, categoryId));
        return count > 0;
    }

    @Override
    public boolean hasRelatedCourses(Long categoryId) {
        long count = courseMapper.selectCount(new LambdaQueryWrapper<Course>()
                .eq(Course::getCategoryId, categoryId));
        return count > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCategory(Long categoryId) {
        // 检查是否有子分类
        if (hasChildren(categoryId)) {
            throw new BusinessException("存在子分类，无法删除");
        }

        // 检查是否有关联课程
        if (hasRelatedCourses(categoryId)) {
            throw new BusinessException("该分类下存在课程，无法删除");
        }

        return removeById(categoryId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCategoryBatch(List<Long> categoryIds) {
        // 检查每个分类
        for (Long categoryId : categoryIds) {
            if (hasChildren(categoryId)) {
                throw new BusinessException("分类ID " + categoryId + " 存在子分类，无法删除");
            }
            if (hasRelatedCourses(categoryId)) {
                throw new BusinessException("分类ID " + categoryId + " 下存在课程，无法删除");
            }
        }

        return removeByIds(categoryIds);
    }

    /**
     * 构建树形结构
     *
     * @param categories 所有分类列表
     * @param parentId 父分类ID
     * @return 树形结构列表
     */
    private List<CourseCategory> buildTree(List<CourseCategory> categories, Long parentId) {
        // 按父ID分组
        Map<Long, List<CourseCategory>> categoryMap = categories.stream()
                .collect(Collectors.groupingBy(category ->
                    category.getParentId() != null ? category.getParentId() : 0L));

        // 递归构建树
        return buildTreeRecursive(categoryMap, parentId);
    }

    /**
     * 递归构建树形结构
     *
     * @param categoryMap 分类映射
     * @param parentId 父分类ID
     * @return 树形结构列表
     */
    private List<CourseCategory> buildTreeRecursive(Map<Long, List<CourseCategory>> categoryMap, Long parentId) {
        List<CourseCategory> children = categoryMap.get(parentId);
        if (children == null || children.isEmpty()) {
            return new ArrayList<>();
        }

        for (CourseCategory category : children) {
            List<CourseCategory> subChildren = buildTreeRecursive(categoryMap, category.getId());
            category.setChildren(subChildren.isEmpty() ? null : subChildren);
        }

        return children;
    }
}
