package com.edu.teaching.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.R;
import com.edu.teaching.domain.dto.BatchClassGraduationDTO;
import com.edu.teaching.domain.dto.BatchClassPromotionDTO;
import com.edu.teaching.domain.dto.ClassGraduationDTO;
import com.edu.teaching.domain.dto.ClassPromotionDTO;
import com.edu.teaching.domain.entity.TeachClass;
import com.edu.teaching.domain.vo.BatchClassGraduationResultVO;
import com.edu.teaching.domain.vo.BatchClassPromotionResultVO;
import com.edu.teaching.domain.vo.ClassGraduationResultVO;
import com.edu.teaching.domain.vo.ClassPromotionResultVO;
import com.edu.teaching.service.TeachClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 班级管理控制器
 */
@Tag(name = "班级管理")
@RestController
@RequestMapping("/teaching/class")
@RequiredArgsConstructor
public class TeachClassController {

    private final TeachClassService classService;

    @Operation(summary = "分页查询班级列表")
    @GetMapping("/page")
    public R<Page<TeachClass>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            TeachClass query) {
        Page<TeachClass> page = new Page<>(pageNum, pageSize);
        classService.pageList(page, query);
        return R.ok(page);
    }

    @Operation(summary = "获取班级详情")
    @GetMapping("/{id}")
    public R<TeachClass> getById(@PathVariable Long id) {
        return R.ok(classService.getById(id));
    }

    @Operation(summary = "新增班级")
    @PostMapping
    public R<Boolean> add(@RequestBody TeachClass teachClass) {
        teachClass.setStatus("pending");
        teachClass.setCurrentCount(0);
        return R.ok(classService.save(teachClass));
    }

    @Operation(summary = "修改班级")
    @PutMapping
    public R<Boolean> update(@RequestBody TeachClass teachClass) {
        return R.ok(classService.updateById(teachClass));
    }

    @Operation(summary = "删除班级")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(classService.removeById(id));
    }

    @Operation(summary = "开班")
    @PutMapping("/{id}/start")
    public R<Boolean> start(@PathVariable Long id) {
        return R.ok(classService.start(id));
    }

    @Operation(summary = "结班")
    @PutMapping("/{id}/finish")
    public R<Boolean> finish(@PathVariable Long id) {
        return R.ok(classService.finish(id));
    }

    @Operation(summary = "取消班级")
    @PutMapping("/{id}/cancel")
    public R<Boolean> cancel(@PathVariable Long id) {
        return R.ok(classService.cancel(id));
    }

    @Operation(summary = "学员分班")
    @PostMapping("/{classId}/students")
    public R<Boolean> addStudents(@PathVariable Long classId, @RequestBody List<Long> studentIds) {
        return R.ok(classService.addStudents(classId, studentIds));
    }

    @Operation(summary = "学员退班")
    @DeleteMapping("/{classId}/students/{studentId}")
    public R<Boolean> removeStudent(@PathVariable Long classId, @PathVariable Long studentId) {
        return R.ok(classService.removeStudent(classId, studentId));
    }

    @Operation(summary = "班级升班")
    @PostMapping("/promote")
    public R<ClassPromotionResultVO> promoteClass(@Valid @RequestBody ClassPromotionDTO dto) {
        return R.ok(classService.promoteClass(dto));
    }

    @Operation(summary = "批量班级升班")
    @PostMapping("/promote/batch")
    public R<BatchClassPromotionResultVO> batchPromoteClass(@Valid @RequestBody BatchClassPromotionDTO dto) {
        return R.ok(classService.batchPromoteClass(dto));
    }

    @Operation(summary = "班级结业")
    @PostMapping("/graduate")
    public R<ClassGraduationResultVO> graduateClass(@Valid @RequestBody ClassGraduationDTO dto) {
        return R.ok(classService.graduateClass(dto));
    }

    @Operation(summary = "批量班级结业")
    @PostMapping("/graduate/batch")
    public R<BatchClassGraduationResultVO> batchGraduateClass(@Valid @RequestBody BatchClassGraduationDTO dto) {
        return R.ok(classService.batchGraduateClass(dto));
    }
}
