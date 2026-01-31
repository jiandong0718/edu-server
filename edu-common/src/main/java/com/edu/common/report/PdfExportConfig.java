package com.edu.common.report;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * PDF 导出配置
 */
@Data
public class PdfExportConfig {

    /**
     * 文档标题
     */
    private String title;

    /**
     * 页眉文本
     */
    private String headerText;

    /**
     * 页脚文本
     */
    private String footerText;

    /**
     * 是否显示页码
     */
    private Boolean showPageNumber = true;

    /**
     * 表格配置列表
     */
    private List<TableConfig> tables = new ArrayList<>();

    /**
     * 是否包含图表
     */
    private Boolean includeCharts = false;

    /**
     * 添加表格配置
     */
    public PdfExportConfig addTable(TableConfig table) {
        this.tables.add(table);
        return this;
    }

    /**
     * 表格配置
     */
    @Data
    public static class TableConfig {
        /**
         * 表格标题
         */
        private String title;

        /**
         * 列标题
         */
        private List<String> headers;

        /**
         * 表格数据（每行是一个字符串列表）
         */
        private List<List<String>> data;

        /**
         * 列宽比例
         */
        private float[] columnWidths;

        public TableConfig(String title, List<String> headers, List<List<String>> data) {
            this.title = title;
            this.headers = headers;
            this.data = data;
        }
    }
}
