package com.edu.common.report;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Excel 导出配置
 */
@Data
public class ExcelExportConfig {

    /**
     * 工作表配置列表
     */
    private List<SheetConfig> sheets = new ArrayList<>();

    /**
     * 是否自动列宽
     */
    private Boolean autoColumnWidth = true;

    /**
     * 是否包含图表
     */
    private Boolean includeCharts = false;

    /**
     * 添加工作表配置
     */
    public ExcelExportConfig addSheet(SheetConfig sheet) {
        this.sheets.add(sheet);
        return this;
    }

    /**
     * 工作表配置
     */
    @Data
    public static class SheetConfig {
        /**
         * 工作表名称
         */
        private String sheetName;

        /**
         * 数据类型
         */
        private Class<?> dataClass;

        /**
         * 数据列表
         */
        private List<?> data;

        /**
         * 表头样式配置
         */
        private StyleConfig headerStyle;

        /**
         * 数据行样式配置
         */
        private StyleConfig dataStyle;

        public SheetConfig(String sheetName, Class<?> dataClass, List<?> data) {
            this.sheetName = sheetName;
            this.dataClass = dataClass;
            this.data = data;
        }
    }

    /**
     * 样式配置
     */
    @Data
    public static class StyleConfig {
        /**
         * 字体大小
         */
        private Short fontSize;

        /**
         * 是否加粗
         */
        private Boolean bold;

        /**
         * 背景颜色（RGB）
         */
        private String backgroundColor;

        /**
         * 字体颜色（RGB）
         */
        private String fontColor;
    }
}
