package com.edu.system.controller;

import com.edu.common.core.R;
import com.edu.system.domain.entity.SysMenu;
import com.edu.system.service.SysMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理控制器
 */
@Tag(name = "菜单管理")
@RestController
@RequestMapping("/system/menu")
@RequiredArgsConstructor
public class SysMenuController {

    private final SysMenuService menuService;

    @Operation(summary = "获取菜单树")
    @GetMapping("/tree")
    public R<List<SysMenu>> tree() {
        return R.ok(menuService.getMenuTree());
    }

    @Operation(summary = "获取菜单详情")
    @GetMapping("/{id}")
    public R<SysMenu> getById(@PathVariable Long id) {
        return R.ok(menuService.getById(id));
    }

    @Operation(summary = "新增菜单")
    @PostMapping
    public R<Boolean> add(@RequestBody SysMenu menu) {
        return R.ok(menuService.addMenu(menu));
    }

    @Operation(summary = "修改菜单")
    @PutMapping
    public R<Boolean> update(@RequestBody SysMenu menu) {
        return R.ok(menuService.updateMenu(menu));
    }

    @Operation(summary = "删除菜单")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(menuService.deleteMenu(id));
    }
}
