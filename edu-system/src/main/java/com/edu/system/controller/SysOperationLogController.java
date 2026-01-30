package com.edu.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.R;
import com.edu.system.domain.entity.SysOperationLog;
import com.edu.system.service.SysOperationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 操作日志控制器
 */
@Tag(name = "操作日志")
@RestController
@RequestMapping("/system/operation-log")
@RequiredArgsConstructor
public class SysOperationLogController {

    private final SysOperationLogService operationLogService;

    @Operation(summary = "分页查询操作日志")
    @GetMapping("/page")
    public R<Page<SysOperationLog>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String operatorName,
            @RequestParam(required = false) Integer businessType,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long campusId,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        Page<SysOperationLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysOperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(title != null, SysOperationLog::getTitle, title)
                .like(operatorName != null, SysOperationLog::getOperatorName, operatorName)
                .eq(businessType != null, SysOperationLog::getBusinessType, businessType)
                .eq(status != null, SysOperationLog::getStatus, status)
                .eq(campusId != null, SysOperationLog::getCampusId, campusId)
                .ge(startTime != null, SysOperationLog::getCreateTime, startTime)
                .le(endTime != null, SysOperationLog::getCreateTime, endTime)
                .orderByDesc(SysOperationLog::getCreateTime);
        operationLogService.page(page, wrapper);
        return R.ok(page);
    }

    @Operation(summary = "获取操作日志详情")
    @GetMapping("/{id}")
    public R<SysOperationLog> getById(@PathVariable Long id) {
        return R.ok(operationLogService.getById(id));
    }

    @Operation(summary = "删除操作日志")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(operationLogService.removeById(id));
    }

    @Operation(summary = "批量删除操作日志")
    @DeleteMapping("/batch")
    public R<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        return R.ok(operationLogService.removeByIds(ids));
    }

    @Operation(summary = "清空操作日志")
    @DeleteMapping("/clean")
    public R<Boolean> clean(@RequestParam(required = false) Integer days) {
        LambdaQueryWrapper<SysOperationLog> wrapper = new LambdaQueryWrapper<>();
        if (days != null && days > 0) {
            LocalDateTime beforeTime = LocalDateTime.now().minusDays(days);
            wrapper.lt(SysOperationLog::getCreateTime, beforeTime);
        }
        return R.ok(operationLogService.remove(wrapper));
    }
}
