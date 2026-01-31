package com.edu.teaching.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.teaching.domain.entity.Course;

import java.util.List;

/**
 * 课程服务接口
 */
public interface CourseService extends IService<Course> {

    /**
     * 检查课程编码是否唯一
     */
    boolean checkCodeUnique(String code, Long excludeId);

    /**
     * 上架课程
     */
    boolean onSale(Long id);

    /**
     * 下架课程
     */
    boolean offSale(Long id);

    /**
     * 批量上架课程
     */
    boolean batchOnSale(List<Long> courseIds);

    /**
     * 批量下架课程
     */
    boolean batchOffSale(List<Long> courseIds);

    /**
     * 获取在售课程列表
     */
    List<Course> getOnSaleCourses();

    /**
     * 上架课程（旧方法，保持兼容）
     * @deprecated 使用 onSale 替代
     */
    @Deprecated
    boolean publish(Long id);

    /**
     * 下架课程（旧方法，保持兼容）
     * @deprecated 使用 offSale 替代
     */
    @Deprecated
    boolean unpublish(Long id);
}
