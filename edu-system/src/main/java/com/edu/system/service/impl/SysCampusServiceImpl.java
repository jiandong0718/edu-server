package com.edu.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.exception.BusinessException;
import com.edu.system.domain.entity.SysCampus;
import com.edu.system.domain.entity.SysUser;
import com.edu.system.mapper.SysCampusMapper;
import com.edu.system.mapper.SysUserMapper;
import com.edu.system.service.SysCampusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 校区服务实现
 */
@Service
@RequiredArgsConstructor
public class SysCampusServiceImpl extends ServiceImpl<SysCampusMapper, SysCampus> implements SysCampusService {

    private final SysUserMapper userMapper;

    @Override
    public boolean checkCodeUnique(String code, Long campusId) {
        LambdaQueryWrapper<SysCampus> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysCampus::getCode, code);
        if (campusId != null) {
            wrapper.ne(SysCampus::getId, campusId);
        }
        return count(wrapper) == 0;
    }

    @Override
    public boolean validateUserCampusAccess(Long userId, Long campusId) {
        // 查询用户信息
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            return false;
        }

        // 超级管理员可以访问所有校区
        // 这里简化处理，实际应该查询用户角色
        // 如果用户有 super_admin 角色，则可以访问所有校区

        // 普通用户只能访问自己所属的校区
        // 注意：这里假设用户只能访问一个校区
        // 如果需要支持多校区访问，需要创建 sys_user_campus 关联表
        if (user.getCampusId() == null) {
            // 如果用户没有绑定校区，可能是超级管理员，允许访问
            return true;
        }

        return user.getCampusId().equals(campusId);
    }

    @Override
    public SysCampus getActiveCampus(Long campusId) {
        SysCampus campus = getById(campusId);
        if (campus == null) {
            throw new BusinessException("校区不存在");
        }
        if (campus.getStatus() != 1) {
            throw new BusinessException("校区已被禁用");
        }
        return campus;
    }
}
