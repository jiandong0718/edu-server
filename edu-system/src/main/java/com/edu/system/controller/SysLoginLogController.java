package com.edu.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.R;
import com.edu.system.domain.entity.SysLoginLog;
import com.edu.system.service.SysLoginLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
    public R<Page<SysLoginLog>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String ip,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        Page<SysLoginLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysLoginLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(username != null, SysLoginLog::getUsername, username)
                .like(ip != null, SysLoginLog::getIp, ip)
                .eq(status != null, SysLoginLog::getStatus, status)
                .ge(startTime != null, SysLoginLog::getLoginTime, startTime)
                .le(endTime != null, SysLoginLog::getLoginTime, endTime)
                .orderByDesc(SysLoginLog::getLoginTime);
        loginLogService.page(page, wrapper);
        return R.ok(page);
    }

    @Operation(summary = "获取登录日志详情")
    @GetMapping("/{id}")
    public R<SysLoginLog> getById(@PathVariable Long id) {
        return R.ok(loginLogService.getById(id));
    }

    @Operation(summary = "删除登录日志")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(loginLogService.removeById(id));
    }

    @Operation(summary = "批量删除登录日志")
    @DeleteMapping("/batch")
    public R<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        return R.ok(loginLogService.removeByIds(ids));
    }

    @Operation(summary = "清空登录日志")
    @DeleteMapping("/clean")
    public R<Boolean> clean(@RequestParam(required = false) Integer days) {
        LambdaQueryWrapper<SysLoginLog> wrapper = new LambdaQueryWrapper<>();
        if (days != null && days > 0) {
            LocalDateTime beforeTime = LocalDateTime.now().minusDays(days);
            wrapper.lt(SysLoginLog::getLoginTime, beforeTime);
        }
        return R.ok(loginLogService.remove(wrapper));
    }
}
