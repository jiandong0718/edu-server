package com.edu.marketing.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.R;
import com.edu.marketing.domain.dto.TrialAppointmentDTO;
import com.edu.marketing.domain.dto.TrialFeedbackDTO;
import com.edu.marketing.domain.dto.TrialLessonQueryDTO;
import com.edu.marketing.domain.dto.TrialSignInDTO;
import com.edu.marketing.domain.entity.TrialLesson;
import com.edu.marketing.domain.vo.AdvisorPerformanceVO;
import com.edu.marketing.domain.vo.ConversionFunnelVO;
import com.edu.marketing.domain.vo.TrialLessonVO;
import com.edu.marketing.service.TrialLessonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 试听记录管理控制器
 *
 * @author edu
 * @since 2024-01-30
 */
@Tag(name = "试听记录管理")
@RestController
@RequestMapping("/marketing/trial")
@RequiredArgsConstructor
public class TrialLessonController {

    private final TrialLessonService trialLessonService;

    @Operation(summary = "分页查询试听记录列表")
    @GetMapping("/page")
    public R<Page<TrialLesson>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            TrialLesson query) {
        Page<TrialLesson> page = new Page<>(pageNum, pageSize);
        trialLessonService.pageList(page, query);
        return R.ok(page);
    }

    @Operation(summary = "分页查询试听记录VO列表（包含关联信息）")
    @GetMapping("/page-vo")
    public R<Page<TrialLessonVO>> pageVO(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            TrialLessonQueryDTO query) {
        Page<TrialLessonVO> page = new Page<>(pageNum, pageSize);
        trialLessonService.pageListVO(page, query);
        return R.ok(page);
    }

    @Operation(summary = "获取试听记录详情")
    @GetMapping("/{id}")
    public R<TrialLesson> getById(@Parameter(description = "试听记录ID") @PathVariable Long id) {
        return R.ok(trialLessonService.getById(id));
    }

    @Operation(summary = "创建试听预约")
    @PostMapping("/appointment")
    public R<Long> createAppointment(@RequestBody TrialAppointmentDTO dto) {
        return R.ok(trialLessonService.createAppointment(dto));
    }

    @Operation(summary = "试听签到")
    @PostMapping("/sign-in")
    public R<Boolean> signIn(@RequestBody TrialSignInDTO dto) {
        return R.ok(trialLessonService.signIn(dto));
    }

    @Operation(summary = "提交试听反馈")
    @PostMapping("/feedback")
    public R<Boolean> submitFeedback(@RequestBody TrialFeedbackDTO dto) {
        return R.ok(trialLessonService.submitFeedback(dto));
    }

    @Operation(summary = "取消试听预约")
    @DeleteMapping("/{id}/cancel")
    public R<Boolean> cancelAppointment(@Parameter(description = "试听记录ID") @PathVariable Long id) {
        return R.ok(trialLessonService.cancelAppointment(id));
    }

    @Operation(summary = "获取线索的试听记录列表")
    @GetMapping("/lead/{leadId}")
    public R<List<TrialLesson>> getByLeadId(@Parameter(description = "线索ID") @PathVariable Long leadId) {
        return R.ok(trialLessonService.getByLeadId(leadId));
    }

    @Operation(summary = "获取学员的试听记录列表")
    @GetMapping("/student/{studentId}")
    public R<List<TrialLesson>> getByStudentId(@Parameter(description = "学员ID") @PathVariable Long studentId) {
        return R.ok(trialLessonService.getByStudentId(studentId));
    }

    // ==================== 统计分析 ====================

    @Operation(summary = "获取招生转化漏斗统计")
    @GetMapping("/conversion-funnel")
    public R<ConversionFunnelVO> getConversionFunnel(
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return R.ok(trialLessonService.getConversionFunnel(campusId, startDate, endDate));
    }

    @Operation(summary = "获取顾问业绩统计")
    @GetMapping("/advisor-performance")
    public R<List<AdvisorPerformanceVO>> getAdvisorPerformance(
            @Parameter(description = "顾问ID") @RequestParam(required = false) Long advisorId,
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return R.ok(trialLessonService.getAdvisorPerformance(advisorId, campusId, startDate, endDate));
    }
}
