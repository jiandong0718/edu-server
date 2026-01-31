package com.edu.student.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.student.domain.entity.StudentTagRelation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学员标签关联Mapper
 *
 * @author edu
 * @since 2024-01-30
 */
@Mapper
public interface StudentTagRelationMapper extends BaseMapper<StudentTagRelation> {
}
