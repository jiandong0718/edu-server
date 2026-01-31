package com.edu.framework.file;

import com.edu.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;

/**
 * 文件存储服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FileStorageService.class);

    private final FileStorageProperties properties;

    /**
     * 上传文件
     */
    public FileUploadResult upload(MultipartFile file) {
        return upload(file, null);
    }

    /**
     * 上传文件到指定目录
     */
    public FileUploadResult upload(MultipartFile file, String directory) {
        // 验证文件
        validateFile(file);

        // 获取文件信息
        String originalName = file.getOriginalFilename();
        String extension = getExtension(originalName);
        String fileName = generateFileName(extension);

        // 构建存储路径
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String relativePath = directory != null
                ? directory + "/" + datePath + "/" + fileName
                : datePath + "/" + fileName;

        // 根据存储类型选择存储方式
        if ("oss".equals(properties.getStorageType()) && properties.getOss().isEnabled()) {
            return uploadToOss(file, relativePath, originalName, extension);
        } else {
            return uploadToLocal(file, relativePath, originalName, extension);
        }
    }

    /**
     * 上传到本地存储
     */
    private FileUploadResult uploadToLocal(MultipartFile file, String relativePath,
                                           String originalName, String extension) {
        try {
            Path basePath = Paths.get(properties.getLocal().getBasePath());
            Path filePath = basePath.resolve(relativePath);

            // 创建目录
            Files.createDirectories(filePath.getParent());

            // 保存文件
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }

            // 构建结果
            FileUploadResult result = new FileUploadResult();
            result.setFileName(filePath.getFileName().toString());
            result.setOriginalName(originalName);
            result.setFilePath(relativePath);
            result.setUrl(properties.getLocal().getBaseUrl() + "/" + relativePath);
            result.setSize(file.getSize());
            result.setContentType(file.getContentType());
            result.setExtension(extension);

            log.info("File uploaded to local: {}", filePath);
            return result;

        } catch (IOException e) {
            log.error("Failed to upload file to local storage", e);
            throw new BusinessException("文件上传失败");
        }
    }

    /**
     * 上传到 OSS
     */
    private FileUploadResult uploadToOss(MultipartFile file, String relativePath,
                                         String originalName, String extension) {
        // TODO: 集成阿里云 OSS SDK
        // OSSClient ossClient = new OSSClient(endpoint, accessKey, secretKey);
        // ossClient.putObject(bucket, relativePath, file.getInputStream());

        log.info("OSS upload not implemented yet, falling back to local storage");
        return uploadToLocal(file, relativePath, originalName, extension);
    }

    /**
     * 删除文件
     */
    public boolean delete(String filePath) {
        try {
            if ("oss".equals(properties.getStorageType()) && properties.getOss().isEnabled()) {
                // TODO: 从 OSS 删除
                return true;
            } else {
                Path path = Paths.get(properties.getLocal().getBasePath(), filePath);
                return Files.deleteIfExists(path);
            }
        } catch (IOException e) {
            log.error("Failed to delete file: ", filePath, e);
            return false;
        }
    }

    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择要上传的文件");
        }

        // 检查文件大小
        long maxSizeBytes = properties.getMaxSize() * 1024L * 1024L;
        if (file.getSize() > maxSizeBytes) {
            throw new BusinessException("文件大小不能超过 " + properties.getMaxSize() + "MB");
        }

        // 检查文件类型
        String extension = getExtension(file.getOriginalFilename());
        if (!isAllowedType(extension)) {
            throw new BusinessException("不支持的文件类型: " + extension);
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 检查是否允许的文件类型
     */
    private boolean isAllowedType(String extension) {
        return Arrays.asList(properties.getAllowedTypes()).contains(extension.toLowerCase());
    }

    /**
     * 生成文件名
     */
    private String generateFileName(String extension) {
        return UUID.randomUUID().toString().replace("-", "") + "." + extension;
    }
}
