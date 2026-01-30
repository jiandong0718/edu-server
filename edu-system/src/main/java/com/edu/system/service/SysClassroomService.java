package com.edu.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.system.domain.entity.SysClassroom;

/**
 * 教室服务接口
 */
public interface SysClassroomService extends IService<SysClassroom> {

    /**
     * 检查教室名称是否唯一
     */
    boolean checkNameUnique(String name, Long campusId, Long id);
}
