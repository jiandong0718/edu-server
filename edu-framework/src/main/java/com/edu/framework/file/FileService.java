package com.edu.framework.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * 文件服务接口
 * 定义文件上传、下载、删除等操作
 */
public interface FileService {

    /**
     * 上传文件
     *
     * @param file 文件
     * @param path 存储路径（相对路径）
     * @return 文件访问URL
     */
    String upload(MultipartFile file, String path);

    /**
     * 上传文件
     *
     * @param inputStream 文件输入流
     * @param path        存储路径（相对路径）
     * @param fileName    文件名
     * @return 文件访问URL
     */
    String upload(InputStream inputStream, String path, String fileName);

    /**
     * 删除文件
     *
     * @param fileUrl 文件URL
     * @return 是否删除成功
     */
    boolean delete(String fileUrl);

    /**
     * 获取文件
     *
     * @param fileUrl 文件URL
     * @return 文件输入流
     */
    InputStream getFile(String fileUrl);

    /**
     * 判断文件是否存在
     *
     * @param fileUrl 文件URL
     * @return 是否存在
     */
    boolean exists(String fileUrl);
}
