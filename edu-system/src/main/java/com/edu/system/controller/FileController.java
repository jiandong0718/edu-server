package com.edu.system.controller;

import com.edu.common.core.Result;
import com.edu.framework.file.FileStorageService;
import com.edu.framework.file.FileUploadResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/system/file")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    /**
     * 上传单个文件
     */
    @PostMapping("/upload")
    public Result<FileUploadResult> upload(@RequestParam("file") MultipartFile file,
                                           @RequestParam(value = "directory", required = false) String directory) {
        FileUploadResult result = fileStorageService.upload(file, directory);
        return Result.success(result);
    }

    /**
     * 上传多个文件
     */
    @PostMapping("/upload/batch")
    public Result<List<FileUploadResult>> uploadBatch(@RequestParam("files") MultipartFile[] files,
                                                      @RequestParam(value = "directory", required = false) String directory) {
        List<FileUploadResult> results = new ArrayList<>();
        for (MultipartFile file : files) {
            results.add(fileStorageService.upload(file, directory));
        }
        return Result.success(results);
    }

    /**
     * 上传头像
     */
    @PostMapping("/upload/avatar")
    public Result<FileUploadResult> uploadAvatar(@RequestParam("file") MultipartFile file) {
        FileUploadResult result = fileStorageService.upload(file, "avatar");
        return Result.success(result);
    }

    /**
     * 删除文件
     */
    @DeleteMapping
    public Result<Void> delete(@RequestParam("filePath") String filePath) {
        fileStorageService.delete(filePath);
        return Result.success();
    }
}
