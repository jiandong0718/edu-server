package com.edu.teaching.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.teaching.domain.entity.CourseCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 课程分类Mapper接口
 *
 * @author edu
 * @since 2024-01-30
 */
@Mapper
public interface CourseCategoryMapper extends BaseMapper<CourseCategory> {
}
