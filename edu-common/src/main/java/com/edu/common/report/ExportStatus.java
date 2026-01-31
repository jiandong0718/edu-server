package com.edu.common.report;

import lombok.Getter;

/**
 * 导出状态枚举
 */
@Getter
public enum ExportStatus {

    /**
     * 等待中
     */
    PENDING("pending", "等待中"),

    /**
     * 处理中
     */
    PROCESSING("processing", "处理中"),

    /**
     * 已完成
     */
    COMPLETED("completed", "已完成"),

    /**
     * 失败
     */
    FAILED("failed", "失败");

    /**
     * 状态代码
     */
    private final String code;

    /**
     * 状态描述
     */
    private final String description;

    ExportStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
