package com.edu.framework.file;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.edu.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 本地文件存储服务实现
 */
@Slf4j
@Service
public class LocalFileService implements FileService {

    @Value("${file.local.base-path:/data/files}")
    private String basePath;

    @Value("${file.local.base-url:/files}")
    private String baseUrl;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    @Override
    public String upload(MultipartFile file, String path) {
        try {
            return upload(file.getInputStream(), path, file.getOriginalFilename());
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public String upload(InputStream inputStream, String path, String fileName) {
        try {
            // 生成存储路径
            String datePath = LocalDate.now().format(DATE_FORMATTER);
            String extension = FileUtil.extName(fileName);
            String newFileName = IdUtil.fastSimpleUUID() + "." + extension;

            String relativePath = StrUtil.isBlank(path)
                    ? datePath + "/" + newFileName
                    : path + "/" + datePath + "/" + newFileName;

            String fullPath = basePath + "/" + relativePath;

            // 创建目录并保存文件
            FileUtil.mkdir(FileUtil.getParent(fullPath, 1));
            FileUtil.writeFromStream(inputStream, fullPath);

            log.info("文件上传成功: {}", fullPath);
            return baseUrl + "/" + relativePath;
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new BusinessException("文件上传失败: " + e.getMessage());
        } finally {
            IoUtil.close(inputStream);
        }
    }

    @Override
    public boolean delete(String fileUrl) {
        try {
            String relativePath = fileUrl.replace(baseUrl, "");
            String fullPath = basePath + relativePath;
            return FileUtil.del(fullPath);
        } catch (Exception e) {
            log.error("文件删除失败: {}", fileUrl, e);
            return false;
        }
    }

    @Override
    public InputStream getFile(String fileUrl) {
        try {
            String relativePath = fileUrl.replace(baseUrl, "");
            String fullPath = basePath + relativePath;
            return new FileInputStream(fullPath);
        } catch (FileNotFoundException e) {
            log.error("文件不存在: {}", fileUrl);
            throw new BusinessException("文件不存在");
        }
    }

    @Override
    public boolean exists(String fileUrl) {
        String relativePath = fileUrl.replace(baseUrl, "");
        String fullPath = basePath + relativePath;
        return FileUtil.exist(fullPath);
    }
}
