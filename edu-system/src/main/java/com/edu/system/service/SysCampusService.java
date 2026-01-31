package com.edu.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.system.domain.entity.SysCampus;

/**
 * 校区服务接口
 */
public interface SysCampusService extends IService<SysCampus> {

    /**
     * 检查校区编码是否唯一
     */
    boolean checkCodeUnique(String code, Long campusId);

    /**
     * 验证用户是否有权限访问指定校区
     * @param userId 用户ID
     * @param campusId 校区ID
     * @return 是否有权限
     */
    boolean validateUserCampusAccess(Long userId, Long campusId);

    /**
     * 获取校区信息（包含状态验证）
     * @param campusId 校区ID
     * @return 校区信息
     */
    SysCampus getActiveCampus(Long campusId);
}
