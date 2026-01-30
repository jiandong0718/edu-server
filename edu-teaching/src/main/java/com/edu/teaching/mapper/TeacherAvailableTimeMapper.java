package com.edu.teaching.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.teaching.domain.entity.TeacherAvailableTime;
import org.apache.ibatis.annotations.Mapper;

/**
 * 教师可用时间配置 Mapper
 */
@Mapper
public interface TeacherAvailableTimeMapper extends BaseMapper<TeacherAvailableTime> {
}
