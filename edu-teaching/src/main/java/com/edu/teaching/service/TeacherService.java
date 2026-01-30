package com.edu.teaching.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.teaching.domain.entity.Teacher;

import java.util.List;

/**
 * 教师服务接口
 */
public interface TeacherService extends IService<Teacher> {

    /**
     * 检查教师编号是否唯一
     */
    boolean checkTeacherNoUnique(String teacherNo, Long id);

    /**
     * 根据用户ID获取教师信息
     */
    Teacher getByUserId(Long userId);

    /**
     * 更新教师状态
     */
    boolean updateStatus(Long id, String status);

    /**
     * 获取指定校区的教师列表
     */
    List<Teacher> getTeachersByCampusId(Long campusId);
}
