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

    // Getter methods (Lombok @Data not working with Java 23)
    public String getStorageType() {
        return storageType;
    }

    public LocalConfig getLocal() {
        return local;
    }

    public OssConfig getOss() {
        return oss;
    }

    public String[] getAllowedTypes() {
        return allowedTypes;
    }

    public int getMaxSize() {
        return maxSize;
    }

    // Setter methods
    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }

    public void setLocal(LocalConfig local) {
        this.local = local;
    }

    public void setOss(OssConfig oss) {
        this.oss = oss;
    }

    public void setAllowedTypes(String[] allowedTypes) {
        this.allowedTypes = allowedTypes;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

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
    
        // Getter and Setter methods (Lombok @Data not working with Java 23)
        public String getBasePath() {
            return basePath;
        }

        public void setBasePath(String basePath) {
            this.basePath = basePath;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

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
    
        // Getter and Setter methods (Lombok @Data not working with Java 23)
        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getAccessKey() {
            return accessKey;
        }

        public void setAccessKey(String accessKey) {
            this.accessKey = accessKey;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public String getBucket() {
            return bucket;
        }

        public void setBucket(String bucket) {
            this.bucket = bucket;
        }

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

    }
}
