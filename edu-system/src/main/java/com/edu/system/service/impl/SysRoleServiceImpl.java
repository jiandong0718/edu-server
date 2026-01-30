package com.edu.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.exception.BusinessException;
import com.edu.system.domain.entity.SysRole;
import com.edu.system.domain.entity.SysRoleMenu;
import com.edu.system.mapper.SysMenuMapper;
import com.edu.system.mapper.SysRoleMapper;
import com.edu.system.mapper.SysRoleMenuMapper;
import com.edu.system.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色服务实现
 */
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private final SysRoleMenuMapper roleMenuMapper;
    private final SysMenuMapper menuMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addRole(SysRole role) {
        // 检查角色编码唯一性
        if (!checkCodeUnique(role.getCode(), null)) {
            throw new BusinessException("角色编码已存在");
        }

        // 保存角色
        boolean result = save(role);

        // 保存角色菜单关联
        if (result && CollUtil.isNotEmpty(role.getMenuIds())) {
            saveRoleMenus(role.getId(), role.getMenuIds());
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRole(SysRole role) {
        // 检查角色编码唯一性
        if (!checkCodeUnique(role.getCode(), role.getId())) {
            throw new BusinessException("角色编码已存在");
        }

        // 更新角色
        boolean result = updateById(role);

        // 更新角色菜单关联
        if (result && role.getMenuIds() != null) {
            // 删除原有菜单
            roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>()
                    .eq(SysRoleMenu::getRoleId, role.getId()));
            // 保存新菜单
            if (CollUtil.isNotEmpty(role.getMenuIds())) {
                saveRoleMenus(role.getId(), role.getMenuIds());
            }
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRole(Long roleId) {
        // 删除角色菜单关联
        roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>()
                .eq(SysRoleMenu::getRoleId, roleId));
        // 删除角色
        return removeById(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRoles(List<Long> roleIds) {
        // 删除角色菜单关联
        roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>()
                .in(SysRoleMenu::getRoleId, roleIds));
        // 删除角色
        return removeByIds(roleIds);
    }

    @Override
    public boolean checkCodeUnique(String code, Long roleId) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getCode, code);
        if (roleId != null) {
            wrapper.ne(SysRole::getId, roleId);
        }
        return count(wrapper) == 0;
    }

    @Override
    public List<Long> getMenuIds(Long roleId) {
        return menuMapper.selectMenuIdsByRoleId(roleId);
    }

    private void saveRoleMenus(Long roleId, List<Long> menuIds) {
        List<SysRoleMenu> roleMenus = new ArrayList<>();
        for (Long menuId : menuIds) {
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            roleMenus.add(roleMenu);
        }
        for (SysRoleMenu roleMenu : roleMenus) {
            roleMenuMapper.insert(roleMenu);
        }
    }
}
