package com.edu.teaching.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.teaching.domain.dto.BatchRescheduleDTO;
import com.edu.teaching.domain.dto.BatchScheduleDTO;
import com.edu.teaching.domain.dto.CancelScheduleDTO;
import com.edu.teaching.domain.dto.RescheduleDTO;
import com.edu.teaching.domain.dto.ScheduleConflictCheckDTO;
import com.edu.teaching.domain.dto.SubstituteTeacherDTO;
import com.edu.teaching.domain.entity.Schedule;
import com.edu.teaching.domain.entity.ScheduleHistory;
import com.edu.teaching.domain.vo.BatchScheduleResultVO;
import com.edu.teaching.domain.vo.ScheduleConflictVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 排课服务接口
 */
public interface ScheduleService extends IService<Schedule> {

    /**
     * 查询课表
     */
    List<Schedule> getScheduleList(Long campusId, LocalDate startDate, LocalDate endDate,
                                   Long teacherId, Long classId, Long classroomId);

    /**
     * 创建排课
     */
    boolean createSchedule(Schedule schedule);

    /**
     * 批量排课（简单版本）
     */
    boolean batchCreateSchedule(Long classId, LocalDate startDate, LocalDate endDate,
                                List<Integer> weekdays, java.time.LocalTime startTime,
                                java.time.LocalTime endTime);

    /**
     * 批量排课（增强版本）
     * 支持按规则自动生成排课计划，支持跳过节假日
     */
    BatchScheduleResultVO batchCreateScheduleEnhanced(BatchScheduleDTO batchScheduleDTO);

    /**
     * 调课（简单版本，保留向后兼容）
     * @deprecated 使用 {@link #rescheduleEnhanced(RescheduleDTO)} 替代
     */
    @Deprecated
    boolean reschedule(Long id, LocalDate newDate, java.time.LocalTime newStartTime,
                       java.time.LocalTime newEndTime, Long newClassroomId);

    /**
     * 调课（增强版本，支持更多参数和历史记录）
     */
    boolean rescheduleEnhanced(RescheduleDTO dto);

    /**
     * 批量调课
     */
    boolean batchReschedule(BatchRescheduleDTO dto);

    /**
     * 代课（简单版本，保留向后兼容）
     */
    boolean substitute(Long id, Long newTeacherId);

    /**
     * 代课（增强版本，支持原因和通知）
     */
    boolean substituteTeacher(SubstituteTeacherDTO dto);

    /**
     * 取消课程（简单版本，保留向后兼容）
     */
    boolean cancelSchedule(Long id);

    /**
     * 停课（增强版本，支持原因、补课安排和通知）
     */
    boolean cancelScheduleWithDetails(CancelScheduleDTO dto);

    /**
     * 检查排课冲突（简单版本）
     */
    boolean checkConflict(LocalDate scheduleDate, java.time.LocalTime startTime,
                          java.time.LocalTime endTime, Long teacherId,
                          Long classroomId, Long excludeId);

    /**
     * 检查教师是否可用（检查状态和时间冲突）
     */
    boolean isTeacherAvailable(Long teacherId, LocalDate scheduleDate,
                               java.time.LocalTime startTime, java.time.LocalTime endTime,
                               Long excludeScheduleId);

    /**
     * 检查排课冲突（详细版本）
     * 返回详细的冲突信息，包括教师冲突、教室冲突、学员冲突、教师可用时间冲突、教室状态冲突
     */
    ScheduleConflictVO checkConflictDetail(ScheduleConflictCheckDTO checkDTO);

    /**
     * 查询调课历史
     */
    List<ScheduleHistory> getScheduleHistory(Long scheduleId);
}
