package com.edu.common.report;

import com.edu.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * 异步报表导出服务
 * 用于处理大数据量的报表导出
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncReportExportService {

    private final ReportExportService reportExportService;

    /**
     * 导出任务状态缓存
     */
    private final Map<String, ReportExportResult> taskCache = new ConcurrentHashMap<>();

    /**
     * 导出文件存储基础路径
     */
    private static final String EXPORT_BASE_PATH = "/data/exports";

    /**
     * 文件URL基础路径
     */
    private static final String EXPORT_BASE_URL = "/api/files/exports";

    /**
     * 异步导出 Excel
     *
     * @param fileName         文件名
     * @param config           Excel 配置
     * @param progressCallback 进度回调（可选）
     * @return 导出任务ID
     */
    @Async
    public CompletableFuture<ReportExportResult> exportExcelAsync(
            String fileName,
            ExcelExportConfig config,
            Consumer<Integer> progressCallback) {

        String taskId = generateTaskId();
        ReportExportResult result = ReportExportResult.async(taskId);
        taskCache.put(taskId, result);

        return CompletableFuture.supplyAsync(() -> {
            try {
                // 更新状态为处理中
                updateProgress(taskId, ExportStatus.PROCESSING, 10);
                if (progressCallback != null) {
                    progressCallback.accept(10);
                }

                // 生成 Excel 文件
                byte[] fileBytes = reportExportService.exportExcelToBytes(config);
                updateProgress(taskId, ExportStatus.PROCESSING, 70);
                if (progressCallback != null) {
                    progressCallback.accept(70);
                }

                // 保存文件
                String filePath = saveFile(fileName, ExportFormat.EXCEL, fileBytes);
                updateProgress(taskId, ExportStatus.PROCESSING, 90);
                if (progressCallback != null) {
                    progressCallback.accept(90);
                }

                // 构建下载URL
                String downloadUrl = buildDownloadUrl(filePath);

                // 更新为完成状态
                result.setStatus(ExportStatus.COMPLETED);
                result.setFileName(fileName + ExportFormat.EXCEL.getExtension());
                result.setFilePath(filePath);
                result.setDownloadUrl(downloadUrl);
                result.setFileSize((long) fileBytes.length);
                result.setProgress(100);
                taskCache.put(taskId, result);

                if (progressCallback != null) {
                    progressCallback.accept(100);
                }

                log.info("Async Excel export completed: taskId={}, file={}", taskId, fileName);
                return result;

            } catch (Exception e) {
                log.error("Async Excel export failed: taskId={}", taskId, e);
                result.setStatus(ExportStatus.FAILED);
                result.setErrorMessage(e.getMessage());
                result.setProgress(0);
                taskCache.put(taskId, result);
                throw new BusinessException("异步导出失败: " + e.getMessage());
            }
        });
    }

    /**
     * 异步导出 PDF
     *
     * @param fileName         文件名
     * @param config           PDF 配置
     * @param progressCallback 进度回调（可选）
     * @return 导出任务ID
     */
    @Async
    public CompletableFuture<ReportExportResult> exportPdfAsync(
            String fileName,
            PdfExportConfig config,
            Consumer<Integer> progressCallback) {

        String taskId = generateTaskId();
        ReportExportResult result = ReportExportResult.async(taskId);
        taskCache.put(taskId, result);

        return CompletableFuture.supplyAsync(() -> {
            try {
                // 更新状态为处理中
                updateProgress(taskId, ExportStatus.PROCESSING, 10);
                if (progressCallback != null) {
                    progressCallback.accept(10);
                }

                // 生成 PDF 文件
                byte[] fileBytes = reportExportService.exportPdfToBytes(config);
                updateProgress(taskId, ExportStatus.PROCESSING, 70);
                if (progressCallback != null) {
                    progressCallback.accept(70);
                }

                // 保存文件
                String filePath = saveFile(fileName, ExportFormat.PDF, fileBytes);
                updateProgress(taskId, ExportStatus.PROCESSING, 90);
                if (progressCallback != null) {
                    progressCallback.accept(90);
                }

                // 构建下载URL
                String downloadUrl = buildDownloadUrl(filePath);

                // 更新为完成状态
                result.setStatus(ExportStatus.COMPLETED);
                result.setFileName(fileName + ExportFormat.PDF.getExtension());
                result.setFilePath(filePath);
                result.setDownloadUrl(downloadUrl);
                result.setFileSize((long) fileBytes.length);
                result.setProgress(100);
                taskCache.put(taskId, result);

                if (progressCallback != null) {
                    progressCallback.accept(100);
                }

                log.info("Async PDF export completed: taskId={}, file={}", taskId, fileName);
                return result;

            } catch (Exception e) {
                log.error("Async PDF export failed: taskId={}", taskId, e);
                result.setStatus(ExportStatus.FAILED);
                result.setErrorMessage(e.getMessage());
                result.setProgress(0);
                taskCache.put(taskId, result);
                throw new BusinessException("异步导出失败: " + e.getMessage());
            }
        });
    }

    /**
     * 查询导出任务状态
     *
     * @param taskId 任务ID
     * @return 导出结果
     */
    public ReportExportResult getTaskStatus(String taskId) {
        ReportExportResult result = taskCache.get(taskId);
        if (result == null) {
            throw new BusinessException("导出任务不存在: " + taskId);
        }
        return result;
    }

    /**
     * 取消导出任务
     *
     * @param taskId 任务ID
     */
    public void cancelTask(String taskId) {
        ReportExportResult result = taskCache.get(taskId);
        if (result != null && result.getStatus() != ExportStatus.COMPLETED) {
            result.setStatus(ExportStatus.FAILED);
            result.setErrorMessage("任务已取消");
            taskCache.put(taskId, result);
            log.info("Export task cancelled: taskId={}", taskId);
        }
    }

    /**
     * 清理已完成的任务（定期调用）
     */
    public void cleanupCompletedTasks() {
        taskCache.entrySet().removeIf(entry -> {
            ReportExportResult result = entry.getValue();
            return result.getStatus() == ExportStatus.COMPLETED ||
                   result.getStatus() == ExportStatus.FAILED;
        });
        log.info("Cleaned up completed export tasks");
    }

    /**
     * 生成任务ID
     */
    private String generateTaskId() {
        return "EXPORT_" + UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

    /**
     * 更新任务进度
     */
    private void updateProgress(String taskId, ExportStatus status, Integer progress) {
        ReportExportResult result = taskCache.get(taskId);
        if (result != null) {
            result.setStatus(status);
            result.setProgress(progress);
            taskCache.put(taskId, result);
        }
    }

    /**
     * 保存文件到本地
     */
    private String saveFile(String fileName, ExportFormat format, byte[] fileBytes) throws IOException {
        // 构建文件路径：exports/yyyy/MM/dd/filename.ext
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String fullFileName = fileName + format.getExtension();
        String relativePath = datePath + "/" + fullFileName;

        Path basePath = Paths.get(EXPORT_BASE_PATH);
        Path filePath = basePath.resolve(relativePath);

        // 创建目录
        Files.createDirectories(filePath.getParent());

        // 写入文件
        try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
            fos.write(fileBytes);
        }

        log.info("Export file saved: {}", filePath);
        return relativePath;
    }

    /**
     * 构建下载URL
     */
    private String buildDownloadUrl(String filePath) {
        return EXPORT_BASE_URL + "/" + filePath;
    }
}
