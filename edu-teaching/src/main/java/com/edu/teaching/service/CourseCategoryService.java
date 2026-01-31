package com.edu.teaching.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.teaching.domain.entity.CourseCategory;

import java.util.List;

/**
 * 课程分类服务接口
 *
 * @author edu
 * @since 2024-01-30
 */
public interface CourseCategoryService extends IService<CourseCategory> {

    /**
     * 获取分类树
     *
     * @return 分类树列表
     */
    List<CourseCategory> getCategoryTree();

    /**
     * 检查分类名称是否唯一
     *
     * @param name 分类名称
     * @param parentId 父分类ID
     * @param excludeId 排除的分类ID（用于更新时）
     * @return true-唯一，false-不唯一
     */
    boolean checkNameUnique(String name, Long parentId, Long excludeId);

    /**
     * 检查是否有子分类
     *
     * @param categoryId 分类ID
     * @return true-有子分类，false-无子分类
     */
    boolean hasChildren(Long categoryId);

    /**
     * 检查是否有关联课程
     *
     * @param categoryId 分类ID
     * @return true-有关联课程，false-无关联课程
     */
    boolean hasRelatedCourses(Long categoryId);

    /**
     * 删除分类（带校验）
     *
     * @param categoryId 分类ID
     * @return true-删除成功，false-删除失败
     */
    boolean deleteCategory(Long categoryId);

    /**
     * 批量删除分类（带校验）
     *
     * @param categoryIds 分类ID列表
     * @return true-删除成功，false-删除失败
     */
    boolean deleteCategoryBatch(List<Long> categoryIds);
}
