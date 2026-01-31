package com.edu.teaching.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.teaching.domain.dto.TeacherLeaveDTO;
import com.edu.teaching.domain.dto.TeacherSignInDTO;
import com.edu.teaching.domain.dto.TeacherSignOutDTO;
import com.edu.teaching.domain.entity.TeacherAttendance;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 教师考勤服务接口
 */
public interface TeacherAttendanceService extends IService<TeacherAttendance> {

    /**
     * 分页查询教师考勤列表
     */
    IPage<TeacherAttendance> getTeacherAttendancePage(IPage<TeacherAttendance> page, TeacherAttendance query);

    /**
     * 根据排课ID获取教师考勤
     */
    TeacherAttendance getByScheduleId(Long scheduleId);

    /**
     * 教师签到
     */
    boolean signIn(TeacherSignInDTO dto);

    /**
     * 教师签退
     */
    boolean signOut(TeacherSignOutDTO dto);

    /**
     * 教师请假
     */
    boolean leave(TeacherLeaveDTO dto);

    /**
     * 更新考勤状态
     */
    boolean updateStatus(Long id, String status, String remark);

    /**
     * 查询教师考勤记录
     */
    List<TeacherAttendance> getTeacherAttendance(Long teacherId, LocalDate startDate, LocalDate endDate);

    /**
     * 统计教师出勤情况
     */
    Map<String, Object> getTeacherAttendanceStats(Long teacherId, Long classId, LocalDate startDate, LocalDate endDate);

    /**
     * 统计班级教师出勤情况
     */
    Map<String, Object> getClassTeacherAttendanceStats(Long classId, LocalDate startDate, LocalDate endDate);

    /**
     * 计算出勤率
     */
    Double calculateAttendanceRate(Long teacherId, LocalDate startDate, LocalDate endDate);

    /**
     * 统计教师考勤情况（按日统计）
     */
    List<Map<String, Object>> getDailyStats(Long teacherId, LocalDate startDate, LocalDate endDate);

    /**
     * 统计教师考勤情况（按周统计）
     */
    List<Map<String, Object>> getWeeklyStats(Long teacherId, LocalDate startDate, LocalDate endDate);

    /**
     * 统计教师考勤情况（按月统计）
     */
    List<Map<String, Object>> getMonthlyStats(Long teacherId, LocalDate startDate, LocalDate endDate);

    /**
     * 获取教师考勤统计汇总（包含迟到、早退、缺勤统计）
     */
    Map<String, Object> getAttendanceSummary(Long teacherId, LocalDate startDate, LocalDate endDate);
}
