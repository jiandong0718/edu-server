package com.edu.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.R;
import com.edu.common.exception.BusinessException;
import com.edu.framework.mybatis.CampusContextHolder;
import com.edu.framework.security.JwtTokenUtil;
import com.edu.framework.security.LoginUser;
import com.edu.framework.security.SecurityContextHolder;
import com.edu.system.domain.entity.SysCampus;
import com.edu.system.domain.entity.SysUser;
import com.edu.system.domain.vo.CampusSwitchVO;
import com.edu.system.service.SysCampusService;
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
    private final SysCampusService campusService;
    private final JwtTokenUtil jwtTokenUtil;

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

    @Operation(summary = "切换校区")
    @PostMapping("/switch-campus")
    public R<CampusSwitchVO> switchCampus(@RequestParam Long campusId) {
        // 获取当前登录用户
        LoginUser loginUser = SecurityContextHolder.getLoginUser();
        if (loginUser == null) {
            throw new BusinessException("用户未登录");
        }

        // 验证校区是否存在且启用
        SysCampus campus = campusService.getActiveCampus(campusId);

        // 验证用户是否有权限访问该校区
        boolean hasAccess = campusService.validateUserCampusAccess(loginUser.getUserId(), campusId);
        if (!hasAccess) {
            throw new BusinessException("您没有权限访问该校区");
        }

        // 更新登录用户的校区信息
        loginUser.setCampusId(campusId);
        loginUser.setCampusName(campus.getName());

        // 更新 Redis 中的 Token 信息
        jwtTokenUtil.refreshToken(loginUser);

        // 更新当前线程的校区上下文
        CampusContextHolder.setCampusId(campusId);

        // 构建返回结果
        CampusSwitchVO result = CampusSwitchVO.builder()
                .campusId(campusId)
                .campusName(campus.getName())
                .campusCode(campus.getCode())
                .message("校区切换成功")
                .build();

        return R.ok(result);
    }
}
