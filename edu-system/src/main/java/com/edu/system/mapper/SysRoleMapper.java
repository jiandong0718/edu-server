package com.edu.system.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.system.domain.entity.SysRole;

/**
 * 角色 Mapper
 */
@DS("system")
public interface SysRoleMapper extends BaseMapper<SysRole> {
}
