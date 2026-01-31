package com.edu.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.R;
import com.edu.framework.security.SecurityContextHolder;
import com.edu.system.domain.entity.SysCampus;
import com.edu.system.service.SysCampusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 校区管理控制器
 */
@Tag(name = "校区管理")
@RestController
@RequestMapping("/system/campus")
@RequiredArgsConstructor
public class SysCampusController {

    private final SysCampusService campusService;

    @Operation(summary = "分页查询校区列表")
    @GetMapping("/page")
    public R<Page<SysCampus>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status) {
        Page<SysCampus> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysCampus> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null, SysCampus::getName, name)
                .eq(status != null, SysCampus::getStatus, status)
                .orderByAsc(SysCampus::getSortOrder);
        campusService.page(page, wrapper);
        return R.ok(page);
    }

    @Operation(summary = "获取所有校区列表")
    @GetMapping("/list")
    public R<List<SysCampus>> list() {
        List<SysCampus> list = campusService.list(new LambdaQueryWrapper<SysCampus>()
                .eq(SysCampus::getStatus, 1)
                .orderByAsc(SysCampus::getSortOrder));
        return R.ok(list);
    }

    @Operation(summary = "获取当前用户可访问的校区列表")
    @GetMapping("/accessible")
    public R<List<SysCampus>> getAccessibleCampuses() {
        Long userId = SecurityContextHolder.getUserId();
        // 获取所有启用的校区
        // 注意：这里简化处理，返回所有启用的校区
        // 如果需要更细粒度的权限控制，需要根据用户角色和权限过滤
        List<SysCampus> list = campusService.list(new LambdaQueryWrapper<SysCampus>()
                .eq(SysCampus::getStatus, 1)
                .orderByAsc(SysCampus::getSortOrder));
        return R.ok(list);
    }

    @Operation(summary = "获取校区详情")
    @GetMapping("/{id}")
    public R<SysCampus> getById(@PathVariable Long id) {
        return R.ok(campusService.getById(id));
    }

    @Operation(summary = "新增校区")
    @PostMapping
    public R<Boolean> add(@RequestBody SysCampus campus) {
        if (!campusService.checkCodeUnique(campus.getCode(), null)) {
            return R.fail("校区编码已存在");
        }
        return R.ok(campusService.save(campus));
    }

    @Operation(summary = "修改校区")
    @PutMapping
    public R<Boolean> update(@RequestBody SysCampus campus) {
        if (!campusService.checkCodeUnique(campus.getCode(), campus.getId())) {
            return R.fail("校区编码已存在");
        }
        return R.ok(campusService.updateById(campus));
    }

    @Operation(summary = "删除校区")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(campusService.removeById(id));
    }

    @Operation(summary = "修改状态")
    @PutMapping("/{id}/status")
    public R<Boolean> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        SysCampus campus = new SysCampus();
        campus.setId(id);
        campus.setStatus(status);
        return R.ok(campusService.updateById(campus));
    }
}
