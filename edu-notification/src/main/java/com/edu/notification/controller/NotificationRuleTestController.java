package com.edu.notification.controller;

import com.edu.common.core.Result;
import com.edu.notification.event.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 通知规则测试Controller
 */
@Tag(name = "通知规则测试")
@RestController
@RequestMapping("/notification/rule/test")
@RequiredArgsConstructor
public class NotificationRuleTestController {

    private final ApplicationEventPublisher eventPublisher;

    @Operation(summary = "测试学员注册事件")
    @PostMapping("/student-register")
    public Result<Void> testStudentRegister(
            @Parameter(description = "学员ID") @RequestParam Long studentId,
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId) {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("studentId", studentId);
        eventData.put("studentName", "测试学员");
        eventData.put("phone", "13800138000");
        eventData.put("registerTime", LocalDateTime.now());

        StudentRegisterEvent event = new StudentRegisterEvent(this, eventData, campusId, studentId);
        eventPublisher.publishEvent(event);

        return Result.success();
    }

    @Operation(summary = "测试合同签署事件")
    @PostMapping("/contract-signed")
    public Result<Void> testContractSigned(
            @Parameter(description = "合同ID") @RequestParam Long contractId,
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId) {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("contractId", contractId);
        eventData.put("contractNo", "HT202601310001");
        eventData.put("studentName", "测试学员");
        eventData.put("amount", new BigDecimal("10000.00"));
        eventData.put("signTime", LocalDateTime.now());

        ContractSignedEvent event = new ContractSignedEvent(this, eventData, campusId, contractId);
        eventPublisher.publishEvent(event);

        return Result.success();
    }

    @Operation(summary = "测试支付成功事件")
    @PostMapping("/payment-success")
    public Result<Void> testPaymentSuccess(
            @Parameter(description = "支付ID") @RequestParam Long paymentId,
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId) {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("paymentId", paymentId);
        eventData.put("paymentNo", "PAY202601310001");
        eventData.put("studentName", "测试学员");
        eventData.put("amount", new BigDecimal("5000.00"));
        eventData.put("paymentTime", LocalDateTime.now());

        PaymentSuccessEvent event = new PaymentSuccessEvent(this, eventData, campusId, paymentId);
        eventPublisher.publishEvent(event);

        return Result.success();
    }

    @Operation(summary = "测试上课提醒事件")
    @PostMapping("/class-remind")
    public Result<Void> testClassRemind(
            @Parameter(description = "课程ID") @RequestParam Long classId,
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId) {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("classId", classId);
        eventData.put("className", "数学课");
        eventData.put("teacherName", "张老师");
        eventData.put("studentName", "测试学员");
        eventData.put("startTime", LocalDateTime.now().plusMinutes(30));

        ClassRemindEvent event = new ClassRemindEvent(this, eventData, campusId, classId);
        eventPublisher.publishEvent(event);

        return Result.success();
    }

    @Operation(summary = "测试缺勤事件")
    @PostMapping("/attendance-absent")
    public Result<Void> testAttendanceAbsent(
            @Parameter(description = "考勤ID") @RequestParam Long attendanceId,
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId) {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("attendanceId", attendanceId);
        eventData.put("studentName", "测试学员");
        eventData.put("className", "数学课");
        eventData.put("absentTime", LocalDateTime.now());

        AttendanceAbsentEvent event = new AttendanceAbsentEvent(this, eventData, campusId, attendanceId);
        eventPublisher.publishEvent(event);

        return Result.success();
    }

    @Operation(summary = "测试课时不足事件")
    @PostMapping("/class-hour-low")
    public Result<Void> testClassHourLow(
            @Parameter(description = "账户ID") @RequestParam Long accountId,
            @Parameter(description = "剩余课时") @RequestParam(defaultValue = "3") Integer remainingClassHours,
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId) {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("accountId", accountId);
        eventData.put("studentName", "测试学员");
        eventData.put("courseName", "数学课程包");
        eventData.put("remainingClassHours", remainingClassHours);
        eventData.put("totalClassHours", 50);

        ClassHourLowEvent event = new ClassHourLowEvent(this, eventData, campusId, accountId);
        eventPublisher.publishEvent(event);

        return Result.success();
    }

    @Operation(summary = "测试试听预约事件")
    @PostMapping("/trial-lesson")
    public Result<Void> testTrialLesson(
            @Parameter(description = "试听ID") @RequestParam Long trialId,
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId) {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("trialId", trialId);
        eventData.put("studentName", "测试学员");
        eventData.put("courseName", "数学试听课");
        eventData.put("teacherName", "张老师");
        eventData.put("trialTime", LocalDateTime.now().plusDays(1));

        TrialLessonEvent event = new TrialLessonEvent(this, eventData, campusId, trialId);
        eventPublisher.publishEvent(event);

        return Result.success();
    }

    @Operation(summary = "测试合同到期事件")
    @PostMapping("/contract-expire")
    public Result<Void> testContractExpire(
            @Parameter(description = "合同ID") @RequestParam Long contractId,
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId) {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("contractId", contractId);
        eventData.put("contractNo", "HT202601310001");
        eventData.put("studentName", "测试学员");
        eventData.put("endDate", LocalDateTime.now().plusDays(7));
        eventData.put("daysRemaining", 7);

        ContractExpireEvent event = new ContractExpireEvent(this, eventData, campusId, contractId);
        eventPublisher.publishEvent(event);

        return Result.success();
    }
}
