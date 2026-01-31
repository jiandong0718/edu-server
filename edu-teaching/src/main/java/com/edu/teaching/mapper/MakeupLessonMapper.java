package com.edu.teaching.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.edu.teaching.domain.entity.MakeupLesson;
import org.apache.ibatis.annotations.Param;

/**
 * 补课记录 Mapper
 */
@DS("teaching")
public interface MakeupLessonMapper extends BaseMapper<MakeupLesson> {

    /**
     * 分页查询补课记录列表
     */
    IPage<MakeupLesson> selectMakeupLessonPage(IPage<MakeupLesson> page, @Param("query") MakeupLesson query);
}
