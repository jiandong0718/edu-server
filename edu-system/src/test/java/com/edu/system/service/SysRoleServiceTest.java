package com.edu.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.edu.common.exception.BusinessException;
import com.edu.system.domain.entity.SysRole;
import com.edu.system.domain.entity.SysRoleMenu;
import com.edu.system.mapper.SysMenuMapper;
import com.edu.system.mapper.SysRoleMapper;
import com.edu.system.mapper.SysRoleMenuMapper;
import com.edu.system.service.impl.SysRoleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 角色服务测试类
 * 测试角色权限分配、数据权限配置等核心功能
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("角色服务测试")
class SysRoleServiceTest {

    @Mock
    private SysRoleMapper roleMapper;

    @Mock
    private SysRoleMenuMapper roleMenuMapper;

    @Mock
    private SysMenuMapper menuMapper;

    @InjectMocks
    private SysRoleServiceImpl roleService;

    private SysRole testRole;

    @BeforeEach
    void setUp() {
        testRole = new SysRole();
        testRole.setId(1L);
        testRole.setName("测试角色");
        testRole.setCode("test_role");
        testRole.setDataScope(2); // 本校区
        testRole.setStatus(1);
        testRole.setSortOrder(1);
        testRole.setMenuIds(Arrays.asList(1L, 2L, 3L));
    }

    @Test
    @DisplayName("测试新增角色 - 成功场景")
    void testAddRole_Success() {
        // Given
        when(roleMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(roleMapper.insert(any(SysRole.class))).thenReturn(1);
        when(roleMenuMapper.insert(any(SysRoleMenu.class))).thenReturn(1);

        // When
        boolean result = roleService.addRole(testRole);

        // Then
        assertTrue(result);
        verify(roleMapper, times(1)).insert(any(SysRole.class));
        verify(roleMenuMapper, times(3)).insert(any(SysRoleMenu.class)); // 3 menus
    }

    @Test
    @DisplayName("测试新增角色 - 角色编码已存在")
    void testAddRole_CodeExists() {
        // Given
        when(roleMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            roleService.addRole(testRole);
        });
        assertEquals("角色编码已存在", exception.getMessage());
        verify(roleMapper, never()).insert(any(SysRole.class));
    }

    @Test
    @DisplayName("测试新增角色 - 无菜单权限")
    void testAddRole_NoMenus() {
        // Given
        testRole.setMenuIds(null);
        when(roleMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(roleMapper.insert(any(SysRole.class))).thenReturn(1);

        // When
        boolean result = roleService.addRole(testRole);

        // Then
        assertTrue(result);
        verify(roleMapper, times(1)).insert(any(SysRole.class));
        verify(roleMenuMapper, never()).insert(any(SysRoleMenu.class));
    }

    @Test
    @DisplayName("测试更新角色 - 成功场景")
    void testUpdateRole_Success() {
        // Given
        testRole.setName("更新后的角色名");
        testRole.setMenuIds(Arrays.asList(1L, 2L, 3L, 4L)); // 新增一个菜单
        when(roleMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(roleMapper.updateById(any(SysRole.class))).thenReturn(1);
        when(roleMenuMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(3);
        when(roleMenuMapper.insert(any(SysRoleMenu.class))).thenReturn(1);

        // When
        boolean result = roleService.updateRole(testRole);

        // Then
        assertTrue(result);
        verify(roleMapper, times(1)).updateById(any(SysRole.class));
        verify(roleMenuMapper, times(1)).delete(any(LambdaQueryWrapper.class));
        verify(roleMenuMapper, times(4)).insert(any(SysRoleMenu.class)); // 4 menus
    }

    @Test
    @DisplayName("测试更新角色 - 角色编码已存在")
    void testUpdateRole_CodeExists() {
        // Given
        when(roleMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            roleService.updateRole(testRole);
        });
        assertEquals("角色编码已存在", exception.getMessage());
        verify(roleMapper, never()).updateById(any(SysRole.class));
    }

    @Test
    @DisplayName("测试更新角色 - 清空菜单权限")
    void testUpdateRole_ClearMenus() {
        // Given
        testRole.setMenuIds(Arrays.asList()); // 空列表
        when(roleMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(roleMapper.updateById(any(SysRole.class))).thenReturn(1);
        when(roleMenuMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(3);

        // When
        boolean result = roleService.updateRole(testRole);

        // Then
        assertTrue(result);
        verify(roleMapper, times(1)).updateById(any(SysRole.class));
        verify(roleMenuMapper, times(1)).delete(any(LambdaQueryWrapper.class));
        verify(roleMenuMapper, never()).insert(any(SysRoleMenu.class));
    }

    @Test
    @DisplayName("测试删除角色")
    void testDeleteRole() {
        // Given
        when(roleMenuMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(3);
        when(roleMapper.deleteById(1L)).thenReturn(1);

        // When
        boolean result = roleService.deleteRole(1L);

        // Then
        assertTrue(result);
        verify(roleMenuMapper, times(1)).delete(any(LambdaQueryWrapper.class));
        verify(roleMapper, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("测试批量删除角色")
    void testDeleteRoles() {
        // Given
        List<Long> roleIds = Arrays.asList(1L, 2L, 3L);
        when(roleMenuMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(9);
        when(roleMapper.deleteBatchIds(roleIds)).thenReturn(3);

        // When
        boolean result = roleService.deleteRoles(roleIds);

        // Then
        assertTrue(result);
        verify(roleMenuMapper, times(1)).delete(any(LambdaQueryWrapper.class));
        verify(roleMapper, times(1)).deleteBatchIds(roleIds);
    }

    @Test
    @DisplayName("测试检查角色编码唯一性 - 新增场景")
    void testCheckCodeUnique_ForCreate() {
        // Given
        when(roleMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

        // When
        boolean result = roleService.checkCodeUnique("new_role", null);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("测试检查角色编码唯一性 - 更新场景（编码未变）")
    void testCheckCodeUnique_ForUpdate_SameCode() {
        // Given
        when(roleMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

        // When
        boolean result = roleService.checkCodeUnique("test_role", 1L);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("测试检查角色编码唯一性 - 编码已存在")
    void testCheckCodeUnique_Exists() {
        // Given
        when(roleMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        // When
        boolean result = roleService.checkCodeUnique("existing_role", null);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("测试获取角色的菜单ID列表")
    void testGetMenuIds() {
        // Given
        List<Long> menuIds = Arrays.asList(1L, 2L, 3L, 4L, 5L);
        when(menuMapper.selectMenuIdsByRoleId(1L)).thenReturn(menuIds);

        // When
        List<Long> result = roleService.getMenuIds(1L);

        // Then
        assertNotNull(result);
        assertEquals(5, result.size());
        assertTrue(result.contains(1L));
        assertTrue(result.contains(5L));
    }

    @Test
    @DisplayName("测试数据权限配置 - 全部数据")
    void testDataScope_All() {
        // Given
        testRole.setDataScope(1); // 全部数据
        when(roleMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(roleMapper.insert(any(SysRole.class))).thenReturn(1);

        // When
        boolean result = roleService.addRole(testRole);

        // Then
        assertTrue(result);
        assertEquals(1, testRole.getDataScope());
    }

    @Test
    @DisplayName("测试数据权限配置 - 本校区数据")
    void testDataScope_Campus() {
        // Given
        testRole.setDataScope(2); // 本校区
        when(roleMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(roleMapper.insert(any(SysRole.class))).thenReturn(1);

        // When
        boolean result = roleService.addRole(testRole);

        // Then
        assertTrue(result);
        assertEquals(2, testRole.getDataScope());
    }

    @Test
    @DisplayName("测试数据权限配置 - 本人数据")
    void testDataScope_Self() {
        // Given
        testRole.setDataScope(3); // 本人
        when(roleMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(roleMapper.insert(any(SysRole.class))).thenReturn(1);

        // When
        boolean result = roleService.addRole(testRole);

        // Then
        assertTrue(result);
        assertEquals(3, testRole.getDataScope());
    }

    @Test
    @DisplayName("测试角色状态管理 - 启用")
    void testRoleStatus_Enabled() {
        // Given
        testRole.setStatus(1);
        when(roleMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(roleMapper.insert(any(SysRole.class))).thenReturn(1);

        // When
        boolean result = roleService.addRole(testRole);

        // Then
        assertTrue(result);
        assertEquals(1, testRole.getStatus());
    }

    @Test
    @DisplayName("测试角色状态管理 - 禁用")
    void testRoleStatus_Disabled() {
        // Given
        testRole.setStatus(0);
        when(roleMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(roleMapper.insert(any(SysRole.class))).thenReturn(1);

        // When
        boolean result = roleService.addRole(testRole);

        // Then
        assertTrue(result);
        assertEquals(0, testRole.getStatus());
    }
}
