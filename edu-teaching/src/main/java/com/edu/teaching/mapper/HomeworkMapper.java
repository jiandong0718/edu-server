package com.edu.teaching.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.edu.teaching.domain.entity.Homework;
import com.edu.teaching.domain.vo.HomeworkStatsVO;
import org.apache.ibatis.annotations.Param;

/**
 * 作业 Mapper
 */
@DS("teaching")
public interface HomeworkMapper extends BaseMapper<Homework> {

    /**
     * 分页查询作业列表
     */
    IPage<Homework> selectHomeworkPage(IPage<Homework> page, @Param("query") Homework query);

    /**
     * 查询作业统计信息
     */
    HomeworkStatsVO selectHomeworkStats(@Param("homeworkId") Long homeworkId);
}
