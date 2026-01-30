package com.edu.teaching.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.teaching.domain.entity.Course;

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
    boolean publish(Long id);

    /**
     * 下架课程
     */
    boolean unpublish(Long id);
}
