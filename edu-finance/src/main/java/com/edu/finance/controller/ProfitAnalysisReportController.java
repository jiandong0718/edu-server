package com.edu.finance.controller;

import com.edu.common.core.R;
import com.edu.finance.domain.dto.ProfitAnalysisQueryDTO;
import com.edu.finance.domain.vo.*;
import com.edu.finance.service.ProfitAnalysisReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 利润分析报表控制器
 */
@Tag(name = "利润分析报表")
@RestController
@RequestMapping("/finance/report/profit-analysis")
@RequiredArgsConstructor
public class ProfitAnalysisReportController {

    private final ProfitAnalysisReportService profitAnalysisReportService;

    @Operation(summary = "获取利润分析概览", description = "统计总收入、总成本、毛利润、毛利率等")
    @GetMapping("/overview")
    public R<ProfitAnalysisOverviewVO> getProfitAnalysisOverview(
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId,
            @Parameter(description = "课程类型ID") @RequestParam(required = false) Long courseTypeId,
            @Parameter(description = "开始日期", required = true) @RequestParam LocalDate startDate,
            @Parameter(description = "结束日期", required = true) @RequestParam LocalDate endDate) {

        ProfitAnalysisQueryDTO query = new ProfitAnalysisQueryDTO();
        query.setCampusId(campusId);
        query.setCourseTypeId(courseTypeId);
        query.setStartDate(startDate);
        query.setEndDate(endDate);

        return R.ok(profitAnalysisReportService.getProfitAnalysisOverview(query));
    }

    @Operation(summary = "按校区分析利润", description = "统计各校区的收入、成本、毛利润、毛利率")
    @GetMapping("/by-campus")
    public R<List<CampusProfitAnalysisVO>> getCampusProfitAnalysis(
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId,
            @Parameter(description = "开始日期", required = true) @RequestParam LocalDate startDate,
            @Parameter(description = "结束日期", required = true) @RequestParam LocalDate endDate) {

        ProfitAnalysisQueryDTO query = new ProfitAnalysisQueryDTO();
        query.setCampusId(campusId);
        query.setStartDate(startDate);
        query.setEndDate(endDate);

        return R.ok(profitAnalysisReportService.getCampusProfitAnalysis(query));
    }

    @Operation(summary = "按课程类型分析利润", description = "统计各课程类型的收入、成本、毛利润、毛利率")
    @GetMapping("/by-course-type")
    public R<List<CourseTypeProfitAnalysisVO>> getCourseTypeProfitAnalysis(
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId,
            @Parameter(description = "课程类型ID") @RequestParam(required = false) Long courseTypeId,
            @Parameter(description = "开始日期", required = true) @RequestParam LocalDate startDate,
            @Parameter(description = "结束日期", required = true) @RequestParam LocalDate endDate) {

        ProfitAnalysisQueryDTO query = new ProfitAnalysisQueryDTO();
        query.setCampusId(campusId);
        query.setCourseTypeId(courseTypeId);
        query.setStartDate(startDate);
        query.setEndDate(endDate);

        return R.ok(profitAnalysisReportService.getCourseTypeProfitAnalysis(query));
    }

    @Operation(summary = "获取利润趋势分析", description = "按时间维度统计利润趋势（自动选择按日或按月）")
    @GetMapping("/trend")
    public R<List<ProfitTrendVO>> getProfitTrend(
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId,
            @Parameter(description = "开始日期", required = true) @RequestParam LocalDate startDate,
            @Parameter(description = "结束日期", required = true) @RequestParam LocalDate endDate) {

        ProfitAnalysisQueryDTO query = new ProfitAnalysisQueryDTO();
        query.setCampusId(campusId);
        query.setStartDate(startDate);
        query.setEndDate(endDate);

        return R.ok(profitAnalysisReportService.getProfitTrend(query));
    }

    @Operation(summary = "导出利润分析报表", description = "导出Excel格式的利润分析报表（包含多个sheet）")
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportProfitAnalysis(
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId,
            @Parameter(description = "课程类型ID") @RequestParam(required = false) Long courseTypeId,
            @Parameter(description = "开始日期", required = true) @RequestParam LocalDate startDate,
            @Parameter(description = "结束日期", required = true) @RequestParam LocalDate endDate) {

        try {
            ProfitAnalysisQueryDTO query = new ProfitAnalysisQueryDTO();
            query.setCampusId(campusId);
            query.setCourseTypeId(courseTypeId);
            query.setStartDate(startDate);
            query.setEndDate(endDate);

            byte[] excelBytes = profitAnalysisReportService.exportProfitAnalysis(query);

            String fileName = "利润分析报表_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx";
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", encodedFileName);
            headers.setContentLength(excelBytes.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
