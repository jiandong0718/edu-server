package com.edu.teaching.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.teaching.domain.entity.TeacherSalary;
import org.apache.ibatis.annotations.Mapper;

/**
 * 教师课酬配置 Mapper
 */
@Mapper
public interface TeacherSalaryMapper extends BaseMapper<TeacherSalary> {
}
