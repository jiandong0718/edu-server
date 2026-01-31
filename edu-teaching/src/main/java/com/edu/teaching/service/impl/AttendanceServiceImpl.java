package com.edu.teaching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.exception.BusinessException;
import com.edu.teaching.domain.dto.AttendanceSignInDTO;
import com.edu.teaching.domain.dto.BatchAttendanceSignInDTO;
import com.edu.teaching.domain.dto.BatchAttendanceWithStatusDTO;
import com.edu.teaching.domain.entity.Attendance;
import com.edu.teaching.domain.entity.Schedule;
import com.edu.teaching.event.AttendanceSignedEvent;
import com.edu.teaching.mapper.AttendanceMapper;
import com.edu.teaching.service.AttendanceService;
import com.edu.teaching.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 考勤服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl extends ServiceImpl<AttendanceMapper, Attendance> implements AttendanceService {

    private final ScheduleService scheduleService;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 迟到容忍时间（分钟）
     */
    private static final int LATE_TOLERANCE_MINUTES = 15;

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
        //     attendance.setClassId(schedule.getClassId());
        //     attendance.setStatus("absent");
        //     attendance.setClassHours(schedule.getClassHours());
        //     save(attendance);
        // }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean signIn(Long scheduleId, Long studentId, String status, String remark) {
        AttendanceSignInDTO dto = new AttendanceSignInDTO();
        dto.setScheduleId(scheduleId);
        dto.setStudentId(studentId);
        dto.setStatus(status);
        dto.setRemark(remark);
        return signIn(dto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean signIn(AttendanceSignInDTO dto) {
        // 验证考勤状态
        validateAttendanceStatus(dto.getStatus());

        // 获取排课信息
        Schedule schedule = scheduleService.getById(dto.getScheduleId());
        if (schedule == null) {
            throw new BusinessException("排课记录不存在");
        }

        // 检查是否已有考勤记录
        Attendance attendance = getOne(new LambdaQueryWrapper<Attendance>()
                .eq(Attendance::getScheduleId, dto.getScheduleId())
                .eq(Attendance::getStudentId, dto.getStudentId()));

        LocalDateTime signTime = LocalDateTime.now();
        boolean isNewRecord = false;

        if (attendance == null) {
            // 创建新的考勤记录
            attendance = new Attendance();
            attendance.setScheduleId(dto.getScheduleId());
            attendance.setStudentId(dto.getStudentId());
            attendance.setClassId(schedule.getClassId());
            attendance.setClassHours(schedule.getClassHours());
            isNewRecord = true;
        }

        // 如果状态是出勤，自动判断是否迟到
        String finalStatus = dto.getStatus();
        if ("present".equals(dto.getStatus())) {
            finalStatus = determineAttendanceStatusByTime(schedule, signTime);
        }

        attendance.setStatus(finalStatus);
        attendance.setRemark(dto.getRemark());

        // 如果是出勤或迟到，记录签到时间
        if ("present".equals(finalStatus) || "late".equals(finalStatus)) {
            attendance.setSignTime(signTime);
        }

        boolean result = saveOrUpdate(attendance);

        // 发布考勤事件（用于课时扣减和通知）
        if (result && ("present".equals(finalStatus) || "late".equals(finalStatus))) {
            publishAttendanceEvent(attendance, schedule);
        }

        log.info("学员签到成功: scheduleId={}, studentId={}, status={}, isNew={}",
                dto.getScheduleId(), dto.getStudentId(), finalStatus, isNewRecord);

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchSignIn(Long scheduleId, List<Long> studentIds, String status) {
        BatchAttendanceSignInDTO dto = new BatchAttendanceSignInDTO();
        dto.setScheduleId(scheduleId);
        dto.setStudentIds(studentIds);
        dto.setStatus(status);
        return batchSignIn(dto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchSignIn(BatchAttendanceSignInDTO dto) {
        // 验证考勤状态
        validateAttendanceStatus(dto.getStatus());

        Schedule schedule = scheduleService.getById(dto.getScheduleId());
        if (schedule == null) {
            throw new BusinessException("排课记录不存在");
        }

        LocalDateTime signTime = LocalDateTime.now();
        int successCount = 0;

        for (Long studentId : dto.getStudentIds()) {
            try {
                Attendance attendance = getOne(new LambdaQueryWrapper<Attendance>()
                        .eq(Attendance::getScheduleId, dto.getScheduleId())
                        .eq(Attendance::getStudentId, studentId));

                if (attendance == null) {
                    attendance = new Attendance();
                    attendance.setScheduleId(dto.getScheduleId());
                    attendance.setStudentId(studentId);
                    attendance.setClassId(schedule.getClassId());
                    attendance.setClassHours(schedule.getClassHours());
                }

                // 如果状态是出勤，自动判断是否迟到
                String finalStatus = dto.getStatus();
                if ("present".equals(dto.getStatus())) {
                    finalStatus = determineAttendanceStatusByTime(schedule, signTime);
                }

                attendance.setStatus(finalStatus);
                attendance.setRemark(dto.getRemark());

                if ("present".equals(finalStatus) || "late".equals(finalStatus)) {
                    attendance.setSignTime(signTime);
                }

                saveOrUpdate(attendance);

                // 发布考勤事件
                if ("present".equals(finalStatus) || "late".equals(finalStatus)) {
                    publishAttendanceEvent(attendance, schedule);
                }

                successCount++;
            } catch (Exception e) {
                log.error("批量签到失败: studentId={}, error={}", studentId, e.getMessage());
            }
        }

        log.info("批量签到完成: scheduleId={}, total={}, success={}",
                dto.getScheduleId(), dto.getStudentIds().size(), successCount);

        return successCount > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchSignInWithStatus(BatchAttendanceWithStatusDTO dto) {
        Schedule schedule = scheduleService.getById(dto.getScheduleId());
        if (schedule == null) {
            throw new BusinessException("排课记录不存在");
        }

        LocalDateTime signTime = LocalDateTime.now();
        int successCount = 0;

        for (Map.Entry<Long, String> entry : dto.getStudentStatusMap().entrySet()) {
            Long studentId = entry.getKey();
            String status = entry.getValue();

            try {
                // 验证考勤状态
                validateAttendanceStatus(status);

                Attendance attendance = getOne(new LambdaQueryWrapper<Attendance>()
                        .eq(Attendance::getScheduleId, dto.getScheduleId())
                        .eq(Attendance::getStudentId, studentId));

                if (attendance == null) {
                    attendance = new Attendance();
                    attendance.setScheduleId(dto.getScheduleId());
                    attendance.setStudentId(studentId);
                    attendance.setClassId(schedule.getClassId());
                    attendance.setClassHours(schedule.getClassHours());
                }

                // 如果状态是出勤，自动判断是否迟到
                String finalStatus = status;
                if ("present".equals(status)) {
                    finalStatus = determineAttendanceStatusByTime(schedule, signTime);
                }

                attendance.setStatus(finalStatus);
                attendance.setRemark(dto.getRemark());

                if ("present".equals(finalStatus) || "late".equals(finalStatus)) {
                    attendance.setSignTime(signTime);
                }

                saveOrUpdate(attendance);

                // 发布考勤事件
                if ("present".equals(finalStatus) || "late".equals(finalStatus)) {
                    publishAttendanceEvent(attendance, schedule);
                }

                successCount++;
            } catch (Exception e) {
                log.error("批量签到（带状态）失败: studentId={}, status={}, error={}",
                        studentId, status, e.getMessage());
            }
        }

        log.info("批量签到（带状态）完成: scheduleId={}, total={}, success={}",
                dto.getScheduleId(), dto.getStudentStatusMap().size(), successCount);

        return successCount > 0;
    }

    @Override
    public boolean updateStatus(Long id, String status, String remark) {
        // 验证考勤状态
        validateAttendanceStatus(status);

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

    @Override
    public String determineAttendanceStatus(Long scheduleId) {
        Schedule schedule = scheduleService.getById(scheduleId);
        if (schedule == null) {
            throw new BusinessException("排课记录不存在");
        }
        return determineAttendanceStatusByTime(schedule, LocalDateTime.now());
    }

    @Override
    public Map<String, Object> getComprehensiveStats(LocalDate startDate, LocalDate endDate, Long classId, Long studentId) {
        Map<String, Object> result = new HashMap<>();

        // 总体统计
        Map<String, Object> summary = new HashMap<>();
        int totalSchedules = 0;
        int totalAttendances = 0;
        int presentCount = 0;
        int absentCount = 0;
        int lateCount = 0;
        int leaveCount = 0;

        // 按班级统计
        List<Map<String, Object>> classStats = new ArrayList<>();
        if (classId != null) {
            // 单个班级统计
            Map<String, Object> classStat = getClassAttendanceStats(classId, startDate, endDate);
            Map<String, Object> classStatItem = new HashMap<>();
            classStatItem.put("classId", classId);
            classStatItem.put("className", "班级" + classId); // TODO: 从班级服务获取班级名称
            classStatItem.put("totalStudents", 0); // TODO: 从班级服务获取学员数
            classStatItem.put("totalSchedules", classStat.get("total"));
            classStatItem.put("presentCount", classStat.get("present"));
            classStatItem.put("absentCount", classStat.get("absent"));
            classStatItem.put("lateCount", classStat.get("late"));
            classStatItem.put("leaveCount", classStat.get("leave"));
            classStatItem.put("attendanceRate", classStat.get("attendanceRate"));
            classStats.add(classStatItem);

            // 累加到总体统计
            totalSchedules += (Integer) classStat.get("total");
            presentCount += (Integer) classStat.get("present");
            absentCount += (Integer) classStat.get("absent");
            lateCount += (Integer) classStat.get("late");
            leaveCount += (Integer) classStat.get("leave");
        }

        // 按学员统计
        List<Map<String, Object>> studentStats = new ArrayList<>();
        if (studentId != null) {
            // 单个学员统计
            Map<String, Object> studentStat = getAttendanceStats(studentId, classId, startDate, endDate);
            Map<String, Object> studentStatItem = new HashMap<>();
            studentStatItem.put("studentId", studentId);
            studentStatItem.put("studentName", "学员" + studentId); // TODO: 从学员服务获取学员姓名
            studentStatItem.put("className", classId != null ? "班级" + classId : "");
            studentStatItem.put("totalSchedules", studentStat.get("total"));
            studentStatItem.put("presentCount", studentStat.get("present"));
            studentStatItem.put("absentCount", studentStat.get("absent"));
            studentStatItem.put("lateCount", studentStat.get("late"));
            studentStatItem.put("leaveCount", studentStat.get("leave"));
            studentStatItem.put("attendanceRate", studentStat.get("attendanceRate"));
            studentStats.add(studentStatItem);
        } else if (classId != null) {
            // 班级所有学员统计
            // TODO: 获取班级所有学员并统计
        }

        // 计算总体出勤率
        totalAttendances = presentCount + absentCount + lateCount + leaveCount;
        double attendanceRate = totalAttendances > 0 ? (presentCount + lateCount) * 100.0 / totalAttendances : 0;

        summary.put("totalSchedules", totalSchedules);
        summary.put("totalAttendances", totalAttendances);
        summary.put("presentCount", presentCount);
        summary.put("absentCount", absentCount);
        summary.put("lateCount", lateCount);
        summary.put("leaveCount", leaveCount);
        summary.put("attendanceRate", Math.round(attendanceRate * 100.0) / 100.0);

        result.put("classStat", classStats);
        result.put("studentStats", studentStats);
        result.put("summary", summary);

        return result;
    }

    /**
     * 根据签到时间和课程时间判断考勤状态
     */
    private String determineAttendanceStatusByTime(Schedule schedule, LocalDateTime signTime) {
        LocalDate scheduleDate = schedule.getScheduleDate();
        LocalTime startTime = schedule.getStartTime();

        // 课程开始时间
        LocalDateTime classStartTime = LocalDateTime.of(scheduleDate, startTime);

        // 如果签到时间在课程开始时间之前或等于，则为正常出勤
        if (signTime.isBefore(classStartTime) || signTime.isEqual(classStartTime)) {
            return "present";
        }

        // 如果签到时间在课程开始时间之后，判断是否超过迟到容忍时间
        LocalDateTime lateThreshold = classStartTime.plusMinutes(LATE_TOLERANCE_MINUTES);
        if (signTime.isBefore(lateThreshold) || signTime.isEqual(lateThreshold)) {
            return "late";
        }

        // 超过迟到容忍时间，标记为迟到（也可以根据业务需求标记为缺勤）
        return "late";
    }

    /**
     * 验证考勤状态
     */
    private void validateAttendanceStatus(String status) {
        if (!"present".equals(status) && !"absent".equals(status)
                && !"late".equals(status) && !"leave".equals(status)) {
            throw new BusinessException("无效的考勤状态: " + status);
        }
    }

    /**
     * 发布考勤事件
     */
    private void publishAttendanceEvent(Attendance attendance, Schedule schedule) {
        try {
            AttendanceSignedEvent event = new AttendanceSignedEvent(
                    this,
                    attendance.getId(),
                    attendance.getScheduleId(),
                    attendance.getStudentId(),
                    attendance.getClassId(),
                    schedule.getCourseId(),
                    attendance.getStatus(),
                    attendance.getSignTime(),
                    attendance.getClassHours(),
                    schedule.getCampusId()
            );
            eventPublisher.publishEvent(event);
            log.info("发布考勤事件: attendanceId={}, studentId={}, status={}",
                    attendance.getId(), attendance.getStudentId(), attendance.getStatus());
        } catch (Exception e) {
            log.error("发布考勤事件失败: attendanceId={}, error={}",
                    attendance.getId(), e.getMessage());
        }
    }
}
