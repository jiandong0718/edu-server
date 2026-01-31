package com.edu.teaching.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.teaching.domain.entity.Schedule;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 排课 Mapper
 */
@DS("teaching")
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

    /**
     * 查询教师时间冲突的排课
     */
    List<Schedule> selectTeacherConflicts(
            @Param("scheduleDate") LocalDate scheduleDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("teacherId") Long teacherId,
            @Param("excludeId") Long excludeId
    );

    /**
     * 查询教室时间冲突的排课
     */
    List<Schedule> selectClassroomConflicts(
            @Param("scheduleDate") LocalDate scheduleDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("classroomId") Long classroomId,
            @Param("excludeId") Long excludeId
    );

    /**
     * 查询班级学员在指定时间的排课（用于检测学员冲突）
     */
    List<Schedule> selectStudentConflictSchedules(
            @Param("scheduleDate") LocalDate scheduleDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("classId") Long classId,
            @Param("excludeId") Long excludeId
    );
}
