package com.edu.student.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.student.domain.entity.StudentTag;

/**
 * 学员标签 Mapper
 */
@DS("student")
public interface StudentTagMapper extends BaseMapper<StudentTag> {
}
