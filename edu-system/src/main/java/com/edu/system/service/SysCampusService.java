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
}
