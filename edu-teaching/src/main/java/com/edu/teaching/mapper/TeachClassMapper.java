package com.edu.teaching.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.edu.teaching.domain.entity.TeachClass;
import org.apache.ibatis.annotations.Param;

/**
 * 班级 Mapper
 */
@DS("teaching")
public interface TeachClassMapper extends BaseMapper<TeachClass> {

    /**
     * 分页查询班级列表
     */
    IPage<TeachClass> selectClassPage(IPage<TeachClass> page, @Param("query") TeachClass query);
}
