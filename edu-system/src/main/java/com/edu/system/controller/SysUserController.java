package com.edu.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.R;
import com.edu.system.domain.entity.SysUser;
import com.edu.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/system/user")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService userService;

    @Operation(summary = "分页查询用户列表")
    @GetMapping("/page")
    public R<Page<SysUser>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            SysUser user) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        userService.pageList(page, user);
        return R.ok(page);
    }

    @Operation(summary = "获取用户详情")
    @GetMapping("/{id}")
    public R<SysUser> getById(@PathVariable Long id) {
        return R.ok(userService.getById(id));
    }

    @Operation(summary = "新增用户")
    @PostMapping
    public R<Boolean> add(@RequestBody SysUser user) {
        return R.ok(userService.addUser(user));
    }

    @Operation(summary = "修改用户")
    @PutMapping
    public R<Boolean> update(@RequestBody SysUser user) {
        return R.ok(userService.updateUser(user));
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(userService.deleteUser(id));
    }

    @Operation(summary = "批量删除用户")
    @DeleteMapping("/batch")
    public R<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        return R.ok(userService.deleteUsers(ids));
    }

    @Operation(summary = "重置密码")
    @PutMapping("/{id}/password/reset")
    public R<Boolean> resetPassword(@PathVariable Long id, @RequestParam String newPassword) {
        return R.ok(userService.resetPassword(id, newPassword));
    }

    @Operation(summary = "修改状态")
    @PutMapping("/{id}/status")
    public R<Boolean> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setStatus(status);
        return R.ok(userService.updateById(user));
    }
}
