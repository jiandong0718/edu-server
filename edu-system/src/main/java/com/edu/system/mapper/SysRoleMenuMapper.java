package com.edu.system.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.system.domain.entity.SysRoleMenu;

/**
 * 角色菜单关联 Mapper
 */
@DS("system")
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {
}
