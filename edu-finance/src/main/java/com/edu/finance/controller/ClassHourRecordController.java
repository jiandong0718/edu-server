package com.edu.finance.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.Result;
import com.edu.finance.domain.entity.ClassHourRecord;
import com.edu.finance.service.ClassHourRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课时消耗记录控制器
 */
@Tag(name = "课时消耗记录管理")
@RestController
@RequestMapping("/finance/class-hour-record")
@RequiredArgsConstructor
public class ClassHourRecordController {

    private final ClassHourRecordService classHourRecordService;

    @Operation(summary = "分页查询课时消耗记录")
    @GetMapping("/page")
    public Result<IPage<ClassHourRecord>> page(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            ClassHourRecord query) {
        Page<ClassHourRecord> page = new Page<>(current, size);
        IPage<ClassHourRecord> result = classHourRecordService.page(page);
        return Result.success(result);
    }

    @Operation(summary = "根据ID查询课时消耗记录")
    @GetMapping("/{id}")
    public Result<ClassHourRecord> getById(@PathVariable Long id) {
        ClassHourRecord record = classHourRecordService.getById(id);
        return Result.success(record);
    }

    @Operation(summary = "查询学员的课时消耗记录")
    @GetMapping("/student/{studentId}")
    public Result<List<ClassHourRecord>> getByStudentId(
            @PathVariable Long studentId,
            @RequestParam(required = false) Long accountId) {
        List<ClassHourRecord> records = classHourRecordService.getByStudentId(studentId, accountId);
        return Result.success(records);
    }
}
