package com.edu.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.R;
import com.edu.framework.annotation.OperationLog;
import com.edu.system.domain.dto.LoginLogQueryDTO;
import com.edu.system.domain.entity.SysLoginLog;
import com.edu.system.domain.vo.LoginLogVO;
import com.edu.system.domain.vo.LoginStatisticsVO;
import com.edu.system.service.SysLoginLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 登录日志控制器
 */
@Tag(name = "登录日志")
@RestController
@RequestMapping("/system/login-log")
@RequiredArgsConstructor
public class SysLoginLogController {

    private final SysLoginLogService loginLogService;

    @Operation(summary = "分页查询登录日志")
    @GetMapping("/page")
    public R<Page<LoginLogVO>> page(LoginLogQueryDTO queryDTO) {
        Page<LoginLogVO> page = loginLogService.pageLoginLogs(queryDTO);
        return R.ok(page);
    }

    @Operation(summary = "获取登录日志详情")
    @GetMapping("/{id}")
    public R<SysLoginLog> getById(@PathVariable Long id) {
        return R.ok(loginLogService.getById(id));
    }

    @Operation(summary = "获取登录统计信息")
    @GetMapping("/statistics")
    public R<LoginStatisticsVO> getStatistics() {
        return R.ok(loginLogService.getLoginStatistics());
    }

    @Operation(summary = "删除登录日志")
    @OperationLog(module = "登录日志", type = OperationLog.OperationType.DELETE, description = "删除登录日志")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(loginLogService.removeById(id));
    }

    @Operation(summary = "批量删除登录日志")
    @OperationLog(module = "登录日志", type = OperationLog.OperationType.DELETE, description = "批量删除登录日志")
    @DeleteMapping("/batch")
    public R<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        return R.ok(loginLogService.removeByIds(ids));
    }

    @Operation(summary = "清空登录日志")
    @OperationLog(module = "登录日志", type = OperationLog.OperationType.DELETE, description = "清空登录日志")
    @DeleteMapping("/clear")
    public R<Boolean> clear(@RequestParam(required = false) Integer days) {
        return R.ok(loginLogService.clearLoginLogs(days));
    }
}
