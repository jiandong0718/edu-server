package com.edu.teaching.controller;

import com.edu.common.core.R;
import com.edu.teaching.domain.entity.TeacherCertificate;
import com.edu.teaching.service.TeacherCertificateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 教师资质证书管理控制器
 */
@Tag(name = "教师资质证书管理")
@RestController
@RequestMapping("/teaching/teacher/certificate")
@RequiredArgsConstructor
public class TeacherCertificateController {

    private final TeacherCertificateService certificateService;

    @Operation(summary = "获取教师证书列表")
    @GetMapping("/list")
    public R<List<TeacherCertificate>> list(@RequestParam Long teacherId) {
        return R.ok(certificateService.getByTeacherId(teacherId));
    }

    @Operation(summary = "获取证书详情")
    @GetMapping("/{id}")
    public R<TeacherCertificate> getById(@PathVariable Long id) {
        return R.ok(certificateService.getById(id));
    }

    @Operation(summary = "新增证书")
    @PostMapping
    public R<Boolean> add(@RequestBody TeacherCertificate certificate) {
        return R.ok(certificateService.save(certificate));
    }

    @Operation(summary = "修改证书")
    @PutMapping
    public R<Boolean> update(@RequestBody TeacherCertificate certificate) {
        return R.ok(certificateService.updateById(certificate));
    }

    @Operation(summary = "删除证书")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(certificateService.removeById(id));
    }

    @Operation(summary = "上传证书图片")
    @PostMapping("/upload")
    public R<String> upload(@RequestParam("file") MultipartFile file) {
        // TODO: 实现文件上传逻辑，返回文件URL
        // 这里需要集成文件上传服务（如OSS、本地存储等）
        // 暂时返回模拟URL
        String fileName = file.getOriginalFilename();
        String fileUrl = "/uploads/certificates/" + System.currentTimeMillis() + "_" + fileName;
        return R.ok(fileUrl);
    }
}
