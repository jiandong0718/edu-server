package com.edu.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.R;
import com.edu.system.domain.dto.SysConfigBatchUpdateDTO;
import com.edu.system.domain.dto.SysConfigDTO;
import com.edu.system.domain.dto.SysConfigQueryDTO;
import com.edu.system.domain.entity.SysConfig;
import com.edu.system.service.SysConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统参数配置控制器
 */
@Tag(name = "系统参数配置")
@RestController
@RequestMapping("/system/config")
@RequiredArgsConstructor
public class SysConfigController {

    private final SysConfigService configService;

    @Operation(summary = "分页查询配置列表")
    @GetMapping("/page")
    public R<Page<SysConfig>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            SysConfigQueryDTO queryDTO) {
        Page<SysConfig> page = new Page<>(pageNum, pageSize);
        configService.pageList(page, queryDTO);
        return R.ok(page);
    }

    @Operation(summary = "获取所有配置列表")
    @GetMapping("/list")
    public R<List<SysConfig>> list(SysConfigQueryDTO queryDTO) {
        Page<SysConfig> page = new Page<>(1, 1000);
        configService.pageList(page, queryDTO);
        return R.ok(page.getRecords());
    }

    @Operation(summary = "根据分组查询配置列表")
    @GetMapping("/group/{configGroup}")
    public R<List<SysConfig>> listByGroup(@PathVariable String configGroup) {
        return R.ok(configService.listByGroup(configGroup));
    }

    @Operation(summary = "获取所有分组")
    @GetMapping("/groups")
    public R<List<String>> listGroups() {
        return R.ok(configService.listGroups());
    }

    @Operation(summary = "根据配置键获取配置值")
    @GetMapping("/key/{configKey}")
    public R<String> getByKey(@PathVariable String configKey) {
        String value = configService.getConfigValue(configKey);
        return value != null ? R.ok(value) : R.fail("配置不存在");
    }

    @Operation(summary = "获取配置详情")
    @GetMapping("/{id}")
    public R<SysConfig> getById(@PathVariable Long id) {
        return R.ok(configService.getById(id));
    }

    @Operation(summary = "新增配置")
    @PostMapping
    public R<Boolean> add(@Validated @RequestBody SysConfigDTO configDTO) {
        SysConfig config = new SysConfig();
        BeanUtils.copyProperties(configDTO, config);
        return R.ok(configService.save(config));
    }

    @Operation(summary = "修改配置")
    @PutMapping
    public R<Boolean> update(@Validated @RequestBody SysConfigDTO configDTO) {
        SysConfig config = new SysConfig();
        BeanUtils.copyProperties(configDTO, config);
        return R.ok(configService.updateById(config));
    }

    @Operation(summary = "批量更新配置")
    @PutMapping("/batch")
    public R<Boolean> batchUpdate(@Validated @RequestBody SysConfigBatchUpdateDTO batchUpdateDTO) {
        return R.ok(configService.batchUpdate(batchUpdateDTO));
    }

    @Operation(summary = "删除配置")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(configService.removeById(id));
    }

    @Operation(summary = "批量删除配置")
    @DeleteMapping("/batch")
    public R<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        // 检查是否包含系统配置
        List<SysConfig> configs = configService.listByIds(ids);
        boolean hasSystemConfig = configs.stream()
            .anyMatch(c -> c.getIsSystem() != null && c.getIsSystem() == 1);
        if (hasSystemConfig) {
            return R.fail("不能删除系统内置配置");
        }
        return R.ok(configService.removeByIds(ids));
    }

    @Operation(summary = "重置为默认值")
    @PutMapping("/{id}/reset")
    public R<Boolean> resetToDefault(@PathVariable Long id) {
        return R.ok(configService.resetToDefault(id));
    }

    @Operation(summary = "刷新缓存")
    @PostMapping("/cache/refresh")
    public R<Void> refreshCache() {
        configService.refreshCache();
        return R.ok();
    }

    @Operation(summary = "清除缓存")
    @DeleteMapping("/cache")
    public R<Void> clearCache() {
        configService.clearCache();
        return R.ok();
    }
}
