package com.edu.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.system.domain.entity.SysRole;

import java.util.List;

/**
 * 角色服务接口
 */
public interface SysRoleService extends IService<SysRole> {

    /**
     * 新增角色
     */
    boolean addRole(SysRole role);

    /**
     * 修改角色
     */
    boolean updateRole(SysRole role);

    /**
     * 删除角色
     */
    boolean deleteRole(Long roleId);

    /**
     * 批量删除角色
     */
    boolean deleteRoles(List<Long> roleIds);

    /**
     * 检查角色编码是否唯一
     */
    boolean checkCodeUnique(String code, Long roleId);

    /**
     * 获取角色的菜单ID列表
     */
    List<Long> getMenuIds(Long roleId);
}
