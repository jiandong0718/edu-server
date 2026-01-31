package com.edu.teaching.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.R;
import com.edu.teaching.domain.dto.TeacherCertificateDTO;
import com.edu.teaching.domain.entity.TeacherCertificate;
import com.edu.teaching.domain.vo.TeacherCertificateVO;
import com.edu.teaching.service.TeacherCertificateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
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

    @Operation(summary = "分页查询证书列表")
    @GetMapping("/page")
    public R<Page<TeacherCertificateVO>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "教师ID") @RequestParam(required = false) Long teacherId,
            @Parameter(description = "证书类型") @RequestParam(required = false) String certType,
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId) {
        return R.ok(certificateService.pageQuery(pageNum, pageSize, teacherId, certType, campusId));
    }

    @Operation(summary = "获取教师证书列表")
    @GetMapping("/list")
    public R<List<TeacherCertificate>> list(@Parameter(description = "教师ID") @RequestParam Long teacherId) {
        return R.ok(certificateService.getByTeacherId(teacherId));
    }

    @Operation(summary = "获取证书详情")
    @GetMapping("/{id}")
    public R<TeacherCertificateVO> getById(@Parameter(description = "证书ID") @PathVariable Long id) {
        return R.ok(certificateService.getDetailById(id));
    }

    @Operation(summary = "新增证书")
    @PostMapping
    public R<Long> add(@Validated @RequestBody TeacherCertificateDTO dto) {
        return R.ok(certificateService.addCertificate(dto));
    }

    @Operation(summary = "修改证书")
    @PutMapping
    public R<Boolean> update(@Validated @RequestBody TeacherCertificateDTO dto) {
        return R.ok(certificateService.updateCertificate(dto));
    }

    @Operation(summary = "删除证书")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@Parameter(description = "证书ID") @PathVariable Long id) {
        return R.ok(certificateService.deleteCertificate(id));
    }

    @Operation(summary = "批量删除证书")
    @DeleteMapping("/batch")
    public R<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        return R.ok(certificateService.deleteBatch(ids));
    }

    @Operation(summary = "上传证书文件")
    @PostMapping("/upload")
    public R<String> upload(
            @Parameter(description = "证书文件（支持JPG、PNG、PDF格式，最大10MB）")
            @RequestParam("file") MultipartFile file) {
        String fileUrl = certificateService.uploadCertificateFile(file);
        return R.ok(fileUrl);
    }

    @Operation(summary = "检查证书是否过期")
    @GetMapping("/{id}/expired")
    public R<Boolean> checkExpired(@Parameter(description = "证书ID") @PathVariable Long id) {
        return R.ok(certificateService.checkExpired(id));
    }

    @Operation(summary = "获取即将过期的证书列表（30天内）")
    @GetMapping("/expiring")
    public R<List<TeacherCertificateVO>> getExpiringCertificates(
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId) {
        return R.ok(certificateService.getExpiringCertificates(campusId));
    }

    @Operation(summary = "批量上传证书文件")
    @PostMapping("/upload/batch")
    public R<List<String>> uploadBatch(
            @Parameter(description = "证书文件列表")
            @RequestParam("files") MultipartFile[] files) {
        List<String> fileUrls = new java.util.ArrayList<>();
        for (MultipartFile file : files) {
            String fileUrl = certificateService.uploadCertificateFile(file);
            fileUrls.add(fileUrl);
        }
        return R.ok(fileUrls);
    }
}
