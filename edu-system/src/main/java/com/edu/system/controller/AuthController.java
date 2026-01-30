package com.edu.system.controller;

import cn.hutool.crypto.digest.BCrypt;
import com.edu.common.core.R;
import com.edu.common.exception.BusinessException;
import com.edu.framework.security.JwtTokenUtil;
import com.edu.framework.security.LoginUser;
import com.edu.framework.security.SecurityContextHolder;
import com.edu.system.domain.entity.SysMenu;
import com.edu.system.domain.entity.SysUser;
import com.edu.system.service.SysMenuService;
import com.edu.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 认证控制器
 */
@Tag(name = "认证管理")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SysUserService userService;
    private final SysMenuService menuService;
    private final JwtTokenUtil jwtTokenUtil;

    @Operation(summary = "登录")
    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody LoginRequest request) {
        // 查询用户
        SysUser user = userService.getByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 验证密码
        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 检查状态
        if (user.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }

        // 获取角色和权限
        List<String> roles = userService.getRoleCodes(user.getId());
        List<String> permissions = userService.getPermissions(user.getId());

        // 构建登录用户
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getId());
        loginUser.setUsername(user.getUsername());
        loginUser.setRealName(user.getRealName());
        loginUser.setAvatar(user.getAvatar());
        loginUser.setCampusId(user.getCampusId());
        loginUser.setCampusName(user.getCampusName());
        loginUser.setRoles(roles);
        loginUser.setPermissions(permissions);

        // 生成 Token
        String token = jwtTokenUtil.createToken(loginUser);

        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userInfo", loginUser);

        return R.ok(result);
    }

    @Operation(summary = "登出")
    @PostMapping("/logout")
    public R<Void> logout(HttpServletRequest request) {
        String token = jwtTokenUtil.getToken(request);
        if (token != null) {
            jwtTokenUtil.deleteToken(token);
        }
        return R.ok();
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public R<LoginUser> info() {
        LoginUser loginUser = SecurityContextHolder.getLoginUser();
        if (loginUser == null) {
            throw new BusinessException("未登录");
        }
        return R.ok(loginUser);
    }

    @Operation(summary = "获取当前用户菜单")
    @GetMapping("/menus")
    public R<List<SysMenu>> menus() {
        LoginUser loginUser = SecurityContextHolder.getLoginUser();
        if (loginUser == null) {
            throw new BusinessException("未登录");
        }

        // 超级管理员返回所有菜单
        if (loginUser.getRoles().contains("super_admin")) {
            return R.ok(menuService.getMenuTree());
        }

        return R.ok(menuService.getMenuTreeByUserId(loginUser.getUserId()));
    }

    @Operation(summary = "修改密码")
    @PostMapping("/password")
    public R<Boolean> changePassword(@RequestBody ChangePasswordRequest request) {
        LoginUser loginUser = SecurityContextHolder.getLoginUser();
        if (loginUser == null) {
            throw new BusinessException("未登录");
        }
        return R.ok(userService.changePassword(loginUser.getUserId(),
                request.getOldPassword(), request.getNewPassword()));
    }

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }

    @Data
    public static class ChangePasswordRequest {
        private String oldPassword;
        private String newPassword;
    }
}
