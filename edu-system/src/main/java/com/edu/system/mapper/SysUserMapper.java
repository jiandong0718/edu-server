package com.edu.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.system.domain.entity.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户 Mapper
 */
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户名查询用户（包含角色信息）
     */
    SysUser selectByUsername(@Param("username") String username);

    /**
     * 查询用户列表（包含角色和校区信息）
     */
    List<SysUser> selectUserList(@Param("user") SysUser user);

    /**
     * 根据用户ID查询角色编码列表
     */
    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查询权限标识列表
     */
    List<String> selectPermissionsByUserId(@Param("userId") Long userId);
}
