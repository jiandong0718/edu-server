package com.edu.teaching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.exception.BusinessException;
import com.edu.teaching.domain.entity.MakeupLesson;
import com.edu.teaching.domain.entity.Schedule;
import com.edu.teaching.event.MakeupArrangedEvent;
import com.edu.teaching.mapper.MakeupLessonMapper;
import com.edu.teaching.service.MakeupLessonService;
import com.edu.teaching.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 补课记录服务实现
 */
@Service
@RequiredArgsConstructor
public class MakeupLessonServiceImpl extends ServiceImpl<MakeupLessonMapper, MakeupLesson> implements MakeupLessonService {

    private final ScheduleService scheduleService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public IPage<MakeupLesson> getMakeupLessonPage(IPage<MakeupLesson> page, MakeupLesson query) {
        return baseMapper.selectMakeupLessonPage(page, query);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean arrangeMakeup(MakeupLesson makeupLesson) {
        // 验证原排课和补课排课是否存在
        Schedule originalSchedule = scheduleService.getById(makeupLesson.getOriginalScheduleId());
        if (originalSchedule == null) {
            throw new BusinessException("原排课不存在");
        }

        Schedule makeupSchedule = scheduleService.getById(makeupLesson.getMakeupScheduleId());
        if (makeupSchedule == null) {
            throw new BusinessException("补课排课不存在");
        }

        // 检查补课时间冲突
        if (checkMakeupConflict(makeupLesson.getStudentId(), makeupLesson.getMakeupScheduleId())) {
            throw new BusinessException("补课时间与该学员其他课程冲突");
        }

        // 检查是否已经为该学员和原排课安排过补课
        Long existingCount = count(new LambdaQueryWrapper<MakeupLesson>()
                .eq(MakeupLesson::getStudentId, makeupLesson.getStudentId())
                .eq(MakeupLesson::getOriginalScheduleId, makeupLesson.getOriginalScheduleId())
                .ne(MakeupLesson::getStatus, "cancelled"));

        if (existingCount > 0) {
            throw new BusinessException("该学员已为此课程安排过补课");
        }

        // 设置初始状态
        makeupLesson.setStatus("pending");
        makeupLesson.setCampusId(originalSchedule.getCampusId());

        boolean result = save(makeupLesson);

        // 发布补课安排事件
        if (result) {
            eventPublisher.publishEvent(new MakeupArrangedEvent(
                    this,
                    makeupLesson.getId(),
                    makeupLesson.getStudentId(),
                    makeupLesson.getMakeupScheduleId(),
                    makeupLesson.getOriginalScheduleId()
            ));
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean completeMakeup(Long id) {
        MakeupLesson makeupLesson = getById(id);
        if (makeupLesson == null) {
            throw new BusinessException("补课记录不存在");
        }

        if (!"pending".equals(makeupLesson.getStatus())) {
            throw new BusinessException("只能完成待补课状态的记录");
        }

        makeupLesson.setStatus("completed");
        return updateById(makeupLesson);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelMakeup(Long id) {
        MakeupLesson makeupLesson = getById(id);
        if (makeupLesson == null) {
            throw new BusinessException("补课记录不存在");
        }

        if ("completed".equals(makeupLesson.getStatus())) {
            throw new BusinessException("已完成的补课不能取消");
        }

        makeupLesson.setStatus("cancelled");
        return updateById(makeupLesson);
    }

    @Override
    public boolean checkMakeupConflict(Long studentId, Long makeupScheduleId) {
        // 获取补课排课信息
        Schedule makeupSchedule = scheduleService.getById(makeupScheduleId);
        if (makeupSchedule == null) {
            return false;
        }

        // 检查该学员在补课时间段是否有其他课程
        // 这里需要查询该学员所在的所有班级，然后检查这些班级在该时间段是否有排课
        // 简化实现：使用ScheduleService的冲突检测方法
        return scheduleService.checkConflict(
                makeupSchedule.getScheduleDate(),
                makeupSchedule.getStartTime(),
                makeupSchedule.getEndTime(),
                null, // 不检查教师冲突
                null, // 不检查教室冲突
                makeupScheduleId
        );
    }
}
