package com.edu.teaching.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.teaching.domain.entity.CoursePackage;
import org.apache.ibatis.annotations.Select;

/**
 * 课程包 Mapper
 */
@DS("teaching")
public interface CoursePackageMapper extends BaseMapper<CoursePackage> {

    /**
     * 查询最大ID
     */
    @Select("SELECT MAX(id) FROM tch_course_package")
    Long selectMaxId();
}
