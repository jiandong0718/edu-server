package com.edu.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.system.domain.entity.SysUser;

import java.util.List;

/**
 * 用户服务接口
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 根据用户名查询用户
     */
    SysUser getByUsername(String username);

    /**
     * 分页查询用户列表
     */
    IPage<SysUser> pageList(IPage<SysUser> page, SysUser user);

    /**
     * 新增用户
     */
    boolean addUser(SysUser user);

    /**
     * 修改用户
     */
    boolean updateUser(SysUser user);

    /**
     * 删除用户
     */
    boolean deleteUser(Long userId);

    /**
     * 批量删除用户
     */
    boolean deleteUsers(List<Long> userIds);

    /**
     * 重置密码
     */
    boolean resetPassword(Long userId, String newPassword);

    /**
     * 修改密码
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 获取用户角色编码列表
     */
    List<String> getRoleCodes(Long userId);

    /**
     * 获取用户权限标识列表
     */
    List<String> getPermissions(Long userId);

    /**
     * 检查用户名是否唯一
     */
    boolean checkUsernameUnique(String username, Long userId);
}
