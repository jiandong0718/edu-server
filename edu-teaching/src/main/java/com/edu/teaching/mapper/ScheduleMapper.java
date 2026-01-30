package com.edu.teaching.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.teaching.domain.entity.Schedule;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 排课 Mapper
 */
public interface ScheduleMapper extends BaseMapper<Schedule> {

    /**
     * 查询日期范围内的课表
     */
    List<Schedule> selectScheduleList(
            @Param("campusId") Long campusId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("teacherId") Long teacherId,
            @Param("classId") Long classId,
            @Param("classroomId") Long classroomId
    );

    /**
     * 检查排课冲突
     */
    int checkConflict(
            @Param("scheduleDate") LocalDate scheduleDate,
            @Param("startTime") java.time.LocalTime startTime,
            @Param("endTime") java.time.LocalTime endTime,
            @Param("teacherId") Long teacherId,
            @Param("classroomId") Long classroomId,
            @Param("excludeId") Long excludeId
    );
}
