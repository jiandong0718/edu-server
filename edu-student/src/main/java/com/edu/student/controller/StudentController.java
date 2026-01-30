package com.edu.student.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.R;
import com.edu.student.domain.entity.Student;
import com.edu.student.domain.entity.StudentContact;
import com.edu.student.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
}
