package com.edu.teaching.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.edu.teaching.domain.entity.Homework;
import org.apache.ibatis.annotations.Param;

/**
 * 作业 Mapper
 */
public interface HomeworkMapper extends BaseMapper<Homework> {

    /**
     * 分页查询作业列表
     */
    IPage<Homework> selectHomeworkPage(IPage<Homework> page, @Param("query") Homework query);
}
