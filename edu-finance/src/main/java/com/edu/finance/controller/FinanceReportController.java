package com.edu.finance.controller;

import com.edu.common.core.Result;
import com.edu.common.report.ExportFormat;
import com.edu.common.report.ReportExportRequest;
import com.edu.common.report.ReportExportResult;
import com.edu.finance.domain.dto.ClassHourReportQueryDTO;
import com.edu.finance.domain.dto.RevenueReportQueryDTO;
import com.edu.finance.domain.vo.*;
import com.edu.finance.service.FinanceReportService;
import com.edu.finance.service.ReportExportFacadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 财务报表控制器
 */
@Tag(name = "财务报表管理")
@RestController
@RequestMapping("/finance/report")
@RequiredArgsConstructor
public class FinanceReportController {

    private final FinanceReportService financeReportService;
    private final ReportExportFacadeService reportExportFacadeService;

    // ==================== 收入报表 ====================

    @Operation(summary = "获取收入汇总统计", description = "统计总收入、合同数、学员数、平均客单价等")
    @GetMapping("/revenue/summary")
    public Result<RevenueSummaryVO> getRevenueSummary(
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId,
            @Parameter(description = "课程类型ID") @RequestParam(required = false) Long courseTypeId,
            @Parameter(description = "支付方式") @RequestParam(required = false) String paymentMethod) {

        RevenueReportQueryDTO query = new RevenueReportQueryDTO();
        query.setStartDate(startDate);
        query.setEndDate(endDate);
        query.setCampusId(campusId);
        query.setCourseTypeId(courseTypeId);
        query.setPaymentMethod(paymentMethod);

        return Result.success(financeReportService.getRevenueSummary(query));
    }

    @Operation(summary = "按时间维度统计收入", description = "支持按日、周、月、季、年统计")
    @GetMapping("/revenue/by-time")
    public Result<List<RevenueReportVO>> getRevenueByTime(
            @Parameter(description = "开始日期", required = true) @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期", required = true) @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "时间维度：day-日，week-周，month-月，quarter-季，year-年", required = true) @RequestParam String timeDimension,
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId,
            @Parameter(description = "课程类型ID") @RequestParam(required = false) Long courseTypeId,
            @Parameter(description = "支付方式") @RequestParam(required = false) String paymentMethod,
            @Parameter(description = "排序字段") @RequestParam(required = false) String sortField,
            @Parameter(description = "排序方式：asc-升序，desc-降序") @RequestParam(defaultValue = "desc") String sortOrder) {

        RevenueReportQueryDTO query = new RevenueReportQueryDTO();
        query.setStartDate(startDate);
        query.setEndDate(endDate);
        query.setTimeDimension(timeDimension);
        query.setCampusId(campusId);
        query.setCourseTypeId(courseTypeId);
        query.setPaymentMethod(paymentMethod);
        query.setSortField(sortField);
        query.setSortOrder(sortOrder);

        return Result.success(financeReportService.getRevenueByTime(query));
    }

    @Operation(summary = "按校区维度统计收入")
    @GetMapping("/revenue/by-campus")
    public Result<List<RevenueReportVO>> getRevenueByCampus(
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "课程类型ID") @RequestParam(required = false) Long courseTypeId,
            @Parameter(description = "支付方式") @RequestParam(required = false) String paymentMethod,
            @Parameter(description = "排序字段") @RequestParam(required = false) String sortField,
            @Parameter(description = "排序方式：asc-升序，desc-降序") @RequestParam(defaultValue = "desc") String sortOrder) {

        RevenueReportQueryDTO query = new RevenueReportQueryDTO();
        query.setStartDate(startDate);
        query.setEndDate(endDate);
        query.setCourseTypeId(courseTypeId);
        query.setPaymentMethod(paymentMethod);
        query.setSortField(sortField);
        query.setSortOrder(sortOrder);

        return Result.success(financeReportService.getRevenueByCampus(query));
    }

    @Operation(summary = "按课程类型统计收入")
    @GetMapping("/revenue/by-course-type")
    public Result<List<RevenueReportVO>> getRevenueByCourseType(
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId,
            @Parameter(description = "支付方式") @RequestParam(required = false) String paymentMethod,
            @Parameter(description = "排序字段") @RequestParam(required = false) String sortField,
            @Parameter(description = "排序方式：asc-升序，desc-降序") @RequestParam(defaultValue = "desc") String sortOrder) {

        RevenueReportQueryDTO query = new RevenueReportQueryDTO();
        query.setStartDate(startDate);
        query.setEndDate(endDate);
        query.setCampusId(campusId);
        query.setPaymentMethod(paymentMethod);
        query.setSortField(sortField);
        query.setSortOrder(sortOrder);

        return Result.success(financeReportService.getRevenueByCourseType(query));
    }

    @Operation(summary = "按支付方式统计收入")
    @GetMapping("/revenue/by-payment-method")
    public Result<List<RevenueReportVO>> getRevenueByPaymentMethod(
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId,
            @Parameter(description = "课程类型ID") @RequestParam(required = false) Long courseTypeId,
            @Parameter(description = "排序字段") @RequestParam(required = false) String sortField,
            @Parameter(description = "排序方式：asc-升序，desc-降序") @RequestParam(defaultValue = "desc") String sortOrder) {

        RevenueReportQueryDTO query = new RevenueReportQueryDTO();
        query.setStartDate(startDate);
        query.setEndDate(endDate);
        query.setCampusId(campusId);
        query.setCourseTypeId(courseTypeId);
        query.setSortField(sortField);
        query.setSortOrder(sortOrder);

        return Result.success(financeReportService.getRevenueByPaymentMethod(query));
    }

    // ==================== 课消报表 ====================

    @Operation(summary = "获取课消汇总统计", description = "统计总课时、已用课时、剩余课时、课消率等")
    @GetMapping("/class-hour/summary")
    public Result<ClassHourSummaryVO> getClassHourSummary(
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId,
            @Parameter(description = "班级ID") @RequestParam(required = false) Long classId,
            @Parameter(description = "课程ID") @RequestParam(required = false) Long courseId,
            @Parameter(description = "教师ID") @RequestParam(required = false) Long teacherId) {

        ClassHourReportQueryDTO query = new ClassHourReportQueryDTO();
        query.setStartDate(startDate);
        query.setEndDate(endDate);
        query.setCampusId(campusId);
        query.setClassId(classId);
        query.setCourseId(courseId);
        query.setTeacherId(teacherId);

        return Result.success(financeReportService.getClassHourSummary(query));
    }

    @Operation(summary = "获取课消趋势", description = "按日期统计课消情况")
    @GetMapping("/class-hour/trend")
    public Result<List<ClassHourTrendVO>> getClassHourTrend(
            @Parameter(description = "开始日期", required = true) @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期", required = true) @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId,
            @Parameter(description = "班级ID") @RequestParam(required = false) Long classId,
            @Parameter(description = "课程ID") @RequestParam(required = false) Long courseId,
            @Parameter(description = "教师ID") @RequestParam(required = false) Long teacherId) {

        ClassHourReportQueryDTO query = new ClassHourReportQueryDTO();
        query.setStartDate(startDate);
        query.setEndDate(endDate);
        query.setCampusId(campusId);
        query.setClassId(classId);
        query.setCourseId(courseId);
        query.setTeacherId(teacherId);

        return Result.success(financeReportService.getClassHourTrend(query));
    }

    @Operation(summary = "按班级统计课消")
    @GetMapping("/class-hour/by-class")
    public Result<List<ClassHourReportVO>> getClassHourByClass(
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId,
            @Parameter(description = "课程ID") @RequestParam(required = false) Long courseId,
            @Parameter(description = "排序字段") @RequestParam(required = false) String sortField,
            @Parameter(description = "排序方式：asc-升序，desc-降序") @RequestParam(defaultValue = "desc") String sortOrder) {

        ClassHourReportQueryDTO query = new ClassHourReportQueryDTO();
        query.setStartDate(startDate);
        query.setEndDate(endDate);
        query.setCampusId(campusId);
        query.setCourseId(courseId);
        query.setSortField(sortField);
        query.setSortOrder(sortOrder);

        return Result.success(financeReportService.getClassHourByClass(query));
    }

    @Operation(summary = "按课程统计课消")
    @GetMapping("/class-hour/by-course")
    public Result<List<ClassHourReportVO>> getClassHourByCourse(
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId,
            @Parameter(description = "排序字段") @RequestParam(required = false) String sortField,
            @Parameter(description = "排序方式：asc-升序，desc-降序") @RequestParam(defaultValue = "desc") String sortOrder) {

        ClassHourReportQueryDTO query = new ClassHourReportQueryDTO();
        query.setStartDate(startDate);
        query.setEndDate(endDate);
        query.setCampusId(campusId);
        query.setSortField(sortField);
        query.setSortOrder(sortOrder);

        return Result.success(financeReportService.getClassHourByCourse(query));
    }

    @Operation(summary = "按教师统计课消")
    @GetMapping("/class-hour/by-teacher")
    public Result<List<ClassHourReportVO>> getClassHourByTeacher(
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId,
            @Parameter(description = "排序字段") @RequestParam(required = false) String sortField,
            @Parameter(description = "排序方式：asc-升序，desc-降序") @RequestParam(defaultValue = "desc") String sortOrder) {

        ClassHourReportQueryDTO query = new ClassHourReportQueryDTO();
        query.setStartDate(startDate);
        query.setEndDate(endDate);
        query.setCampusId(campusId);
        query.setSortField(sortField);
        query.setSortOrder(sortOrder);

        return Result.success(financeReportService.getClassHourByTeacher(query));
    }

    // ==================== 报表导出 ====================

    @Operation(summary = "导出收入报表", description = "支持Excel和PDF格式，支持同步和异步导出")
    @PostMapping("/revenue/export")
    public Result<ReportExportResult> exportRevenueReport(
            @Parameter(description = "导出格式：excel或pdf", required = true) @RequestParam String format,
            @Parameter(description = "是否异步导出") @RequestParam(defaultValue = "false") Boolean async,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId,
            @Parameter(description = "课程类型ID") @RequestParam(required = false) Long courseTypeId,
            @Parameter(description = "支付方式") @RequestParam(required = false) String paymentMethod,
            HttpServletResponse response) {

        ReportExportRequest request = new ReportExportRequest();
        request.setReportType("revenue");
        request.setFormat(ExportFormat.fromCode(format));
        request.setTitle("收入报表");
        request.setAsync(async);

        Map<String, Object> params = new HashMap<>();
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("campusId", campusId);
        params.put("courseTypeId", courseTypeId);
        params.put("paymentMethod", paymentMethod);
        request.setParams(params);

        if (async) {
            // 异步导出，返回任务ID
            ReportExportResult result = reportExportFacadeService.exportRevenueReportAsync(request);
            return Result.success(result);
        } else {
            // 同步导出，直接下载
            reportExportFacadeService.exportRevenueReport(request, response);
            return null;
        }
    }

    @Operation(summary = "导出课消报表", description = "支持Excel和PDF格式，支持同步和异步导出")
    @PostMapping("/class-hour/export")
    public Result<ReportExportResult> exportClassHourReport(
            @Parameter(description = "导出格式：excel或pdf", required = true) @RequestParam String format,
            @Parameter(description = "是否异步导出") @RequestParam(defaultValue = "false") Boolean async,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId,
            @Parameter(description = "班级ID") @RequestParam(required = false) Long classId,
            @Parameter(description = "课程ID") @RequestParam(required = false) Long courseId,
            @Parameter(description = "教师ID") @RequestParam(required = false) Long teacherId,
            HttpServletResponse response) {

        ReportExportRequest request = new ReportExportRequest();
        request.setReportType("classHour");
        request.setFormat(ExportFormat.fromCode(format));
        request.setTitle("课消报表");
        request.setAsync(async);

        Map<String, Object> params = new HashMap<>();
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("campusId", campusId);
        params.put("classId", classId);
        params.put("courseId", courseId);
        params.put("teacherId", teacherId);
        request.setParams(params);

        if (async) {
            ReportExportResult result = reportExportFacadeService.exportClassHourReportAsync(request);
            return Result.success(result);
        } else {
            reportExportFacadeService.exportClassHourReport(request, response);
            return null;
        }
    }

    @Operation(summary = "查询导出任务状态")
    @GetMapping("/export/status/{taskId}")
    public Result<ReportExportResult> getExportTaskStatus(
            @Parameter(description = "任务ID", required = true) @PathVariable String taskId) {
        ReportExportResult result = reportExportFacadeService.getExportTaskStatus(taskId);
        return Result.success(result);
    }

    @Operation(summary = "取消导出任务")
    @PostMapping("/export/cancel/{taskId}")
    public Result<Void> cancelExportTask(
            @Parameter(description = "任务ID", required = true) @PathVariable String taskId) {
        reportExportFacadeService.cancelExportTask(taskId);
        return Result.success();
    }
}
