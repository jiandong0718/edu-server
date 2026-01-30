package com.edu.teaching.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.teaching.domain.entity.Schedule;

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
     * 批量排课
     */
    boolean batchCreateSchedule(Long classId, LocalDate startDate, LocalDate endDate,
                                List<Integer> weekdays, java.time.LocalTime startTime,
                                java.time.LocalTime endTime);

    /**
     * 调课
     */
    boolean reschedule(Long id, LocalDate newDate, java.time.LocalTime newStartTime,
                       java.time.LocalTime newEndTime, Long newClassroomId);

    /**
     * 代课
     */
    boolean substitute(Long id, Long newTeacherId);

    /**
     * 取消课程
     */
    boolean cancelSchedule(Long id);

    /**
     * 检查排课冲突
     */
    boolean checkConflict(LocalDate scheduleDate, java.time.LocalTime startTime,
                          java.time.LocalTime endTime, Long teacherId,
                          Long classroomId, Long excludeId);
}
