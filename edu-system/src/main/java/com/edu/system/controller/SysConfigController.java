package com.edu.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.R;
import com.edu.system.domain.entity.SysConfig;
import com.edu.system.service.SysConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统配置控制器
 */
@Tag(name = "系统配置")
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
            @RequestParam(required = false) String configName,
            @RequestParam(required = false) String configKey,
            @RequestParam(required = false) String category) {
        Page<SysConfig> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(configName != null, SysConfig::getConfigName, configName)
                .like(configKey != null, SysConfig::getConfigKey, configKey)
                .eq(category != null, SysConfig::getCategory, category)
                .orderByDesc(SysConfig::getCreateTime);
        configService.page(page, wrapper);
        return R.ok(page);
    }

    @Operation(summary = "获取所有配置列表")
    @GetMapping("/list")
    public R<List<SysConfig>> list() {
        return R.ok(configService.list());
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
    public R<Boolean> add(@RequestBody SysConfig config) {
        if (!configService.checkConfigKeyUnique(config.getConfigKey(), null)) {
            return R.fail("配置键已存在");
        }
        return R.ok(configService.save(config));
    }

    @Operation(summary = "修改配置")
    @PutMapping
    public R<Boolean> update(@RequestBody SysConfig config) {
        if (!configService.checkConfigKeyUnique(config.getConfigKey(), config.getId())) {
            return R.fail("配置键已存在");
        }
        return R.ok(configService.updateById(config));
    }

    @Operation(summary = "删除配置")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        SysConfig config = configService.getById(id);
        if (config != null && config.getIsSystem() == 1) {
            return R.fail("系统内置配置不能删除");
        }
        return R.ok(configService.removeById(id));
    }

    @Operation(summary = "批量删除配置")
    @DeleteMapping("/batch")
    public R<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        // 检查是否包含系统配置
        List<SysConfig> configs = configService.listByIds(ids);
        boolean hasSystemConfig = configs.stream().anyMatch(c -> c.getIsSystem() == 1);
        if (hasSystemConfig) {
            return R.fail("不能删除系统内置配置");
        }
        return R.ok(configService.removeByIds(ids));
    }
}
