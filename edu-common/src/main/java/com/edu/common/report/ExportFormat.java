package com.edu.common.report;

import lombok.Getter;

/**
 * 报表导出格式枚举
 */
@Getter
public enum ExportFormat {

    /**
     * Excel 格式
     */
    EXCEL("excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ".xlsx"),

    /**
     * PDF 格式
     */
    PDF("pdf", "application/pdf", ".pdf");

    /**
     * 格式代码
     */
    private final String code;

    /**
     * MIME 类型
     */
    private final String mimeType;

    /**
     * 文件扩展名
     */
    private final String extension;

    ExportFormat(String code, String mimeType, String extension) {
        this.code = code;
        this.mimeType = mimeType;
        this.extension = extension;
    }

    /**
     * 根据代码获取格式
     */
    public static ExportFormat fromCode(String code) {
        for (ExportFormat format : values()) {
            if (format.code.equalsIgnoreCase(code)) {
                return format;
            }
        }
        throw new IllegalArgumentException("Unknown export format: " + code);
    }
}
