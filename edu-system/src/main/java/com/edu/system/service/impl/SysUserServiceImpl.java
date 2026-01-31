package com.edu.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.exception.BusinessException;
import com.edu.system.domain.entity.SysUser;
import com.edu.system.domain.entity.SysUserRole;
import com.edu.system.mapper.SysUserMapper;
import com.edu.system.mapper.SysUserRoleMapper;
import com.edu.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户服务实现
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SysUserRoleMapper userRoleMapper;

    @Override
    public SysUser getByUsername(String username) {
        return baseMapper.selectByUsername(username);
    }

    @Override
    public IPage<SysUser> pageList(IPage<SysUser> page, SysUser user) {
        List<SysUser> list = baseMapper.selectUserList(user);
        page.setRecords(list);
        page.setTotal(list.size());
        return page;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addUser(SysUser user) {
        // 检查用户名唯一性
        if (!checkUsernameUnique(user.getUsername(), null)) {
            throw new BusinessException("用户名已存在");
        }

        // 验证密码强度
        validatePasswordStrength(user.getPassword());

        // 加密密码
        user.setPassword(BCrypt.hashpw(user.getPassword()));

        // 保存用户
        boolean result = save(user);

        // 保存用户角色关联
        if (result && CollUtil.isNotEmpty(user.getRoleIds())) {
            saveUserRoles(user.getId(), user.getRoleIds());
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUser(SysUser user) {
        // 检查用户名唯一性
        if (!checkUsernameUnique(user.getUsername(), user.getId())) {
            throw new BusinessException("用户名已存在");
        }

        // 不更新密码
        user.setPassword(null);

        // 更新用户
        boolean result = updateById(user);

        // 更新用户角色关联
        if (result && user.getRoleIds() != null) {
            // 删除原有角色
            userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
                    .eq(SysUserRole::getUserId, user.getId()));
            // 保存新角色
            if (CollUtil.isNotEmpty(user.getRoleIds())) {
                saveUserRoles(user.getId(), user.getRoleIds());
            }
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUser(Long userId) {
        // 删除用户角色关联
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, userId));
        // 删除用户
        return removeById(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUsers(List<Long> userIds) {
        // 删除用户角色关联
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
                .in(SysUserRole::getUserId, userIds));
        // 删除用户
        return removeByIds(userIds);
    }

    @Override
    public boolean resetPassword(Long userId, String newPassword) {
        // 验证密码强度
        validatePasswordStrength(newPassword);

        SysUser user = new SysUser();
        user.setId(userId);
        user.setPassword(BCrypt.hashpw(newPassword));
        return updateById(user);
    }

    @Override
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (!BCrypt.checkpw(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        // 验证新密码强度
        validatePasswordStrength(newPassword);

        user.setPassword(BCrypt.hashpw(newPassword));
        return updateById(user);
    }

    /**
     * 验证密码强度
     * 要求：至少8位，包含大小写字母和数字
     */
    private void validatePasswordStrength(String password) {
        if (password == null || password.length() < 8) {
            throw new BusinessException("密码长度至少为8位");
        }

        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpperCase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowerCase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            }
        }

        if (!hasUpperCase) {
            throw new BusinessException("密码必须包含至少一个大写字母");
        }
        if (!hasLowerCase) {
            throw new BusinessException("密码必须包含至少一个小写字母");
        }
        if (!hasDigit) {
            throw new BusinessException("密码必须包含至少一个数字");
        }
    }

    @Override
    public List<String> getRoleCodes(Long userId) {
        return baseMapper.selectRoleCodesByUserId(userId);
    }

    @Override
    public List<String> getPermissions(Long userId) {
        return baseMapper.selectPermissionsByUserId(userId);
    }

    @Override
    public boolean checkUsernameUnique(String username, Long userId) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        if (userId != null) {
            wrapper.ne(SysUser::getId, userId);
        }
        return count(wrapper) == 0;
    }

    private void saveUserRoles(Long userId, List<Long> roleIds) {
        List<SysUserRole> userRoles = new ArrayList<>();
        for (Long roleId : roleIds) {
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRoles.add(userRole);
        }
        for (SysUserRole userRole : userRoles) {
            userRoleMapper.insert(userRole);
        }
    }
}
