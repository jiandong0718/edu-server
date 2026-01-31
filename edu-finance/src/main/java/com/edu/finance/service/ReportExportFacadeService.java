package com.edu.finance.service;

import com.edu.common.report.ReportExportRequest;
import com.edu.common.report.ReportExportResult;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 报表导出门面服务
 * 统一处理各类报表的导出逻辑
 */
public interface ReportExportFacadeService {

    /**
     * 导出收入报表（同步）
     *
     * @param request  导出请求
     * @param response HTTP响应
     */
    void exportRevenueReport(ReportExportRequest request, HttpServletResponse response);

    /**
     * 导出收入报表（异步）
     *
     * @param request 导出请求
     * @return 导出结果（包含任务ID）
     */
    ReportExportResult exportRevenueReportAsync(ReportExportRequest request);

    /**
     * 导出课消报表（同步）
     *
     * @param request  导出请求
     * @param response HTTP响应
     */
    void exportClassHourReport(ReportExportRequest request, HttpServletResponse response);

    /**
     * 导出课消报表（异步）
     *
     * @param request 导出请求
     * @return 导出结果（包含任务ID）
     */
    ReportExportResult exportClassHourReportAsync(ReportExportRequest request);

    /**
     * 导出应收账款报表（同步）
     *
     * @param request  导出请求
     * @param response HTTP响应
     */
    void exportAccountsReceivableReport(ReportExportRequest request, HttpServletResponse response);

    /**
     * 导出应收账款报表（异步）
     *
     * @param request 导出请求
     * @return 导出结果（包含任务ID）
     */
    ReportExportResult exportAccountsReceivableReportAsync(ReportExportRequest request);

    /**
     * 导出利润分析报表（同步）
     *
     * @param request  导出请求
     * @param response HTTP响应
     */
    void exportProfitAnalysisReport(ReportExportRequest request, HttpServletResponse response);

    /**
     * 导出利润分析报表（异步）
     *
     * @param request 导出请求
     * @return 导出结果（包含任务ID）
     */
    ReportExportResult exportProfitAnalysisReportAsync(ReportExportRequest request);

    /**
     * 查询导出任务状态
     *
     * @param taskId 任务ID
     * @return 导出结果
     */
    ReportExportResult getExportTaskStatus(String taskId);

    /**
     * 取消导出任务
     *
     * @param taskId 任务ID
     */
    void cancelExportTask(String taskId);
}
