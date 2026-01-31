package com.edu.student.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.R;
import com.edu.student.domain.dto.StudentImportResultDTO;
import com.edu.student.domain.entity.Student;
import com.edu.student.domain.entity.StudentContact;
import com.edu.student.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 学员管理控制器
 */
@Tag(name = "学员管理")
@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @Operation(summary = "分页查询学员列表")
    @GetMapping("/page")
    public R<Page<Student>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            Student query) {
        Page<Student> page = new Page<>(pageNum, pageSize);
        studentService.pageList(page, query);
        return R.ok(page);
    }

    @Operation(summary = "获取学员详情")
    @GetMapping("/{id}")
    public R<Student> getById(@PathVariable Long id) {
        return R.ok(studentService.getDetail(id));
    }

    @Operation(summary = "新增学员")
    @PostMapping
    public R<Boolean> add(@RequestBody Student student) {
        return R.ok(studentService.addStudent(student));
    }

    @Operation(summary = "修改学员")
    @PutMapping
    public R<Boolean> update(@RequestBody Student student) {
        return R.ok(studentService.updateStudent(student));
    }

    @Operation(summary = "删除学员")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(studentService.deleteStudent(id));
    }

    @Operation(summary = "批量删除学员")
    @DeleteMapping("/batch")
    public R<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        return R.ok(studentService.deleteStudents(ids));
    }

    @Operation(summary = "更新学员状态")
    @PutMapping("/{id}/status")
    public R<Boolean> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return R.ok(studentService.updateStatus(id, status));
    }

    // ==================== 联系人管理 ====================

    @Operation(summary = "获取学员联系人列表")
    @GetMapping("/{studentId}/contacts")
    public R<List<StudentContact>> getContacts(@PathVariable Long studentId) {
        return R.ok(studentService.getContacts(studentId));
    }

    @Operation(summary = "保存学员联系人")
    @PostMapping("/{studentId}/contacts")
    public R<Boolean> saveContact(@PathVariable Long studentId, @RequestBody StudentContact contact) {
        contact.setStudentId(studentId);
        return R.ok(studentService.saveContact(contact));
    }

    @Operation(summary = "删除学员联系人")
    @DeleteMapping("/contacts/{contactId}")
    public R<Boolean> deleteContact(@PathVariable Long contactId) {
        return R.ok(studentService.deleteContact(contactId));
    }

    // ==================== 标签管理 ====================

    @Operation(summary = "设置学员标签")
    @PutMapping("/{studentId}/tags")
    public R<Boolean> setTags(@PathVariable Long studentId, @RequestBody List<Long> tagIds) {
        return R.ok(studentService.setTags(studentId, tagIds));
    }

    // ==================== 导入导出 ====================

    @Operation(summary = "导出学员数据")
    @GetMapping("/export")
    public ResponseEntity<byte[]> export(Student query) {
        byte[] data = studentService.exportToExcel(query);
        String fileName = "students_" + System.currentTimeMillis() + ".xlsx";
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }

    @Operation(summary = "批量导入学员数据")
    @PostMapping("/import")
    public R<Boolean> importData(@RequestParam("file") MultipartFile file) throws IOException {
        byte[] fileData = file.getBytes();
        return R.ok(studentService.importFromExcel(fileData));
    }

    @Operation(summary = "批量导入学员数据（增强版）")
    @PostMapping("/batch-import")
    public R<StudentImportResultDTO> batchImport(@RequestParam("file") MultipartFile file) throws IOException {
        byte[] fileData = file.getBytes();
        StudentImportResultDTO result = studentService.batchImportStudents(fileData);
        return R.ok(result);
    }

    @Operation(summary = "下载导入模板")
    @GetMapping("/import-template")
    public ResponseEntity<byte[]> downloadTemplate() {
        byte[] data = studentService.downloadImportTemplate();
        String fileName = "student_import_template.xlsx";
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }
}
