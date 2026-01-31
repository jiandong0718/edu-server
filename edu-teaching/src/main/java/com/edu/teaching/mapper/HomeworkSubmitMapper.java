package com.edu.teaching.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.edu.teaching.domain.entity.HomeworkSubmit;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 作业提交 Mapper
 */
@DS("teaching")
public interface HomeworkSubmitMapper extends BaseMapper<HomeworkSubmit> {

    /**
     * 分页查询作业提交列表
     */
    IPage<HomeworkSubmit> selectSubmitPage(IPage<HomeworkSubmit> page, @Param("query") HomeworkSubmit query);

    /**
     * 根据作业ID查询提交列表
     */
    List<HomeworkSubmit> selectByHomeworkId(@Param("homeworkId") Long homeworkId);

    /**
     * 查询学员的作业提交记录
     */
    List<HomeworkSubmit> selectByStudentId(@Param("studentId") Long studentId, @Param("classId") Long classId);
}
