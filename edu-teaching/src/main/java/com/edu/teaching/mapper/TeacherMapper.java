package com.edu.teaching.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.teaching.domain.entity.Teacher;

/**
 * 教师 Mapper
 */
@DS("teaching")
public interface TeacherMapper extends BaseMapper<Teacher> {
}
