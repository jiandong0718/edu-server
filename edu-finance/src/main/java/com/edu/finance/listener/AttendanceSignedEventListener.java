package com.edu.finance.listener;

import com.edu.finance.domain.dto.ClassHourDeductDTO;
import com.edu.finance.service.ClassHourAccountService;
import com.edu.finance.service.ClassHourRuleService;
import com.edu.teaching.event.AttendanceSignedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 考勤签到事件监听器
 * 监听学员签到事件，自动扣减课时
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AttendanceSignedEventListener {

    private final ClassHourAccountService classHourAccountService;
    private final ClassHourRuleService classHourRuleService;

    /**
     * 处理考勤签到事件
     * 根据消课规则自动扣减课时
     */
    @Async
    @EventListener
    @Transactional(rollbackFor = Exception.class)
    public void handleAttendanceSignedEvent(AttendanceSignedEvent event) {
        try {
            log.info("收到考勤签到事件: attendanceId={}, studentId={}, courseId={}, status={}",
                    event.getAttendanceId(), event.getStudentId(), event.getCourseId(), event.getStatus());

            // 只有出勤和迟到才扣减课时
            if (!"present".equals(event.getStatus()) && !"late".equals(event.getStatus())) {
                log.info("考勤状态不是出勤或迟到，不扣减课时: status={}", event.getStatus());
                return;
            }

            // 获取班级类型（这里简化处理，实际应该从班级信息中获取）
            // TODO: 从班级服务获取班级类型
            String classType = null;

            // 根据消课规则计算应扣减的课时数
            BigDecimal deductHours = classHourRuleService.calculateDeductHours(
                    event.getCourseId(),
                    classType,
                    event.getCampusId(),
                    event.getClassHours()
            );

            // 扣减课时
            ClassHourDeductDTO dto = new ClassHourDeductDTO();
            dto.setStudentId(event.getStudentId());
            dto.setCourseId(event.getCourseId());
            dto.setScheduleId(event.getScheduleId());
            dto.setHours(deductHours);
            dto.setRemark("签到扣减课时 - 排课ID: " + event.getScheduleId() + ", 考勤ID: " + event.getAttendanceId());

            boolean result = classHourAccountService.deductHours(dto);

            if (result) {
                log.info("签到后课时扣减成功: studentId={}, courseId={}, hours={}",
                        event.getStudentId(), event.getCourseId(), deductHours);
            } else {
                log.warn("签到后课时扣减失败: studentId={}, courseId={}",
                        event.getStudentId(), event.getCourseId());
            }
        } catch (Exception e) {
            log.error("处理考勤签到事件失败: attendanceId={}, studentId={}, error={}",
                    event.getAttendanceId(), event.getStudentId(), e.getMessage(), e);
            // 不抛出异常，避免影响签到流程
        }
    }
}
