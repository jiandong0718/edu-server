package com.edu.teaching.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.teaching.domain.entity.ClassStudent;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 班级学员关联 Mapper
 */
@DS("teaching")
public interface ClassStudentMapper extends BaseMapper<ClassStudent> {

    /**
     * 查询班级的所有学员ID
     */
    List<Long> selectStudentIdsByClassId(@Param("classId") Long classId);

    /**
     * 查询学员所在的所有班级ID
     */
    List<Long> selectClassIdsByStudentId(@Param("studentId") Long studentId);
}
