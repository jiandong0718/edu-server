package com.edu.teaching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.teaching.domain.entity.Course;
import com.edu.teaching.mapper.CourseMapper;
import com.edu.teaching.service.CourseService;
import org.springframework.stereotype.Service;

/**
 * 课程服务实现
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    @Override
    public boolean checkCodeUnique(String code, Long excludeId) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getCode, code);
        if (excludeId != null) {
            wrapper.ne(Course::getId, excludeId);
        }
        return count(wrapper) == 0;
    }

    @Override
    public boolean publish(Long id) {
        Course course = new Course();
        course.setId(id);
        course.setStatus(1);
        return updateById(course);
    }

    @Override
    public boolean unpublish(Long id) {
        Course course = new Course();
        course.setId(id);
        course.setStatus(0);
        return updateById(course);
    }
}
