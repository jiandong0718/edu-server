package com.edu.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.R;
import com.edu.system.domain.entity.SysDictData;
import com.edu.system.domain.entity.SysDictType;
import com.edu.system.service.SysDictService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典管理控制器
 */
@Tag(name = "字典管理")
@RestController
@RequestMapping("/system/dict")
@RequiredArgsConstructor
public class SysDictController {

    private final SysDictService dictService;

    // ==================== 字典类型 ====================

    @Operation(summary = "分页查询字典类型")
    @GetMapping("/type/page")
    public R<Page<SysDictType>> typePage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) Integer status) {
        Page<SysDictType> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null, SysDictType::getName, name)
                .like(code != null, SysDictType::getCode, code)
                .eq(status != null, SysDictType::getStatus, status)
                .orderByDesc(SysDictType::getCreateTime);
        dictService.page(page, wrapper);
        return R.ok(page);
    }

    @Operation(summary = "获取字典类型详情")
    @GetMapping("/type/{id}")
    public R<SysDictType> getTypeById(@PathVariable Long id) {
        return R.ok(dictService.getById(id));
    }

    @Operation(summary = "新增字典类型")
    @PostMapping("/type")
    public R<Boolean> addType(@RequestBody SysDictType dictType) {
        return R.ok(dictService.addDictType(dictType));
    }

    @Operation(summary = "修改字典类型")
    @PutMapping("/type")
    public R<Boolean> updateType(@RequestBody SysDictType dictType) {
        return R.ok(dictService.updateDictType(dictType));
    }

    @Operation(summary = "删除字典类型")
    @DeleteMapping("/type/{id}")
    public R<Boolean> deleteType(@PathVariable Long id) {
        return R.ok(dictService.deleteDictType(id));
    }

    // ==================== 字典数据 ====================

    @Operation(summary = "根据字典编码获取字典数据")
    @GetMapping("/data/{dictCode}")
    public R<List<SysDictData>> getDataByCode(@PathVariable String dictCode) {
        return R.ok(dictService.getDictDataByCode(dictCode));
    }

    @Operation(summary = "新增字典数据")
    @PostMapping("/data")
    public R<Boolean> addData(@RequestBody SysDictData dictData) {
        return R.ok(dictService.addDictData(dictData));
    }

    @Operation(summary = "修改字典数据")
    @PutMapping("/data")
    public R<Boolean> updateData(@RequestBody SysDictData dictData) {
        return R.ok(dictService.updateDictData(dictData));
    }

    @Operation(summary = "删除字典数据")
    @DeleteMapping("/data/{id}")
    public R<Boolean> deleteData(@PathVariable Long id) {
        return R.ok(dictService.deleteDictData(id));
    }

    @Operation(summary = "刷新字典缓存")
    @PostMapping("/refresh")
    public R<Void> refreshCache() {
        dictService.refreshCache();
        return R.ok();
    }
}
