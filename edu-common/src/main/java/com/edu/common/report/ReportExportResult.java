package com.edu.common.report;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 报表导出结果
 */
@Data
public class ReportExportResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务ID（异步导出时使用）
     */
    private String taskId;

    /**
     * 导出状态
     */
    private ExportStatus status;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 下载URL
     */
    private String downloadUrl;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 导出进度（0-100）
     */
    private Integer progress;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 完成时间
     */
    private LocalDateTime completeTime;

    /**
     * 创建成功的结果
     */
    public static ReportExportResult success(String fileName, String filePath, String downloadUrl, Long fileSize) {
        ReportExportResult result = new ReportExportResult();
        result.setStatus(ExportStatus.COMPLETED);
        result.setFileName(fileName);
        result.setFilePath(filePath);
        result.setDownloadUrl(downloadUrl);
        result.setFileSize(fileSize);
        result.setProgress(100);
        result.setCompleteTime(LocalDateTime.now());
        return result;
    }

    /**
     * 创建异步任务结果
     */
    public static ReportExportResult async(String taskId) {
        ReportExportResult result = new ReportExportResult();
        result.setTaskId(taskId);
        result.setStatus(ExportStatus.PENDING);
        result.setProgress(0);
        result.setCreateTime(LocalDateTime.now());
        return result;
    }

    /**
     * 创建失败的结果
     */
    public static ReportExportResult failed(String errorMessage) {
        ReportExportResult result = new ReportExportResult();
        result.setStatus(ExportStatus.FAILED);
        result.setErrorMessage(errorMessage);
        result.setProgress(0);
        return result;
    }
}
