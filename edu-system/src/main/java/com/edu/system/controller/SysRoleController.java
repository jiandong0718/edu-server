package com.edu.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.R;
import com.edu.system.domain.entity.SysRole;
import com.edu.system.service.SysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 */
@Tag(name = "角色管理")
@RestController
@RequestMapping("/system/role")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleService roleService;

    @Operation(summary = "分页查询角色列表")
    @GetMapping("/page")
    public R<Page<SysRole>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) Integer status) {
        Page<SysRole> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null, SysRole::getName, name)
                .like(code != null, SysRole::getCode, code)
                .eq(status != null, SysRole::getStatus, status)
                .orderByAsc(SysRole::getSortOrder);
        roleService.page(page, wrapper);
        return R.ok(page);
    }

    @Operation(summary = "获取所有角色列表")
    @GetMapping("/list")
    public R<List<SysRole>> list() {
        List<SysRole> list = roleService.list(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getStatus, 1)
                .orderByAsc(SysRole::getSortOrder));
        return R.ok(list);
    }

    @Operation(summary = "获取角色详情")
    @GetMapping("/{id}")
    public R<SysRole> getById(@PathVariable Long id) {
        SysRole role = roleService.getById(id);
        if (role != null) {
            role.setMenuIds(roleService.getMenuIds(id));
        }
        return R.ok(role);
    }

    @Operation(summary = "新增角色")
    @PostMapping
    public R<Boolean> add(@RequestBody SysRole role) {
        return R.ok(roleService.addRole(role));
    }

    @Operation(summary = "修改角色")
    @PutMapping
    public R<Boolean> update(@RequestBody SysRole role) {
        return R.ok(roleService.updateRole(role));
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(roleService.deleteRole(id));
    }

    @Operation(summary = "批量删除角色")
    @DeleteMapping("/batch")
    public R<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        return R.ok(roleService.deleteRoles(ids));
    }

    @Operation(summary = "修改状态")
    @PutMapping("/{id}/status")
    public R<Boolean> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        SysRole role = new SysRole();
        role.setId(id);
        role.setStatus(status);
        return R.ok(roleService.updateById(role));
    }
}
