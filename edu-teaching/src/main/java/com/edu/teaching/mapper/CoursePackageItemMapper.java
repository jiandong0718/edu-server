package com.edu.teaching.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.teaching.domain.entity.CoursePackageItem;

/**
 * 课程包明细 Mapper
 */
@DS("teaching")
public interface CoursePackageItemMapper extends BaseMapper<CoursePackageItem> {
}
