package com.edu.finance.service.impl;

import com.edu.common.exception.BusinessException;
import com.edu.common.report.*;
import com.edu.finance.domain.dto.*;
import com.edu.finance.domain.vo.*;
import com.edu.finance.service.AccountsReceivableReportService;
import com.edu.finance.service.FinanceReportService;
import com.edu.finance.service.ReportExportFacadeService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 报表导出门面服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportExportFacadeServiceImpl implements ReportExportFacadeService {

    private final ReportExportService reportExportService = new ReportExportService();
    private final AsyncReportExportService asyncReportExportService;
    private final FinanceReportService financeReportService;
    private final AccountsReceivableReportService accountsReceivableReportService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void exportRevenueReport(ReportExportRequest request, HttpServletResponse response) {
        String fileName = generateFileName("收入报表", request.getFormat());

        if (request.getFormat() == ExportFormat.EXCEL) {
            ExcelExportConfig config = buildRevenueExcelConfig(request);
            reportExportService.exportExcel(response, fileName, config);
        } else if (request.getFormat() == ExportFormat.PDF) {
            PdfExportConfig config = buildRevenuePdfConfig(request);
            reportExportService.exportPdf(response, fileName, config);
        } else {
            throw new BusinessException("不支持的导出格式: " + request.getFormat());
        }
    }

    @Override
    public ReportExportResult exportRevenueReportAsync(ReportExportRequest request) {
        String fileName = generateFileName("收入报表", request.getFormat());

        CompletableFuture<ReportExportResult> future;
        if (request.getFormat() == ExportFormat.EXCEL) {
            ExcelExportConfig config = buildRevenueExcelConfig(request);
            future = asyncReportExportService.exportExcelAsync(fileName, config, null);
        } else if (request.getFormat() == ExportFormat.PDF) {
            PdfExportConfig config = buildRevenuePdfConfig(request);
            future = asyncReportExportService.exportPdfAsync(fileName, config, null);
        } else {
            throw new BusinessException("不支持的导出格式: " + request.getFormat());
        }

        // 返回任务信息
        try {
            return future.get();
        } catch (Exception e) {
            log.error("Async export failed", e);
            return ReportExportResult.failed(e.getMessage());
        }
    }

    @Override
    public void exportClassHourReport(ReportExportRequest request, HttpServletResponse response) {
        String fileName = generateFileName("课消报表", request.getFormat());

        if (request.getFormat() == ExportFormat.EXCEL) {
            ExcelExportConfig config = buildClassHourExcelConfig(request);
            reportExportService.exportExcel(response, fileName, config);
        } else if (request.getFormat() == ExportFormat.PDF) {
            PdfExportConfig config = buildClassHourPdfConfig(request);
            reportExportService.exportPdf(response, fileName, config);
        } else {
            throw new BusinessException("不支持的导出格式: " + request.getFormat());
        }
    }

    @Override
    public ReportExportResult exportClassHourReportAsync(ReportExportRequest request) {
        String fileName = generateFileName("课消报表", request.getFormat());

        CompletableFuture<ReportExportResult> future;
        if (request.getFormat() == ExportFormat.EXCEL) {
            ExcelExportConfig config = buildClassHourExcelConfig(request);
            future = asyncReportExportService.exportExcelAsync(fileName, config, null);
        } else if (request.getFormat() == ExportFormat.PDF) {
            PdfExportConfig config = buildClassHourPdfConfig(request);
            future = asyncReportExportService.exportPdfAsync(fileName, config, null);
        } else {
            throw new BusinessException("不支持的导出格式: " + request.getFormat());
        }

        try {
            return future.get();
        } catch (Exception e) {
            log.error("Async export failed", e);
            return ReportExportResult.failed(e.getMessage());
        }
    }

    @Override
    public void exportAccountsReceivableReport(ReportExportRequest request, HttpServletResponse response) {
        String fileName = generateFileName("应收账款报表", request.getFormat());

        if (request.getFormat() == ExportFormat.EXCEL) {
            ExcelExportConfig config = buildAccountsReceivableExcelConfig(request);
            reportExportService.exportExcel(response, fileName, config);
        } else if (request.getFormat() == ExportFormat.PDF) {
            PdfExportConfig config = buildAccountsReceivablePdfConfig(request);
            reportExportService.exportPdf(response, fileName, config);
        } else {
            throw new BusinessException("不支持的导出格式: " + request.getFormat());
        }
    }

    @Override
    public ReportExportResult exportAccountsReceivableReportAsync(ReportExportRequest request) {
        String fileName = generateFileName("应收账款报表", request.getFormat());

        CompletableFuture<ReportExportResult> future;
        if (request.getFormat() == ExportFormat.EXCEL) {
            ExcelExportConfig config = buildAccountsReceivableExcelConfig(request);
            future = asyncReportExportService.exportExcelAsync(fileName, config, null);
        } else if (request.getFormat() == ExportFormat.PDF) {
            PdfExportConfig config = buildAccountsReceivablePdfConfig(request);
            future = asyncReportExportService.exportPdfAsync(fileName, config, null);
        } else {
            throw new BusinessException("不支持的导出格式: " + request.getFormat());
        }

        try {
            return future.get();
        } catch (Exception e) {
            log.error("Async export failed", e);
            return ReportExportResult.failed(e.getMessage());
        }
    }

    @Override
    public void exportProfitAnalysisReport(ReportExportRequest request, HttpServletResponse response) {
        // TODO: Implement profit analysis report export
        throw new BusinessException("利润分析报表导出功能待实现");
    }

    @Override
    public ReportExportResult exportProfitAnalysisReportAsync(ReportExportRequest request) {
        // TODO: Implement async profit analysis report export
        throw new BusinessException("利润分析报表异步导出功能待实现");
    }

    @Override
    public ReportExportResult getExportTaskStatus(String taskId) {
        return asyncReportExportService.getTaskStatus(taskId);
    }

    @Override
    public void cancelExportTask(String taskId) {
        asyncReportExportService.cancelTask(taskId);
    }

    // ==================== 私有方法 ====================

    /**
     * 构建收入报表 Excel 配置
     */
    private ExcelExportConfig buildRevenueExcelConfig(ReportExportRequest request) {
        RevenueReportQueryDTO query = buildRevenueQuery(request.getParams());

        // 获取报表数据
        List<RevenueReportVO> reportData = financeReportService.getRevenueByTime(query);
        RevenueSummaryVO summary = financeReportService.getRevenueSummary(query);

        // 转换为导出DTO
        List<RevenueReportExportDTO> exportData = reportData.stream()
                .map(this::convertToRevenueExportDTO)
                .collect(Collectors.toList());

        // 构建 Excel 配置
        ExcelExportConfig config = new ExcelExportConfig();
        config.addSheet(new ExcelExportConfig.SheetConfig(
                "收入明细",
                RevenueReportExportDTO.class,
                exportData
        ));

        return config;
    }

    /**
     * 构建收入报表 PDF 配置
     */
    private PdfExportConfig buildRevenuePdfConfig(ReportExportRequest request) {
        RevenueReportQueryDTO query = buildRevenueQuery(request.getParams());

        // 获取报表数据
        List<RevenueReportVO> reportData = financeReportService.getRevenueByTime(query);

        // 构建 PDF 配置
        PdfExportConfig config = new PdfExportConfig();
        config.setTitle("收入报表");
        config.setHeaderText("财务报表 - 收入统计");
        config.setFooterText("机密文件，请妥善保管");

        // 构建表格数据
        List<String> headers = List.of("统计维度", "维度值", "总收入", "新签收入", "续费收入", "退费金额", "实际收入");
        List<List<String>> data = reportData.stream()
                .map(vo -> List.of(
                        vo.getDimension() != null ? vo.getDimension() : "",
                        vo.getDimensionValue() != null ? vo.getDimensionValue() : "",
                        formatMoney(vo.getTotalRevenue()),
                        formatMoney(vo.getNewContractRevenue()),
                        formatMoney(vo.getRenewalRevenue()),
                        formatMoney(vo.getRefundAmount()),
                        formatMoney(vo.getNetRevenue())
                ))
                .collect(Collectors.toList());

        config.addTable(new PdfExportConfig.TableConfig("收入明细", headers, data));

        return config;
    }

    /**
     * 构建课消报表 Excel 配置
     */
    private ExcelExportConfig buildClassHourExcelConfig(ReportExportRequest request) {
        ClassHourReportQueryDTO query = buildClassHourQuery(request.getParams());

        // 获取报表数据
        List<ClassHourReportVO> reportData = financeReportService.getClassHourByClass(query);

        // 转换为导出DTO
        List<ClassHourReportExportDTO> exportData = reportData.stream()
                .map(this::convertToClassHourExportDTO)
                .collect(Collectors.toList());

        // 构建 Excel 配置
        ExcelExportConfig config = new ExcelExportConfig();
        config.addSheet(new ExcelExportConfig.SheetConfig(
                "课消明细",
                ClassHourReportExportDTO.class,
                exportData
        ));

        return config;
    }

    /**
     * 构建课消报表 PDF 配置
     */
    private PdfExportConfig buildClassHourPdfConfig(ReportExportRequest request) {
        ClassHourReportQueryDTO query = buildClassHourQuery(request.getParams());

        // 获取报表数据
        List<ClassHourReportVO> reportData = financeReportService.getClassHourByClass(query);

        // 构建 PDF 配置
        PdfExportConfig config = new PdfExportConfig();
        config.setTitle("课消报表");
        config.setHeaderText("财务报表 - 课时消耗统计");
        config.setFooterText("机密文件，请妥善保管");

        // 构建表格数据
        List<String> headers = List.of("统计维度", "维度值", "总课时", "已用课时", "剩余课时", "课消率", "课消金额");
        List<List<String>> data = reportData.stream()
                .map(vo -> List.of(
                        vo.getDimension() != null ? vo.getDimension() : "",
                        vo.getDimensionValue() != null ? vo.getDimensionValue() : "",
                        formatNumber(vo.getTotalHours()),
                        formatNumber(vo.getUsedHours()),
                        formatNumber(vo.getRemainingHours()),
                        formatPercent(vo.getConsumptionRate()),
                        formatMoney(vo.getConsumptionAmount())
                ))
                .collect(Collectors.toList());

        config.addTable(new PdfExportConfig.TableConfig("课消明细", headers, data));

        return config;
    }

    /**
     * 构建应收账款报表 Excel 配置
     */
    private ExcelExportConfig buildAccountsReceivableExcelConfig(ReportExportRequest request) {
        AccountsReceivableQueryDTO query = buildAccountsReceivableQuery(request.getParams());

        // 获取报表数据
        var page = accountsReceivableReportService.getAccountsReceivablePage(query);
        List<AccountsReceivableVO> reportData = page.getRecords();

        // 转换为导出DTO
        List<AccountsReceivableExportDTO> exportData = reportData.stream()
                .map(this::convertToAccountsReceivableExportDTO)
                .collect(Collectors.toList());

        // 构建 Excel 配置
        ExcelExportConfig config = new ExcelExportConfig();
        config.addSheet(new ExcelExportConfig.SheetConfig(
                "应收账款明细",
                AccountsReceivableExportDTO.class,
                exportData
        ));

        return config;
    }

    /**
     * 构建应收账款报表 PDF 配置
     */
    private PdfExportConfig buildAccountsReceivablePdfConfig(ReportExportRequest request) {
        AccountsReceivableQueryDTO query = buildAccountsReceivableQuery(request.getParams());

        // 获取报表数据
        var page = accountsReceivableReportService.getAccountsReceivablePage(query);
        List<AccountsReceivableVO> reportData = page.getRecords();

        // 构建 PDF 配置
        PdfExportConfig config = new PdfExportConfig();
        config.setTitle("应收账款报表");
        config.setHeaderText("财务报表 - 应收账款统计");
        config.setFooterText("机密文件，请妥善保管");

        // 构建表格数据
        List<String> headers = List.of("学员姓名", "联系电话", "所属校区", "合同金额", "已收金额", "应收金额", "账龄");
        List<List<String>> data = reportData.stream()
                .map(vo -> List.of(
                        vo.getStudentName() != null ? vo.getStudentName() : "",
                        vo.getPhone() != null ? vo.getPhone() : "",
                        vo.getCampusName() != null ? vo.getCampusName() : "",
                        formatMoney(vo.getContractAmount()),
                        formatMoney(vo.getPaidAmount()),
                        formatMoney(vo.getReceivableAmount()),
                        vo.getAgingDays() != null ? vo.getAgingDays() + "天" : ""
                ))
                .collect(Collectors.toList());

        config.addTable(new PdfExportConfig.TableConfig("应收账款明细", headers, data));

        return config;
    }

    /**
     * 构建收入查询条件
     */
    private RevenueReportQueryDTO buildRevenueQuery(Map<String, Object> params) {
        RevenueReportQueryDTO query = new RevenueReportQueryDTO();
        if (params.get("startDate") != null) {
            query.setStartDate((LocalDate) params.get("startDate"));
        }
        if (params.get("endDate") != null) {
            query.setEndDate((LocalDate) params.get("endDate"));
        }
        if (params.get("campusId") != null) {
            query.setCampusId((Long) params.get("campusId"));
        }
        if (params.get("courseTypeId") != null) {
            query.setCourseTypeId((Long) params.get("courseTypeId"));
        }
        if (params.get("paymentMethod") != null) {
            query.setPaymentMethod((String) params.get("paymentMethod"));
        }
        query.setTimeDimension("day"); // 默认按天统计
        return query;
    }

    /**
     * 构建课消查询条件
     */
    private ClassHourReportQueryDTO buildClassHourQuery(Map<String, Object> params) {
        ClassHourReportQueryDTO query = new ClassHourReportQueryDTO();
        if (params.get("startDate") != null) {
            query.setStartDate((LocalDate) params.get("startDate"));
        }
        if (params.get("endDate") != null) {
            query.setEndDate((LocalDate) params.get("endDate"));
        }
        if (params.get("campusId") != null) {
            query.setCampusId((Long) params.get("campusId"));
        }
        if (params.get("classId") != null) {
            query.setClassId((Long) params.get("classId"));
        }
        if (params.get("courseId") != null) {
            query.setCourseId((Long) params.get("courseId"));
        }
        if (params.get("teacherId") != null) {
            query.setTeacherId((Long) params.get("teacherId"));
        }
        return query;
    }

    /**
     * 构建应收账款查询条件
     */
    private AccountsReceivableQueryDTO buildAccountsReceivableQuery(Map<String, Object> params) {
        AccountsReceivableQueryDTO query = new AccountsReceivableQueryDTO();
        if (params.get("campusId") != null) {
            query.setCampusId((Long) params.get("campusId"));
        }
        if (params.get("studentName") != null) {
            query.setStudentName((String) params.get("studentName"));
        }
        if (params.get("agingCategory") != null) {
            query.setAgingRange((String) params.get("agingCategory"));
        }
        query.setPageNum(1);
        query.setPageSize(10000); // 导出时获取所有数据
        return query;
    }

    /**
     * 转换为收入导出DTO
     */
    private RevenueReportExportDTO convertToRevenueExportDTO(RevenueReportVO vo) {
        RevenueReportExportDTO dto = new RevenueReportExportDTO();
        dto.setDimension(vo.getDimension());
        dto.setDimensionValue(vo.getDimensionValue());
        dto.setTotalRevenue(vo.getTotalRevenue());
        dto.setNewContractRevenue(vo.getNewContractRevenue());
        dto.setRenewalRevenue(vo.getRenewalRevenue());
        dto.setRefundAmount(vo.getRefundAmount());
        dto.setNetRevenue(vo.getNetRevenue());
        dto.setContractCount(vo.getContractCount());
        dto.setStudentCount(vo.getStudentCount());
        dto.setAvgOrderValue(vo.getAvgOrderValue());
        return dto;
    }

    /**
     * 转换为课消导出DTO
     */
    private ClassHourReportExportDTO convertToClassHourExportDTO(ClassHourReportVO vo) {
        ClassHourReportExportDTO dto = new ClassHourReportExportDTO();
        dto.setDimension(vo.getDimension());
        dto.setDimensionValue(vo.getDimensionValue());
        dto.setTotalHours(vo.getTotalHours());
        dto.setUsedHours(vo.getUsedHours());
        dto.setRemainingHours(vo.getRemainingHours());
        dto.setConsumptionRate(vo.getConsumptionRate());
        dto.setConsumptionAmount(vo.getConsumptionAmount());
        dto.setStudentCount(vo.getStudentCount());
        dto.setClassCount(vo.getClassCount());
        return dto;
    }

    /**
     * 转换为应收账款导出DTO
     */
    private AccountsReceivableExportDTO convertToAccountsReceivableExportDTO(AccountsReceivableVO vo) {
        AccountsReceivableExportDTO dto = new AccountsReceivableExportDTO();
        dto.setStudentName(vo.getStudentName());
        dto.setPhone(vo.getPhone());
        dto.setCampusName(vo.getCampusName());
        dto.setContractNo(vo.getContractNo());
        dto.setContractAmount(vo.getContractAmount());
        dto.setPaidAmount(vo.getPaidAmount());
        dto.setReceivableAmount(vo.getReceivableAmount());
        dto.setAgingDays(vo.getAgingDays());
        dto.setAgingCategory(vo.getAgingCategory());
        dto.setContractDate(vo.getContractDate() != null ? vo.getContractDate().format(DATE_FORMATTER) : "");
        dto.setLastPaymentDate(vo.getLastPaymentDate() != null ? vo.getLastPaymentDate().format(DATE_FORMATTER) : "");
        return dto;
    }

    /**
     * 生成文件名
     */
    private String generateFileName(String prefix, ExportFormat format) {
        String timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return prefix + "_" + timestamp;
    }

    /**
     * 格式化金额
     */
    private String formatMoney(BigDecimal amount) {
        return amount != null ? amount.toString() : "0.00";
    }

    /**
     * 格式化数字
     */
    private String formatNumber(BigDecimal number) {
        return number != null ? number.toString() : "0";
    }

    /**
     * 格式化百分比
     */
    private String formatPercent(BigDecimal percent) {
        return percent != null ? percent.toString() + "%" : "0%";
    }
}
