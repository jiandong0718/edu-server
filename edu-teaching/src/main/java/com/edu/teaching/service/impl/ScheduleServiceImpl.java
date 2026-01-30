package com.edu.teaching.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.exception.BusinessException;
import com.edu.teaching.domain.entity.Schedule;
import com.edu.teaching.mapper.ScheduleMapper;
import com.edu.teaching.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 排课服务实现
 */
@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl extends ServiceImpl<ScheduleMapper, Schedule> implements ScheduleService {

    @Override
    public List<Schedule> getScheduleList(Long campusId, LocalDate startDate, LocalDate endDate,
                                          Long teacherId, Long classId, Long classroomId) {
        return baseMapper.selectScheduleList(campusId, startDate, endDate, teacherId, classId, classroomId);
    }

    @Override
    public boolean createSchedule(Schedule schedule) {
        // 检查冲突
        if (checkConflict(schedule.getScheduleDate(), schedule.getStartTime(), schedule.getEndTime(),
                schedule.getTeacherId(), schedule.getClassroomId(), null)) {
            throw new BusinessException("排课时间冲突，请检查教师或教室是否已被占用");
        }
        schedule.setStatus("scheduled");
        return save(schedule);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchCreateSchedule(Long classId, LocalDate startDate, LocalDate endDate,
                                       List<Integer> weekdays, LocalTime startTime, LocalTime endTime) {
        // 遍历日期范围，按星期几生成排课
        LocalDate current = startDate;
        int lessonNo = 1;

        while (!current.isAfter(endDate)) {
            DayOfWeek dayOfWeek = current.getDayOfWeek();
            if (weekdays.contains(dayOfWeek.getValue())) {
                Schedule schedule = new Schedule();
                schedule.setClassId(classId);
                schedule.setScheduleDate(current);
                schedule.setStartTime(startTime);
                schedule.setEndTime(endTime);
                schedule.setLessonNo(lessonNo++);
                schedule.setStatus("scheduled");

                // TODO: 从班级获取课程、教师、教室等信息
                save(schedule);
            }
            current = current.plusDays(1);
        }
        return true;
    }

    @Override
    public boolean reschedule(Long id, LocalDate newDate, LocalTime newStartTime,
                              LocalTime newEndTime, Long newClassroomId) {
        Schedule schedule = getById(id);
        if (schedule == null) {
            throw new BusinessException("排课记录不存在");
        }

        // 检查冲突
        Long classroomId = newClassroomId != null ? newClassroomId : schedule.getClassroomId();
        if (checkConflict(newDate, newStartTime, newEndTime, schedule.getTeacherId(), classroomId, id)) {
            throw new BusinessException("调课时间冲突");
        }

        schedule.setScheduleDate(newDate);
        schedule.setStartTime(newStartTime);
        schedule.setEndTime(newEndTime);
        if (newClassroomId != null) {
            schedule.setClassroomId(newClassroomId);
        }
        return updateById(schedule);
    }

    @Override
    public boolean substitute(Long id, Long newTeacherId) {
        Schedule schedule = getById(id);
        if (schedule == null) {
            throw new BusinessException("排课记录不存在");
        }

        // 检查新教师是否有冲突
        if (checkConflict(schedule.getScheduleDate(), schedule.getStartTime(), schedule.getEndTime(),
                newTeacherId, schedule.getClassroomId(), id)) {
            throw new BusinessException("代课教师在该时间段已有其他课程");
        }

        schedule.setTeacherId(newTeacherId);
        return updateById(schedule);
    }

    @Override
    public boolean cancelSchedule(Long id) {
        Schedule schedule = getById(id);
        if (schedule == null) {
            throw new BusinessException("排课记录不存在");
        }
        schedule.setStatus("cancelled");
        return updateById(schedule);
    }

    @Override
    public boolean checkConflict(LocalDate scheduleDate, LocalTime startTime, LocalTime endTime,
                                 Long teacherId, Long classroomId, Long excludeId) {
        return baseMapper.checkConflict(scheduleDate, startTime, endTime, teacherId, classroomId, excludeId) > 0;
    }
}
