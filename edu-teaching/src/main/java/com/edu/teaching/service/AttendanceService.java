package com.edu.teaching.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.teaching.domain.dto.AttendanceSignInDTO;
import com.edu.teaching.domain.dto.BatchAttendanceSignInDTO;
import com.edu.teaching.domain.dto.BatchAttendanceWithStatusDTO;
import com.edu.teaching.domain.entity.Attendance;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 考勤服务接口
 */
public interface AttendanceService extends IService<Attendance> {

    /**
     * 分页查询考勤列表
     */
    IPage<Attendance> getAttendancePage(IPage<Attendance> page, Attendance query);

    /**
     * 根据排课ID获取考勤列表
     */
    List<Attendance> getByScheduleId(Long scheduleId);

    /**
     * 初始化排课考勤记录（为班级所有学员创建考勤记录）
     */
    boolean initAttendance(Long scheduleId);

    /**
     * 单个签到
     */
    boolean signIn(Long scheduleId, Long studentId, String status, String remark);

    /**
     * 单个签到（使用DTO）
     */
    boolean signIn(AttendanceSignInDTO dto);

    /**
     * 批量签到（统一状态）
     */
    boolean batchSignIn(Long scheduleId, List<Long> studentIds, String status);

    /**
     * 批量签到（使用DTO）
     */
    boolean batchSignIn(BatchAttendanceSignInDTO dto);

    /**
     * 批量签到（不同状态）
     */
    boolean batchSignInWithStatus(BatchAttendanceWithStatusDTO dto);

    /**
     * 更新考勤状态
     */
    boolean updateStatus(Long id, String status, String remark);

    /**
     * 查询学员考勤记录
     */
    List<Attendance> getStudentAttendance(Long studentId, LocalDate startDate, LocalDate endDate);

    /**
     * 统计学员出勤情况
     */
    Map<String, Object> getAttendanceStats(Long studentId, Long classId, LocalDate startDate, LocalDate endDate);

    /**
     * 统计班级出勤情况
     */
    Map<String, Object> getClassAttendanceStats(Long classId, LocalDate startDate, LocalDate endDate);

    /**
     * 自动判断迟到状态
     * 根据签到时间和课程开始时间自动判断是否迟到
     */
    String determineAttendanceStatus(Long scheduleId);

    /**
     * 综合考勤统计
     * 返回按班级和按学员的统计数据，以及总体汇总
     */
    Map<String, Object> getComprehensiveStats(LocalDate startDate, LocalDate endDate, Long classId, Long studentId);
}
