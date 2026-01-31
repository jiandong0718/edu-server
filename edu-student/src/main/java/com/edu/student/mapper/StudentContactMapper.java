package com.edu.student.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.student.domain.entity.StudentContact;

/**
 * 学员联系人 Mapper
 */
@DS("student")
public interface StudentContactMapper extends BaseMapper<StudentContact> {
}
