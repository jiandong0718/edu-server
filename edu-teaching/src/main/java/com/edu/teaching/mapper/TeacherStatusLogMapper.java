package com.edu.teaching.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.teaching.domain.entity.TeacherStatusLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 教师状态变更记录Mapper
 */
@Mapper
public interface TeacherStatusLogMapper extends BaseMapper<TeacherStatusLog> {
}
