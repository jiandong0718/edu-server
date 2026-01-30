package com.edu.framework.file;

import lombok.Data;

/**
 * 文件上传结果
 */
@Data
public class FileUploadResult {

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 原始文件名
     */
    private String originalName;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 访问URL
     */
    private String url;

    /**
     * 文件大小（字节）
     */
    private Long size;

    /**
     * 文件类型
     */
    private String contentType;

    /**
     * 文件扩展名
     */
    private String extension;
}
