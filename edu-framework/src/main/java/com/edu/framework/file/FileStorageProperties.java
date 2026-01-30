package com.edu.framework.file;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 文件存储配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "file")
public class FileStorageProperties {

    /**
     * 存储类型：local-本地存储，oss-阿里云OSS，cos-腾讯云COS
     */
    private String storageType = "local";

    /**
     * 本地存储配置
     */
    private LocalConfig local = new LocalConfig();

    /**
     * OSS 配置
     */
    private OssConfig oss = new OssConfig();

    /**
     * 允许的文件类型
     */
    private String[] allowedTypes = {"jpg", "jpeg", "png", "gif", "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "zip", "rar"};

    /**
     * 最大文件大小（MB）
     */
    private int maxSize = 50;

    @Data
    public static class LocalConfig {
        /**
         * 本地存储基础路径
         */
        private String basePath = "/data/files";

        /**
         * 访问URL前缀
         */
        private String baseUrl = "/files";
    }

    @Data
    public static class OssConfig {
        /**
         * 是否启用
         */
        private boolean enabled = false;

        /**
         * 端点
         */
        private String endpoint;

        /**
         * Access Key
         */
        private String accessKey;

        /**
         * Secret Key
         */
        private String secretKey;

        /**
         * 存储桶名称
         */
        private String bucket;

        /**
         * 访问域名
         */
        private String domain;
    }
}
