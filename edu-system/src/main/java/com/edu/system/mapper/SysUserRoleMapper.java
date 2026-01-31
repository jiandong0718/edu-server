package com.edu.system.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.system.domain.entity.SysUserRole;

/**
 * 用户角色关联 Mapper
 */
@DS("system")
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {
}
