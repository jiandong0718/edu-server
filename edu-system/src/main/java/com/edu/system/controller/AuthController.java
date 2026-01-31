package com.edu.system.controller;

import cn.hutool.crypto.digest.BCrypt;
import com.edu.common.core.R;
import com.edu.common.exception.BusinessException;
import com.edu.framework.annotation.OperationLog;
import com.edu.framework.security.JwtTokenUtil;
import com.edu.framework.security.LoginUser;
import com.edu.framework.security.SecurityContextHolder;
import com.edu.system.domain.entity.SysCampus;
import com.edu.system.domain.entity.SysMenu;
import com.edu.system.domain.entity.SysUser;
import com.edu.system.domain.vo.CampusSwitchVO;
import com.edu.system.service.SysCampusService;
import com.edu.system.service.SysConfigService;
import com.edu.system.service.SysLoginLogService;
import com.edu.system.service.SysMenuService;
import com.edu.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
    private final SysConfigService configService;
    private final SysLoginLogService loginLogService;
    private final SysCampusService campusService;

    @Operation(summary = "登录")
    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        String ip = getIpAddress(httpRequest);

        // 查询用户
        SysUser user = userService.getByUsername(request.getUsername());
        if (user == null) {
            // 记录登录失败日志
            loginLogService.recordLoginLog(request.getUsername(), null, ip, 0, "用户不存在");
            throw new BusinessException("用户名或密码错误");
        }

        // 检查账号是否被锁定
        if (user.getLockTime() != null) {
            Integer lockDuration = configService.getConfigValueAsInt("login.lock.duration", 30);
            LocalDateTime unlockTime = user.getLockTime().plusMinutes(lockDuration);
            if (LocalDateTime.now().isBefore(unlockTime)) {
                long remainingMinutes = java.time.Duration.between(LocalDateTime.now(), unlockTime).toMinutes();
                loginLogService.recordLoginLog(user.getUsername(), user.getId(), ip, 0, "账号已锁定");
                throw new BusinessException("账号已被锁定，请在 " + remainingMinutes + " 分钟后重试");
            } else {
                // 锁定时间已过，解锁账号
                user.setLockTime(null);
                user.setLoginFailCount(0);
                userService.updateById(user);
            }
        }

        // 验证密码
        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            // 登录失败，增加失败次数
            Integer failCount = (user.getLoginFailCount() == null ? 0 : user.getLoginFailCount()) + 1;
            Integer maxFailCount = configService.getConfigValueAsInt("login.fail.lock.count", 5);

            user.setLoginFailCount(failCount);
            if (failCount >= maxFailCount) {
                // 达到最大失败次数，锁定账号
                user.setLockTime(LocalDateTime.now());
                userService.updateById(user);
                loginLogService.recordLoginLog(user.getUsername(), user.getId(), ip, 0, "密码错误次数过多，账号已锁定");
                throw new BusinessException("密码错误次数过多，账号已被锁定 " + configService.getConfigValueAsInt("login.lock.duration", 30) + " 分钟");
            } else {
                userService.updateById(user);
                loginLogService.recordLoginLog(user.getUsername(), user.getId(), ip, 0, "密码错误");
                throw new BusinessException("用户名或密码错误，还可尝试 " + (maxFailCount - failCount) + " 次");
            }
        }

        // 检查状态
        if (user.getStatus() != 1) {
            loginLogService.recordLoginLog(user.getUsername(), user.getId(), ip, 0, "账号已禁用");
            throw new BusinessException("账号已被禁用");
        }

        // 登录成功，重置失败次数
        user.setLoginFailCount(0);
        user.setLockTime(null);
        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginIp(ip);
        userService.updateById(user);

        // 记录登录成功日志
        loginLogService.recordLoginLog(user.getUsername(), user.getId(), ip, 1, "登录成功");

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

        // 检查是否首次登录
        Boolean forceChangePassword = configService.getConfigValueAsBoolean("login.force.change.password", true);
        if (forceChangePassword && user.getIsFirstLogin() != null && user.getIsFirstLogin() == 1) {
            result.put("isFirstLogin", true);
            result.put("message", "首次登录，请修改密码");
        } else {
            result.put("isFirstLogin", false);
        }

        return R.ok(result);
    }

    /**
     * 获取客户端IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
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

        // 如果是首次登录修改密码，可以不验证旧密码
        SysUser user = userService.getById(loginUser.getUserId());
        if (user.getIsFirstLogin() != null && user.getIsFirstLogin() == 1 && request.isFirstLogin()) {
            // 首次登录修改密码
            user.setPassword(BCrypt.hashpw(request.getNewPassword()));
            user.setIsFirstLogin(0);
            user.setPasswordUpdateTime(LocalDateTime.now());
            return R.ok(userService.updateById(user));
        } else {
            // 普通修改密码，需要验证旧密码
            boolean result = userService.changePassword(loginUser.getUserId(),
                    request.getOldPassword(), request.getNewPassword());
            if (result) {
                // 更新密码修改时间
                user.setPasswordUpdateTime(LocalDateTime.now());
                userService.updateById(user);
            }
            return R.ok(result);
        }
    }

    @Operation(summary = "切换校区")
    @OperationLog(module = "认证管理", type = OperationLog.OperationType.UPDATE, description = "切换校区")
    @PostMapping("/switch-campus")
    public R<CampusSwitchVO> switchCampus(@RequestBody SwitchCampusRequest request) {
        LoginUser loginUser = SecurityContextHolder.getLoginUser();
        if (loginUser == null) {
            throw new BusinessException("未登录");
        }

        // 验证校区ID是否为空
        if (request.getCampusId() == null) {
            throw new BusinessException("校区ID不能为空");
        }

        // 验证校区是否存在且启用
        SysCampus campus = campusService.getActiveCampus(request.getCampusId());

        // 验证用户是否有权限访问该校区
        if (!campusService.validateUserCampusAccess(loginUser.getUserId(), request.getCampusId())) {
            throw new BusinessException("您没有权限访问该校区");
        }

        // 更新用户的当前校区信息（可选：如果需要持久化用户的默认校区）
        SysUser user = new SysUser();
        user.setId(loginUser.getUserId());
        user.setCampusId(request.getCampusId());
        userService.updateById(user);

        // 更新当前登录用户的校区信息
        loginUser.setCampusId(campus.getId());
        loginUser.setCampusName(campus.getName());

        // 更新 Redis 中的用户信息（刷新 Token）
        jwtTokenUtil.refreshToken(loginUser);

        // 更新线程上下文中的校区信息
        com.edu.framework.mybatis.CampusContextHolder.setCampusId(campus.getId());

        // 构建响应
        CampusSwitchVO result = CampusSwitchVO.builder()
                .campusId(campus.getId())
                .campusName(campus.getName())
                .campusCode(campus.getCode())
                .message("切换校区成功")
                .build();

        return R.ok(result);
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
        private boolean firstLogin;  // 是否首次登录修改密码
    }

    @Data
    public static class SwitchCampusRequest {
        private Long campusId;
    }
}
