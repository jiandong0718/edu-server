package com.edu.teaching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.exception.BusinessException;
import com.edu.teaching.domain.entity.Homework;
import com.edu.teaching.domain.entity.HomeworkSubmit;
import com.edu.teaching.mapper.HomeworkSubmitMapper;
import com.edu.teaching.service.HomeworkService;
import com.edu.teaching.service.HomeworkSubmitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 作业提交服务实现
 */
@Service
@RequiredArgsConstructor
public class HomeworkSubmitServiceImpl extends ServiceImpl<HomeworkSubmitMapper, HomeworkSubmit> implements HomeworkSubmitService {

    private final HomeworkService homeworkService;

    @Override
    public IPage<HomeworkSubmit> getSubmitPage(IPage<HomeworkSubmit> page, HomeworkSubmit query) {
        return baseMapper.selectSubmitPage(page, query);
    }

    @Override
    public List<HomeworkSubmit> getByHomeworkId(Long homeworkId) {
        return baseMapper.selectByHomeworkId(homeworkId);
    }

    @Override
    public boolean submit(HomeworkSubmit submit) {
        Homework homework = homeworkService.getById(submit.getHomeworkId());
        if (homework == null) {
            throw new BusinessException("作业不存在");
        }

        if (!"published".equals(homework.getStatus())) {
            throw new BusinessException("作业未发布或已关闭");
        }

        // 检查是否已截止
        if (homework.getDeadline() != null && LocalDateTime.now().isAfter(homework.getDeadline())) {
            throw new BusinessException("作业已截止，无法提交");
        }

        // 检查是否已提交
        HomeworkSubmit existing = getOne(new LambdaQueryWrapper<HomeworkSubmit>()
                .eq(HomeworkSubmit::getHomeworkId, submit.getHomeworkId())
                .eq(HomeworkSubmit::getStudentId, submit.getStudentId()));

        if (existing != null) {
            // 更新已有提交
            existing.setContent(submit.getContent());
            existing.setAttachments(submit.getAttachments());
            existing.setSubmitTime(LocalDateTime.now());
            existing.setStatus("pending");
            return updateById(existing);
        }

        submit.setSubmitTime(LocalDateTime.now());
        submit.setStatus("pending");
        return save(submit);
    }

    @Override
    public boolean review(Long id, Integer score, String grade, String comment) {
        HomeworkSubmit submit = getById(id);
        if (submit == null) {
            throw new BusinessException("作业提交记录不存在");
        }

        submit.setScore(score);
        submit.setGrade(grade);
        submit.setComment(comment);
        submit.setStatus("reviewed");
        submit.setReviewTime(LocalDateTime.now());
        // TODO: 设置批改人ID
        // submit.setReviewerId(SecurityUtils.getUserId());

        return updateById(submit);
    }

    @Override
    public boolean returnSubmit(Long id, String reason) {
        HomeworkSubmit submit = getById(id);
        if (submit == null) {
            throw new BusinessException("作业提交记录不存在");
        }

        submit.setStatus("returned");
        submit.setComment(reason);
        submit.setReviewTime(LocalDateTime.now());

        return updateById(submit);
    }

    @Override
    public List<HomeworkSubmit> getStudentSubmits(Long studentId, Long classId) {
        return baseMapper.selectByStudentId(studentId, classId);
    }
}
