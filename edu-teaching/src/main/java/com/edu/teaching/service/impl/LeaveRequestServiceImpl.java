package com.edu.teaching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.exception.BusinessException;
import com.edu.teaching.domain.entity.Attendance;
import com.edu.teaching.domain.entity.LeaveRequest;
import com.edu.teaching.domain.entity.MakeupLesson;
import com.edu.teaching.domain.entity.Schedule;
import com.edu.teaching.event.LeaveApprovedEvent;
import com.edu.teaching.mapper.LeaveRequestMapper;
import com.edu.teaching.service.AttendanceService;
import com.edu.teaching.service.LeaveRequestService;
import com.edu.teaching.service.MakeupLessonService;
import com.edu.teaching.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 请假申请服务实现
 */
@Service
@RequiredArgsConstructor
public class LeaveRequestServiceImpl extends ServiceImpl<LeaveRequestMapper, LeaveRequest> implements LeaveRequestService {

    private final AttendanceService attendanceService;
    private final ScheduleService scheduleService;
    private final MakeupLessonService makeupLessonService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public IPage<LeaveRequest> getLeaveRequestPage(IPage<LeaveRequest> page, LeaveRequest query) {
        return baseMapper.selectLeaveRequestPage(page, query);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitLeaveRequest(LeaveRequest leaveRequest) {
        // 生成请假单号
        leaveRequest.setLeaveNo(generateLeaveNo());
        leaveRequest.setStatus("pending");

        // 如果是单次请假且指定了排课ID，设置日期
        if ("single".equals(leaveRequest.getType()) && leaveRequest.getScheduleId() != null) {
            Schedule schedule = scheduleService.getById(leaveRequest.getScheduleId());
            if (schedule == null) {
                throw new BusinessException("排课不存在");
            }
            leaveRequest.setStartDate(schedule.getScheduleDate());
            leaveRequest.setEndDate(schedule.getScheduleDate());
            leaveRequest.setClassId(schedule.getClassId());
            leaveRequest.setCampusId(schedule.getCampusId());
        }

        // 验证日期范围
        if ("period".equals(leaveRequest.getType())) {
            if (leaveRequest.getStartDate() == null || leaveRequest.getEndDate() == null) {
                throw new BusinessException("时段请假必须指定开始和结束日期");
            }
            if (leaveRequest.getStartDate().isAfter(leaveRequest.getEndDate())) {
                throw new BusinessException("开始日期不能晚于结束日期");
            }
        }

        return save(leaveRequest);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean approve(Long id, boolean approved, String remark) {
        LeaveRequest leaveRequest = getById(id);
        if (leaveRequest == null) {
            throw new BusinessException("请假申请不存在");
        }

        if (!"pending".equals(leaveRequest.getStatus())) {
            throw new BusinessException("该请假申请已处理");
        }

        leaveRequest.setStatus(approved ? "approved" : "rejected");
        leaveRequest.setApproveTime(LocalDateTime.now());
        leaveRequest.setApproveRemark(remark);
        // TODO: 设置审批人ID
        // leaveRequest.setApproverId(SecurityUtils.getUserId());

        boolean result = updateById(leaveRequest);

        // 如果批准，更新对应的考勤记录为请假状态
        if (result && approved) {
            if (leaveRequest.getScheduleId() != null) {
                // 单次请假，更新指定排课的考勤
                attendanceService.signIn(leaveRequest.getScheduleId(), leaveRequest.getStudentId(), "leave", "请假");
            } else {
                // 时段请假，需要更新该时段内所有排课的考勤
                // TODO: 查询该时段内该学员的所有排课，更新考勤状态
            }

            // 发布请假审批事件
            eventPublisher.publishEvent(new LeaveApprovedEvent(
                    this,
                    leaveRequest.getId(),
                    leaveRequest.getStudentId(),
                    approved,
                    remark
            ));
        }

        return result;
    }

    @Override
    public boolean cancel(Long id) {
        LeaveRequest leaveRequest = getById(id);
        if (leaveRequest == null) {
            throw new BusinessException("请假申请不存在");
        }

        if (!"pending".equals(leaveRequest.getStatus())) {
            throw new BusinessException("只能取消待审批的请假申请");
        }

        leaveRequest.setStatus("cancelled");
        return updateById(leaveRequest);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean arrangeMakeup(Long id, Long makeupScheduleId) {
        LeaveRequest leaveRequest = getById(id);
        if (leaveRequest == null) {
            throw new BusinessException("请假申请不存在");
        }

        if (!"approved".equals(leaveRequest.getStatus())) {
            throw new BusinessException("只能为已批准的请假安排补课");
        }

        if (leaveRequest.getScheduleId() == null) {
            throw new BusinessException("只能为单次请假安排补课");
        }

        // 创建补课记录
        MakeupLesson makeupLesson = new MakeupLesson();
        makeupLesson.setLeaveRequestId(id);
        makeupLesson.setOriginalScheduleId(leaveRequest.getScheduleId());
        makeupLesson.setMakeupScheduleId(makeupScheduleId);
        makeupLesson.setStudentId(leaveRequest.getStudentId());
        makeupLesson.setCampusId(leaveRequest.getCampusId());
        makeupLesson.setStatus("pending");

        boolean result = makeupLessonService.arrangeMakeup(makeupLesson);

        // 更新请假申请的补课排课ID
        if (result) {
            leaveRequest.setMakeupScheduleId(makeupScheduleId);
            updateById(leaveRequest);
        }

        return result;
    }

    @Override
    public String generateLeaveNo() {
        String prefix = "LV";
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 查询当天最大编号
        Long count = count(new LambdaQueryWrapper<LeaveRequest>()
                .likeRight(LeaveRequest::getLeaveNo, prefix + dateStr));

        return prefix + dateStr + String.format("%04d", count + 1);
    }
}
