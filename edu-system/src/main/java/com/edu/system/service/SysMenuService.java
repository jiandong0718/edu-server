package com.edu.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.system.domain.entity.SysMenu;

import java.util.List;

/**
 * 菜单服务接口
 */
public interface SysMenuService extends IService<SysMenu> {

    /**
     * 获取用户菜单树
     */
    List<SysMenu> getMenuTreeByUserId(Long userId);

    /**
     * 获取所有菜单树
     */
    List<SysMenu> getMenuTree();

    /**
     * 新增菜单
     */
    boolean addMenu(SysMenu menu);

    /**
     * 修改菜单
     */
    boolean updateMenu(SysMenu menu);

    /**
     * 删除菜单
     */
    boolean deleteMenu(Long menuId);

    /**
     * 检查是否有子菜单
     */
    boolean hasChildren(Long menuId);
}
