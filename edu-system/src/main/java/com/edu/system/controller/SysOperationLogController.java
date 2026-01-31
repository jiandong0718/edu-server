package com.edu.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.R;
import com.edu.framework.annotation.OperationLog;
import com.edu.system.domain.dto.OperationLogQueryDTO;
import com.edu.system.domain.vo.OperationLogVO;
import com.edu.system.service.SysOperationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    @PreAuthorize("hasAuthority('system:operationLog:query')")
    public R<Page<OperationLogVO>> page(OperationLogQueryDTO queryDTO) {
        Page<OperationLogVO> page = operationLogService.pageQuery(queryDTO);
        return R.ok(page);
    }

    @Operation(summary = "获取操作日志详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:operationLog:query')")
    public R<OperationLogVO> getById(@PathVariable Long id) {
        OperationLogVO vo = operationLogService.getDetail(id);
        return R.ok(vo);
    }

    @Operation(summary = "批量删除操作日志")
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('system:operationLog:delete')")
    @OperationLog(module = "操作日志", type = OperationLog.OperationType.DELETE, description = "批量删除操作日志")
    public R<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        return R.ok(operationLogService.removeByIds(ids));
    }

    @Operation(summary = "清空操作日志")
    @DeleteMapping("/clean")
    @PreAuthorize("hasAuthority('system:operationLog:delete')")
    @OperationLog(module = "操作日志", type = OperationLog.OperationType.DELETE, description = "清空操作日志")
    public R<Boolean> clean(@RequestParam(defaultValue = "30") Integer days) {
        return R.ok(operationLogService.cleanLogs(days));
    }
}
