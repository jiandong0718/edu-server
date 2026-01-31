package com.edu.finance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.R;
import com.edu.finance.domain.dto.AccountsReceivableQueryDTO;
import com.edu.finance.domain.dto.CollectionReminderDTO;
import com.edu.finance.domain.vo.AccountsReceivableVO;
import com.edu.finance.domain.vo.AgingAnalysisVO;
import com.edu.finance.domain.vo.CampusArrearsStatisticsVO;
import com.edu.finance.service.AccountsReceivableReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 应收账款报表控制器
 */
@Tag(name = "应收账款报表")
@RestController
@RequestMapping("/finance/report/accounts-receivable")
@RequiredArgsConstructor
public class AccountsReceivableReportController {

    private final AccountsReceivableReportService accountsReceivableReportService;

    @Operation(summary = "分页查询应收账款列表", description = "支持多维度查询：校区、学员、合同、欠费金额范围、账龄范围")
    @GetMapping("/page")
    public R<Page<AccountsReceivableVO>> getAccountsReceivablePage(
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId,
            @Parameter(description = "学员ID") @RequestParam(required = false) Long studentId,
            @Parameter(description = "学员姓名") @RequestParam(required = false) String studentName,
            @Parameter(description = "学员手机号") @RequestParam(required = false) String studentPhone,
            @Parameter(description = "合同编号") @RequestParam(required = false) String contractNo,
            @Parameter(description = "最小欠费金额") @RequestParam(required = false) java.math.BigDecimal minArrearsAmount,
            @Parameter(description = "最大欠费金额") @RequestParam(required = false) java.math.BigDecimal maxArrearsAmount,
            @Parameter(description = "账龄范围：30-30天内，30_60-30-60天，60_90-60-90天，90_plus-90天以上") @RequestParam(required = false) String agingRange,
            @Parameter(description = "开始日期") @RequestParam(required = false) java.time.LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) java.time.LocalDate endDate,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {

        AccountsReceivableQueryDTO query = new AccountsReceivableQueryDTO();
        query.setCampusId(campusId);
        query.setStudentId(studentId);
        query.setStudentName(studentName);
        query.setStudentPhone(studentPhone);
        query.setContractNo(contractNo);
        query.setMinArrearsAmount(minArrearsAmount);
        query.setMaxArrearsAmount(maxArrearsAmount);
        query.setAgingRange(agingRange);
        query.setStartDate(startDate);
        query.setEndDate(endDate);
        query.setPageNum(pageNum);
        query.setPageSize(pageSize);

        return R.ok(accountsReceivableReportService.getAccountsReceivablePage(query));
    }

    @Operation(summary = "获取账龄分析", description = "统计不同账龄范围的欠费金额和合同数")
    @GetMapping("/aging-analysis")
    public R<AgingAnalysisVO> getAgingAnalysis(
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId) {
        return R.ok(accountsReceivableReportService.getAgingAnalysis(campusId));
    }

    @Operation(summary = "按校区统计欠费情况", description = "统计各校区的欠费金额、学员数、合同数")
    @GetMapping("/campus-statistics")
    public R<List<CampusArrearsStatisticsVO>> getCampusArrearsStatistics() {
        return R.ok(accountsReceivableReportService.getCampusArrearsStatistics());
    }

    @Operation(summary = "发送催缴提醒", description = "向选中的欠费学员发送催缴提醒（短信/站内信）")
    @PostMapping("/send-reminder")
    public R<Integer> sendCollectionReminder(@Valid @RequestBody CollectionReminderDTO dto) {
        Integer successCount = accountsReceivableReportService.sendCollectionReminder(dto);
        return R.ok("成功发送" + successCount + "条催缴提醒", successCount);
    }

    @Operation(summary = "导出应收账款报表", description = "导出Excel格式的应收账款报表")
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportAccountsReceivable(
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId,
            @Parameter(description = "学员ID") @RequestParam(required = false) Long studentId,
            @Parameter(description = "学员姓名") @RequestParam(required = false) String studentName,
            @Parameter(description = "学员手机号") @RequestParam(required = false) String studentPhone,
            @Parameter(description = "合同编号") @RequestParam(required = false) String contractNo,
            @Parameter(description = "最小欠费金额") @RequestParam(required = false) java.math.BigDecimal minArrearsAmount,
            @Parameter(description = "最大欠费金额") @RequestParam(required = false) java.math.BigDecimal maxArrearsAmount,
            @Parameter(description = "账龄范围") @RequestParam(required = false) String agingRange,
            @Parameter(description = "开始日期") @RequestParam(required = false) java.time.LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) java.time.LocalDate endDate) {

        try {
            AccountsReceivableQueryDTO query = new AccountsReceivableQueryDTO();
            query.setCampusId(campusId);
            query.setStudentId(studentId);
            query.setStudentName(studentName);
            query.setStudentPhone(studentPhone);
            query.setContractNo(contractNo);
            query.setMinArrearsAmount(minArrearsAmount);
            query.setMaxArrearsAmount(maxArrearsAmount);
            query.setAgingRange(agingRange);
            query.setStartDate(startDate);
            query.setEndDate(endDate);

            byte[] excelBytes = accountsReceivableReportService.exportAccountsReceivable(query);

            String fileName = "应收账款报表_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx";
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
