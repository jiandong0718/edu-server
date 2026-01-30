package com.edu.teaching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.exception.BusinessException;
import com.edu.teaching.domain.entity.Attendance;
import com.edu.teaching.domain.entity.Schedule;
import com.edu.teaching.mapper.AttendanceMapper;
import com.edu.teaching.service.AttendanceService;
import com.edu.teaching.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 考勤服务实现
 */
@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl extends ServiceImpl<AttendanceMapper, Attendance> implements AttendanceService {

    private final ScheduleService scheduleService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public IPage<Attendance> getAttendancePage(IPage<Attendance> page, Attendance query) {
        return baseMapper.selectAttendancePage(page, query);
    }

    @Override
    public List<Attendance> getByScheduleId(Long scheduleId) {
        return baseMapper.selectByScheduleId(scheduleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean initAttendance(Long scheduleId) {
        Schedule schedule = scheduleService.getById(scheduleId);
        if (schedule == null) {
            throw new BusinessException("排课记录不存在");
        }

        // TODO: 获取班级学员列表，为每个学员创建考勤记录
        // 这里需要调用班级学员关联服务获取学员列表
        // List<Long> studentIds = classStudentService.getStudentIdsByClassId(schedule.getClassId());
        // for (Long studentId : studentIds) {
        //     Attendance attendance = new Attendance();
        //     attendance.setScheduleId(scheduleId);
        //     attendance.setStudentId(studentId);
        //     attendance.setStatus("absent");
        //     attendance.setClassHours(schedule.getClassHours());
        //     save(attendance);
        // }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean signIn(Long scheduleId, Long studentId, String status, String remark) {
        // 检查是否已有考勤记录
        Attendance attendance = getOne(new LambdaQueryWrapper<Attendance>()
                .eq(Attendance::getScheduleId, scheduleId)
                .eq(Attendance::getStudentId, studentId));

        if (attendance == null) {
            // 创建新的考勤记录
            Schedule schedule = scheduleService.getById(scheduleId);
            if (schedule == null) {
                throw new BusinessException("排课记录不存在");
            }

            attendance = new Attendance();
            attendance.setScheduleId(scheduleId);
            attendance.setStudentId(studentId);
            attendance.setClassHours(schedule.getClassHours());
        }

        attendance.setStatus(status);
        attendance.setRemark(remark);

        // 如果是出勤或迟到，记录签到时间
        if ("present".equals(status) || "late".equals(status)) {
            attendance.setSignTime(LocalDateTime.now());
        }

        boolean result = saveOrUpdate(attendance);

        // 发布考勤事件（用于课时扣减和通知）
        if (result && ("present".equals(status) || "late".equals(status))) {
            // eventPublisher.publishEvent(new AttendanceSignedEvent(attendance));
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchSignIn(Long scheduleId, List<Long> studentIds, String status) {
        Schedule schedule = scheduleService.getById(scheduleId);
        if (schedule == null) {
            throw new BusinessException("排课记录不存在");
        }

        LocalDateTime signTime = LocalDateTime.now();

        for (Long studentId : studentIds) {
            Attendance attendance = getOne(new LambdaQueryWrapper<Attendance>()
                    .eq(Attendance::getScheduleId, scheduleId)
                    .eq(Attendance::getStudentId, studentId));

            if (attendance == null) {
                attendance = new Attendance();
                attendance.setScheduleId(scheduleId);
                attendance.setStudentId(studentId);
                attendance.setClassHours(schedule.getClassHours());
            }

            attendance.setStatus(status);
            if ("present".equals(status) || "late".equals(status)) {
                attendance.setSignTime(signTime);
            }

            saveOrUpdate(attendance);
        }

        return true;
    }

    @Override
    public boolean updateStatus(Long id, String status, String remark) {
        Attendance attendance = getById(id);
        if (attendance == null) {
            throw new BusinessException("考勤记录不存在");
        }

        attendance.setStatus(status);
        attendance.setRemark(remark);

        if ("present".equals(status) || "late".equals(status)) {
            if (attendance.getSignTime() == null) {
                attendance.setSignTime(LocalDateTime.now());
            }
        }

        return updateById(attendance);
    }

    @Override
    public List<Attendance> getStudentAttendance(Long studentId, LocalDate startDate, LocalDate endDate) {
        return baseMapper.selectByStudentId(studentId, startDate, endDate);
    }

    @Override
    public Map<String, Object> getAttendanceStats(Long studentId, Long classId, LocalDate startDate, LocalDate endDate) {
        List<Attendance> attendances = baseMapper.selectAttendanceStats(studentId, classId, startDate, endDate);

        int total = attendances.size();
        int present = 0;
        int absent = 0;
        int late = 0;
        int leave = 0;

        for (Attendance attendance : attendances) {
            switch (attendance.getStatus()) {
                case "present":
                    present++;
                    break;
                case "absent":
                    absent++;
                    break;
                case "late":
                    late++;
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
        stats.put("leave", leave);
        stats.put("attendanceRate", total > 0 ? (present + late) * 100.0 / total : 0);

        return stats;
    }

    @Override
    public Map<String, Object> getClassAttendanceStats(Long classId, LocalDate startDate, LocalDate endDate) {
        List<Attendance> attendances = baseMapper.selectAttendanceStats(null, classId, startDate, endDate);

        int total = attendances.size();
        int present = 0;
        int absent = 0;
        int late = 0;
        int leave = 0;

        for (Attendance attendance : attendances) {
            switch (attendance.getStatus()) {
                case "present":
                    present++;
                    break;
                case "absent":
                    absent++;
                    break;
                case "late":
                    late++;
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
        stats.put("leave", leave);
        stats.put("attendanceRate", total > 0 ? (present + late) * 100.0 / total : 0);

        return stats;
    }
}
