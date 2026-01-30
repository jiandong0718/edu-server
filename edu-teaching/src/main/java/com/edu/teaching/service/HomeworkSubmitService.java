package com.edu.teaching.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.teaching.domain.entity.HomeworkSubmit;

import java.util.List;

/**
 * 作业提交服务接口
 */
public interface HomeworkSubmitService extends IService<HomeworkSubmit> {

    /**
     * 分页查询作业提交列表
     */
    IPage<HomeworkSubmit> getSubmitPage(IPage<HomeworkSubmit> page, HomeworkSubmit query);

    /**
     * 根据作业ID获取提交列表
     */
    List<HomeworkSubmit> getByHomeworkId(Long homeworkId);

    /**
     * 提交作业
     */
    boolean submit(HomeworkSubmit submit);

    /**
     * 批改作业
     */
    boolean review(Long id, Integer score, String grade, String comment);

    /**
     * 退回作业
     */
    boolean returnSubmit(Long id, String reason);

    /**
     * 查询学员的作业提交记录
     */
    List<HomeworkSubmit> getStudentSubmits(Long studentId, Long classId);
}
