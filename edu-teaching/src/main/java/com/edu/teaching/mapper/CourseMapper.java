package com.edu.teaching.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.teaching.domain.entity.Course;

/**
 * 课程 Mapper
 */
@DS("teaching")
public interface CourseMapper extends BaseMapper<Course> {
}
