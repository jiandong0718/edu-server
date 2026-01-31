package com.edu.teaching.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.edu.teaching.domain.entity.Attendance;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 考勤 Mapper
 */
@DS("teaching")
public interface AttendanceMapper extends BaseMapper<Attendance> {

    /**
     * 分页查询考勤列表
     */
    IPage<Attendance> selectAttendancePage(IPage<Attendance> page, @Param("query") Attendance query);

    /**
     * 根据排课ID查询考勤列表
     */
    List<Attendance> selectByScheduleId(@Param("scheduleId") Long scheduleId);

    /**
     * 查询学员考勤记录
     */
    List<Attendance> selectByStudentId(@Param("studentId") Long studentId,
                                       @Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate);

    /**
     * 统计学员出勤情况
     */
    List<Attendance> selectAttendanceStats(@Param("studentId") Long studentId,
                                           @Param("classId") Long classId,
                                           @Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);
}
