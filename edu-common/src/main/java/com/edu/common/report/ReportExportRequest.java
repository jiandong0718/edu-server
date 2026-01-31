package com.edu.common.report;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 报表导出请求
 */
@Data
public class ReportExportRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 报表类型
     */
    private String reportType;

    /**
     * 导出格式
     */
    private ExportFormat format;

    /**
     * 报表标题
     */
    private String title;

    /**
     * 查询参数
     */
    private Map<String, Object> params;

    /**
     * 是否异步导出
     */
    private Boolean async = false;

    /**
     * 是否包含图表
     */
    private Boolean includeCharts = false;
}
