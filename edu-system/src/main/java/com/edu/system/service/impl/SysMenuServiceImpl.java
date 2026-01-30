package com.edu.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.exception.BusinessException;
import com.edu.system.domain.entity.SysMenu;
import com.edu.system.mapper.SysMenuMapper;
import com.edu.system.service.SysMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单服务实现
 */
@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Override
    public List<SysMenu> getMenuTreeByUserId(Long userId) {
        List<SysMenu> menus = baseMapper.selectMenusByUserId(userId);
        return buildMenuTree(menus);
    }

    @Override
    public List<SysMenu> getMenuTree() {
        List<SysMenu> menus = list(new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getStatus, 1)
                .orderByAsc(SysMenu::getParentId)
                .orderByAsc(SysMenu::getSortOrder));
        return buildMenuTree(menus);
    }

    @Override
    public boolean addMenu(SysMenu menu) {
        return save(menu);
    }

    @Override
    public boolean updateMenu(SysMenu menu) {
        return updateById(menu);
    }

    @Override
    public boolean deleteMenu(Long menuId) {
        if (hasChildren(menuId)) {
            throw new BusinessException("存在子菜单，无法删除");
        }
        return removeById(menuId);
    }

    @Override
    public boolean hasChildren(Long menuId) {
        return count(new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getParentId, menuId)) > 0;
    }

    /**
     * 构建菜单树
     */
    private List<SysMenu> buildMenuTree(List<SysMenu> menus) {
        if (CollUtil.isEmpty(menus)) {
            return new ArrayList<>();
        }

        // 获取所有根节点
        List<SysMenu> rootMenus = menus.stream()
                .filter(menu -> menu.getParentId() == null || menu.getParentId() == 0)
                .collect(Collectors.toList());

        // 递归构建子节点
        for (SysMenu rootMenu : rootMenus) {
            buildChildren(rootMenu, menus);
        }

        return rootMenus;
    }

    /**
     * 递归构建子节点
     */
    private void buildChildren(SysMenu parent, List<SysMenu> allMenus) {
        List<SysMenu> children = allMenus.stream()
                .filter(menu -> parent.getId().equals(menu.getParentId()))
                .collect(Collectors.toList());

        if (CollUtil.isNotEmpty(children)) {
            parent.setChildren(children);
            for (SysMenu child : children) {
                buildChildren(child, allMenus);
            }
        }
    }
}
