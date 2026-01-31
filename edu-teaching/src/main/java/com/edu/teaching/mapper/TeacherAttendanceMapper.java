package com.edu.teaching.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.edu.teaching.domain.entity.TeacherAttendance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 教师考勤Mapper
 */
@Mapper
public interface TeacherAttendanceMapper extends BaseMapper<TeacherAttendance> {

    /**
     * 分页查询教师考勤列表（带关联信息）
     */
    IPage<TeacherAttendance> selectTeacherAttendancePage(IPage<TeacherAttendance> page, @Param("query") TeacherAttendance query);

    /**
     * 根据排课ID获取教师考勤
     */
    TeacherAttendance selectByScheduleId(@Param("scheduleId") Long scheduleId);

    /**
     * 根据教师ID查询考勤记录
     */
    List<TeacherAttendance> selectByTeacherId(@Param("teacherId") Long teacherId,
                                               @Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);

    /**
     * 查询教师考勤统计数据
     */
    List<TeacherAttendance> selectTeacherAttendanceStats(@Param("teacherId") Long teacherId,
                                                          @Param("classId") Long classId,
                                                          @Param("startDate") LocalDate startDate,
                                                          @Param("endDate") LocalDate endDate);

    /**
     * 根据排课ID和教师ID查询考勤记录
     */
    TeacherAttendance selectByScheduleAndTeacher(@Param("scheduleId") Long scheduleId,
                                                  @Param("teacherId") Long teacherId);

    /**
     * 统计教师考勤情况（按日统计）
     */
    List<Map<String, Object>> selectDailyStats(@Param("teacherId") Long teacherId,
                                                @Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate);

    /**
     * 统计教师考勤情况（按周统计）
     */
    List<Map<String, Object>> selectWeeklyStats(@Param("teacherId") Long teacherId,
                                                 @Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);

    /**
     * 统计教师考勤情况（按月统计）
     */
    List<Map<String, Object>> selectMonthlyStats(@Param("teacherId") Long teacherId,
                                                  @Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate);

    /**
     * 统计教师考勤汇总数据
     */
    Map<String, Object> selectAttendanceSummary(@Param("teacherId") Long teacherId,
                                                 @Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);
}
