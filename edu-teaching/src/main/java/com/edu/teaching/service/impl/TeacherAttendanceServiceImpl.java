package com.edu.teaching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.exception.BusinessException;
import com.edu.teaching.domain.dto.TeacherLeaveDTO;
import com.edu.teaching.domain.dto.TeacherSignInDTO;
import com.edu.teaching.domain.dto.TeacherSignOutDTO;
import com.edu.teaching.domain.entity.Schedule;
import com.edu.teaching.domain.entity.TeacherAttendance;
import com.edu.teaching.mapper.TeacherAttendanceMapper;
import com.edu.teaching.service.ScheduleService;
import com.edu.teaching.service.TeacherAttendanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 教师考勤服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TeacherAttendanceServiceImpl extends ServiceImpl<TeacherAttendanceMapper, TeacherAttendance>
        implements TeacherAttendanceService {

    private final ScheduleService scheduleService;

    /**
     * 迟到容忍时间（分钟）
     */
    private static final int LATE_TOLERANCE_MINUTES = 15;

    /**
     * 早退容忍时间（分钟）
     */
    private static final int EARLY_LEAVE_TOLERANCE_MINUTES = 15;

    @Override
    public IPage<TeacherAttendance> getTeacherAttendancePage(IPage<TeacherAttendance> page, TeacherAttendance query) {
        return baseMapper.selectTeacherAttendancePage(page, query);
    }

    @Override
    public TeacherAttendance getByScheduleId(Long scheduleId) {
        return baseMapper.selectByScheduleId(scheduleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean signIn(TeacherSignInDTO dto) {
        // 获取排课信息
        Schedule schedule = scheduleService.getById(dto.getScheduleId());
        if (schedule == null) {
            throw new BusinessException("排课记录不存在");
        }

        // 验证教师是否匹配
        if (!schedule.getTeacherId().equals(dto.getTeacherId())) {
            throw new BusinessException("教师与排课不匹配");
        }

        // 检查是否已有考勤记录
        TeacherAttendance attendance = getOne(new LambdaQueryWrapper<TeacherAttendance>()
                .eq(TeacherAttendance::getScheduleId, dto.getScheduleId())
                .eq(TeacherAttendance::getTeacherId, dto.getTeacherId()));

        LocalDateTime signInTime = LocalDateTime.now();
        boolean isNewRecord = false;

        if (attendance == null) {
            // 创建新的考勤记录
            attendance = new TeacherAttendance();
            attendance.setScheduleId(dto.getScheduleId());
            attendance.setTeacherId(dto.getTeacherId());
            attendance.setClassId(schedule.getClassId());
            isNewRecord = true;
        } else if (attendance.getSignInTime() != null) {
            throw new BusinessException("教师已签到，请勿重复签到");
        }

        // 判断是否迟到
        LocalDateTime classStartTime = LocalDateTime.of(schedule.getScheduleDate(), schedule.getStartTime());
        boolean isLate = signInTime.isAfter(classStartTime.plusMinutes(LATE_TOLERANCE_MINUTES));
        int lateMinutes = 0;

        if (signInTime.isAfter(classStartTime)) {
            lateMinutes = (int) Duration.between(classStartTime, signInTime).toMinutes();
        }

        attendance.setSignInTime(signInTime);
        attendance.setIsLate(isLate);
        attendance.setLateMinutes(lateMinutes);
        attendance.setStatus(isLate ? "late" : "present");
        attendance.setRemark(dto.getRemark());

        boolean result = saveOrUpdate(attendance);

        log.info("教师签到成功: scheduleId={}, teacherId={}, isLate={}, lateMinutes={}, isNew={}",
                dto.getScheduleId(), dto.getTeacherId(), isLate, lateMinutes, isNewRecord);

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean signOut(TeacherSignOutDTO dto) {
        // 获取排课信息
        Schedule schedule = scheduleService.getById(dto.getScheduleId());
        if (schedule == null) {
            throw new BusinessException("排课记录不存在");
        }

        // 验证教师是否匹配
        if (!schedule.getTeacherId().equals(dto.getTeacherId())) {
            throw new BusinessException("教师与排课不匹配");
        }

        // 获取考勤记录
        TeacherAttendance attendance = getOne(new LambdaQueryWrapper<TeacherAttendance>()
                .eq(TeacherAttendance::getScheduleId, dto.getScheduleId())
                .eq(TeacherAttendance::getTeacherId, dto.getTeacherId()));

        if (attendance == null) {
            throw new BusinessException("未找到签到记录，请先签到");
        }

        if (attendance.getSignInTime() == null) {
            throw new BusinessException("未签到，无法签退");
        }

        if (attendance.getSignOutTime() != null) {
            throw new BusinessException("已签退，请勿重复签退");
        }

        LocalDateTime signOutTime = LocalDateTime.now();

        // 判断是否早退
        LocalDateTime classEndTime = LocalDateTime.of(schedule.getScheduleDate(), schedule.getEndTime());
        boolean isEarlyLeave = signOutTime.isBefore(classEndTime.minusMinutes(EARLY_LEAVE_TOLERANCE_MINUTES));
        int earlyLeaveMinutes = 0;

        if (signOutTime.isBefore(classEndTime)) {
            earlyLeaveMinutes = (int) Duration.between(signOutTime, classEndTime).toMinutes();
        }

        attendance.setSignOutTime(signOutTime);
        attendance.setIsEarlyLeave(isEarlyLeave);
        attendance.setEarlyLeaveMinutes(earlyLeaveMinutes);

        // 更新状态：如果既迟到又早退，状态为early_leave；如果只迟到，状态为late；否则为present
        if (isEarlyLeave) {
            attendance.setStatus("early_leave");
        } else if (attendance.getIsLate()) {
            attendance.setStatus("late");
        } else {
            attendance.setStatus("present");
        }

        if (dto.getRemark() != null && !dto.getRemark().isEmpty()) {
            attendance.setRemark(dto.getRemark());
        }

        boolean result = updateById(attendance);

        log.info("教师签退成功: scheduleId={}, teacherId={}, isEarlyLeave={}, earlyLeaveMinutes={}",
                dto.getScheduleId(), dto.getTeacherId(), isEarlyLeave, earlyLeaveMinutes);

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean leave(TeacherLeaveDTO dto) {
        // 获取排课信息
        Schedule schedule = scheduleService.getById(dto.getScheduleId());
        if (schedule == null) {
            throw new BusinessException("排课记录不存在");
        }

        // 验证教师是否匹配
        if (!schedule.getTeacherId().equals(dto.getTeacherId())) {
            throw new BusinessException("教师与排课不匹配");
        }

        // 检查是否已有考勤记录
        TeacherAttendance attendance = getOne(new LambdaQueryWrapper<TeacherAttendance>()
                .eq(TeacherAttendance::getScheduleId, dto.getScheduleId())
                .eq(TeacherAttendance::getTeacherId, dto.getTeacherId()));

        if (attendance == null) {
            // 创建新的考勤记录
            attendance = new TeacherAttendance();
            attendance.setScheduleId(dto.getScheduleId());
            attendance.setTeacherId(dto.getTeacherId());
            attendance.setClassId(schedule.getClassId());
        } else if (attendance.getSignInTime() != null) {
            throw new BusinessException("教师已签到，无法标记为请假");
        }

        attendance.setStatus("leave");
        attendance.setRemark(dto.getRemark());
        attendance.setIsLate(false);
        attendance.setIsEarlyLeave(false);
        attendance.setLateMinutes(0);
        attendance.setEarlyLeaveMinutes(0);

        boolean result = saveOrUpdate(attendance);

        log.info("教师请假记录成功: scheduleId={}, teacherId={}, remark={}",
                dto.getScheduleId(), dto.getTeacherId(), dto.getRemark());

        return result;
    }

    @Override
    public boolean updateStatus(Long id, String status, String remark) {
        // 验证考勤状态
        validateAttendanceStatus(status);

        TeacherAttendance attendance = getById(id);
        if (attendance == null) {
            throw new BusinessException("考勤记录不存在");
        }

        attendance.setStatus(status);
        attendance.setRemark(remark);

        return updateById(attendance);
    }

    @Override
    public List<TeacherAttendance> getTeacherAttendance(Long teacherId, LocalDate startDate, LocalDate endDate) {
        return baseMapper.selectByTeacherId(teacherId, startDate, endDate);
    }

    @Override
    public Map<String, Object> getTeacherAttendanceStats(Long teacherId, Long classId, LocalDate startDate, LocalDate endDate) {
        List<TeacherAttendance> attendances = baseMapper.selectTeacherAttendanceStats(teacherId, classId, startDate, endDate);

        return calculateStats(attendances);
    }

    @Override
    public Map<String, Object> getClassTeacherAttendanceStats(Long classId, LocalDate startDate, LocalDate endDate) {
        List<TeacherAttendance> attendances = baseMapper.selectTeacherAttendanceStats(null, classId, startDate, endDate);

        return calculateStats(attendances);
    }

    @Override
    public Double calculateAttendanceRate(Long teacherId, LocalDate startDate, LocalDate endDate) {
        List<TeacherAttendance> attendances = baseMapper.selectByTeacherId(teacherId, startDate, endDate);

        int total = attendances.size();
        if (total == 0) {
            return 0.0;
        }

        long presentCount = attendances.stream()
                .filter(a -> "present".equals(a.getStatus()) || "late".equals(a.getStatus()))
                .count();

        return (presentCount * 100.0) / total;
    }

    @Override
    public List<Map<String, Object>> getDailyStats(Long teacherId, LocalDate startDate, LocalDate endDate) {
        return baseMapper.selectDailyStats(teacherId, startDate, endDate);
    }

    @Override
    public List<Map<String, Object>> getWeeklyStats(Long teacherId, LocalDate startDate, LocalDate endDate) {
        return baseMapper.selectWeeklyStats(teacherId, startDate, endDate);
    }

    @Override
    public List<Map<String, Object>> getMonthlyStats(Long teacherId, LocalDate startDate, LocalDate endDate) {
        return baseMapper.selectMonthlyStats(teacherId, startDate, endDate);
    }

    @Override
    public Map<String, Object> getAttendanceSummary(Long teacherId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> summary = baseMapper.selectAttendanceSummary(teacherId, startDate, endDate);
        if (summary == null) {
            summary = new HashMap<>();
            summary.put("totalCount", 0);
            summary.put("presentCount", 0);
            summary.put("absentCount", 0);
            summary.put("lateCount", 0);
            summary.put("earlyLeaveCount", 0);
            summary.put("leaveCount", 0);
            summary.put("totalLateMinutes", 0);
            summary.put("totalEarlyLeaveMinutes", 0);
            summary.put("attendanceRate", 0.0);
            summary.put("lateRate", 0.0);
            summary.put("earlyLeaveRate", 0.0);
        } else {
            // 计算百分比
            int total = ((Number) summary.getOrDefault("totalCount", 0)).intValue();
            if (total > 0) {
                int present = ((Number) summary.getOrDefault("presentCount", 0)).intValue();
                int late = ((Number) summary.getOrDefault("lateCount", 0)).intValue();
                int earlyLeave = ((Number) summary.getOrDefault("earlyLeaveCount", 0)).intValue();

                summary.put("attendanceRate", (present + late) * 100.0 / total);
                summary.put("lateRate", late * 100.0 / total);
                summary.put("earlyLeaveRate", earlyLeave * 100.0 / total);
            } else {
                summary.put("attendanceRate", 0.0);
                summary.put("lateRate", 0.0);
                summary.put("earlyLeaveRate", 0.0);
            }
        }
        return summary;
    }

    /**
     * 计算考勤统计数据
     */
    private Map<String, Object> calculateStats(List<TeacherAttendance> attendances) {
        int total = attendances.size();
        int present = 0;
        int absent = 0;
        int late = 0;
        int earlyLeave = 0;
        int leave = 0;
        int totalLateMinutes = 0;
        int totalEarlyLeaveMinutes = 0;

        for (TeacherAttendance attendance : attendances) {
            switch (attendance.getStatus()) {
                case "present":
                    present++;
                    break;
                case "absent":
                    absent++;
                    break;
                case "late":
                    late++;
                    if (attendance.getLateMinutes() != null) {
                        totalLateMinutes += attendance.getLateMinutes();
                    }
                    break;
                case "early_leave":
                    earlyLeave++;
                    if (attendance.getEarlyLeaveMinutes() != null) {
                        totalEarlyLeaveMinutes += attendance.getEarlyLeaveMinutes();
                    }
                    break;
                case "leave":
                    leave++;
                    break;
            }
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("present", present);
        stats.put("absent", absent);
        stats.put("late", late);
        stats.put("earlyLeave", earlyLeave);
        stats.put("leave", leave);
        stats.put("attendanceRate", total > 0 ? (present + late) * 100.0 / total : 0);
        stats.put("totalLateMinutes", totalLateMinutes);
        stats.put("totalEarlyLeaveMinutes", totalEarlyLeaveMinutes);
        stats.put("avgLateMinutes", late > 0 ? totalLateMinutes * 1.0 / late : 0);
        stats.put("avgEarlyLeaveMinutes", earlyLeave > 0 ? totalEarlyLeaveMinutes * 1.0 / earlyLeave : 0);

        return stats;
    }

    /**
     * 验证考勤状态
     */
    private void validateAttendanceStatus(String status) {
        if (!"present".equals(status) && !"absent".equals(status)
                && !"late".equals(status) && !"early_leave".equals(status) && !"leave".equals(status)) {
            throw new BusinessException("无效的考勤状态: " + status);
        }
    }
}
