package com.edu.system.service;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.exception.BusinessException;
import com.edu.system.domain.entity.SysUser;
import com.edu.system.domain.entity.SysUserRole;
import com.edu.system.mapper.SysUserMapper;
import com.edu.system.mapper.SysUserRoleMapper;
import com.edu.system.service.impl.SysUserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 用户服务测试类
 * 测试用户创建、更新、删除、密码管理等核心功能
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("用户服务测试")
class SysUserServiceTest {

    @Mock
    private SysUserMapper userMapper;

    @Mock
    private SysUserRoleMapper userRoleMapper;

    @InjectMocks
    private SysUserServiceImpl userService;

    private SysUser testUser;

    @BeforeEach
    void setUp() {
        testUser = new SysUser();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("Test@123");
        testUser.setRealName("测试用户");
        testUser.setPhone("13800138000");
        testUser.setEmail("test@example.com");
        testUser.setStatus(1);
        testUser.setCampusId(1L);
        testUser.setRoleIds(Arrays.asList(1L, 2L));
    }

    @Test
    @DisplayName("测试根据用户名查询用户")
    void testGetByUsername() {
        // Given
        when(userMapper.selectByUsername("testuser")).thenReturn(testUser);

        // When
        SysUser result = userService.getByUsername("testuser");

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userMapper, times(1)).selectByUsername("testuser");
    }

    @Test
    @DisplayName("测试新增用户 - 成功场景")
    void testAddUser_Success() {
        // Given
        when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(userMapper.insert(any(SysUser.class))).thenReturn(1);
        when(userRoleMapper.insert(any(SysUserRole.class))).thenReturn(1);

        // When
        boolean result = userService.addUser(testUser);

        // Then
        assertTrue(result);
        verify(userMapper, times(1)).insert(any(SysUser.class));
        verify(userRoleMapper, times(2)).insert(any(SysUserRole.class)); // 2 roles

        // Verify password is encrypted
        ArgumentCaptor<SysUser> userCaptor = ArgumentCaptor.forClass(SysUser.class);
        verify(userMapper).insert(userCaptor.capture());
        SysUser savedUser = userCaptor.getValue();
        assertNotEquals("Test@123", savedUser.getPassword());
        assertTrue(BCrypt.checkpw("Test@123", savedUser.getPassword()));
    }

    @Test
    @DisplayName("测试新增用户 - 用户名已存在")
    void testAddUser_UsernameExists() {
        // Given
        when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.addUser(testUser);
        });
        assertEquals("用户名已存在", exception.getMessage());
        verify(userMapper, never()).insert(any(SysUser.class));
    }

    @Test
    @DisplayName("测试新增用户 - 密码强度不足（无大写字母）")
    void testAddUser_WeakPassword_NoUpperCase() {
        // Given
        testUser.setPassword("test@123");
        when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.addUser(testUser);
        });
        assertEquals("密码必须包含至少一个大写字母", exception.getMessage());
    }

    @Test
    @DisplayName("测试新增用户 - 密码强度不足（无小写字母）")
    void testAddUser_WeakPassword_NoLowerCase() {
        // Given
        testUser.setPassword("TEST@123");
        when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.addUser(testUser);
        });
        assertEquals("密码必须包含至少一个小写字母", exception.getMessage());
    }

    @Test
    @DisplayName("测试新增用户 - 密码强度不足（无数字）")
    void testAddUser_WeakPassword_NoDigit() {
        // Given
        testUser.setPassword("Test@abc");
        when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.addUser(testUser);
        });
        assertEquals("密码必须包含至少一个数字", exception.getMessage());
    }

    @Test
    @DisplayName("测试新增用户 - 密码长度不足")
    void testAddUser_WeakPassword_TooShort() {
        // Given
        testUser.setPassword("Test@12");
        when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.addUser(testUser);
        });
        assertEquals("密码长度至少为8位", exception.getMessage());
    }

    @Test
    @DisplayName("测试更新用户 - 成功场景")
    void testUpdateUser_Success() {
        // Given
        testUser.setRealName("更新后的姓名");
        when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(userMapper.updateById(any(SysUser.class))).thenReturn(1);
        when(userRoleMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(2);
        when(userRoleMapper.insert(any(SysUserRole.class))).thenReturn(1);

        // When
        boolean result = userService.updateUser(testUser);

        // Then
        assertTrue(result);
        verify(userMapper, times(1)).updateById(any(SysUser.class));
        verify(userRoleMapper, times(1)).delete(any(LambdaQueryWrapper.class));
        verify(userRoleMapper, times(2)).insert(any(SysUserRole.class));

        // Verify password is not updated
        ArgumentCaptor<SysUser> userCaptor = ArgumentCaptor.forClass(SysUser.class);
        verify(userMapper).updateById(userCaptor.capture());
        assertNull(userCaptor.getValue().getPassword());
    }

    @Test
    @DisplayName("测试更新用户 - 用户名已存在")
    void testUpdateUser_UsernameExists() {
        // Given
        when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.updateUser(testUser);
        });
        assertEquals("用户名已存在", exception.getMessage());
    }

    @Test
    @DisplayName("测试删除用户")
    void testDeleteUser() {
        // Given
        when(userRoleMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(2);
        when(userMapper.deleteById(1L)).thenReturn(1);

        // When
        boolean result = userService.deleteUser(1L);

        // Then
        assertTrue(result);
        verify(userRoleMapper, times(1)).delete(any(LambdaQueryWrapper.class));
        verify(userMapper, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("测试批量删除用户")
    void testDeleteUsers() {
        // Given
        List<Long> userIds = Arrays.asList(1L, 2L, 3L);
        when(userRoleMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(5);
        when(userMapper.deleteBatchIds(userIds)).thenReturn(3);

        // When
        boolean result = userService.deleteUsers(userIds);

        // Then
        assertTrue(result);
        verify(userRoleMapper, times(1)).delete(any(LambdaQueryWrapper.class));
        verify(userMapper, times(1)).deleteBatchIds(userIds);
    }

    @Test
    @DisplayName("测试重置密码")
    void testResetPassword() {
        // Given
        when(userMapper.updateById(any(SysUser.class))).thenReturn(1);

        // When
        boolean result = userService.resetPassword(1L, "NewPass@123");

        // Then
        assertTrue(result);
        ArgumentCaptor<SysUser> userCaptor = ArgumentCaptor.forClass(SysUser.class);
        verify(userMapper).updateById(userCaptor.capture());
        SysUser updatedUser = userCaptor.getValue();
        assertTrue(BCrypt.checkpw("NewPass@123", updatedUser.getPassword()));
    }

    @Test
    @DisplayName("测试修改密码 - 成功场景")
    void testChangePassword_Success() {
        // Given
        String hashedPassword = BCrypt.hashpw("OldPass@123");
        testUser.setPassword(hashedPassword);
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userMapper.updateById(any(SysUser.class))).thenReturn(1);

        // When
        boolean result = userService.changePassword(1L, "OldPass@123", "NewPass@456");

        // Then
        assertTrue(result);
        ArgumentCaptor<SysUser> userCaptor = ArgumentCaptor.forClass(SysUser.class);
        verify(userMapper).updateById(userCaptor.capture());
        SysUser updatedUser = userCaptor.getValue();
        assertTrue(BCrypt.checkpw("NewPass@456", updatedUser.getPassword()));
    }

    @Test
    @DisplayName("测试修改密码 - 用户不存在")
    void testChangePassword_UserNotFound() {
        // Given
        when(userMapper.selectById(1L)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.changePassword(1L, "OldPass@123", "NewPass@456");
        });
        assertEquals("用户不存在", exception.getMessage());
    }

    @Test
    @DisplayName("测试修改密码 - 原密码错误")
    void testChangePassword_WrongOldPassword() {
        // Given
        String hashedPassword = BCrypt.hashpw("OldPass@123");
        testUser.setPassword(hashedPassword);
        when(userMapper.selectById(1L)).thenReturn(testUser);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.changePassword(1L, "WrongPass@123", "NewPass@456");
        });
        assertEquals("原密码错误", exception.getMessage());
    }

    @Test
    @DisplayName("测试检查用户名唯一性 - 新增场景")
    void testCheckUsernameUnique_ForCreate() {
        // Given
        when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

        // When
        boolean result = userService.checkUsernameUnique("newuser", null);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("测试检查用户名唯一性 - 更新场景（用户名未变）")
    void testCheckUsernameUnique_ForUpdate_SameUsername() {
        // Given
        when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

        // When
        boolean result = userService.checkUsernameUnique("testuser", 1L);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("测试检查用户名唯一性 - 用户名已存在")
    void testCheckUsernameUnique_Exists() {
        // Given
        when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        // When
        boolean result = userService.checkUsernameUnique("existinguser", null);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("测试获取用户角色编码列表")
    void testGetRoleCodes() {
        // Given
        List<String> roleCodes = Arrays.asList("admin", "teacher");
        when(userMapper.selectRoleCodesByUserId(1L)).thenReturn(roleCodes);

        // When
        List<String> result = userService.getRoleCodes(1L);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("admin"));
        assertTrue(result.contains("teacher"));
    }

    @Test
    @DisplayName("测试获取用户权限标识列表")
    void testGetPermissions() {
        // Given
        List<String> permissions = Arrays.asList("system:user:add", "system:user:edit");
        when(userMapper.selectPermissionsByUserId(1L)).thenReturn(permissions);

        // When
        List<String> result = userService.getPermissions(1L);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("system:user:add"));
        assertTrue(result.contains("system:user:edit"));
    }

    @Test
    @DisplayName("测试分页查询用户列表")
    void testPageList() {
        // Given
        IPage<SysUser> page = new Page<>(1, 10);
        SysUser query = new SysUser();
        query.setUsername("test");
        List<SysUser> users = Arrays.asList(testUser);
        when(userMapper.selectUserList(query)).thenReturn(users);

        // When
        IPage<SysUser> result = userService.pageList(page, query);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
    }
}
