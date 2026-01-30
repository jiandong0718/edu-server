package com.edu.student.api;

import com.edu.student.domain.entity.Student;

import java.util.List;

/**
 * 学员API接口（供其他模块调用）
 */
public interface StudentApi {

    /**
     * 根据ID获取学员信息
     */
    Student getById(Long id);

    /**
     * 根据学员编号获取学员信息
     */
    Student getByStudentNo(String studentNo);

    /**
     * 根据手机号获取学员信息
     */
    Student getByPhone(String phone);

    /**
     * 批量获取学员信息
     */
    List<Student> getByIds(List<Long> ids);

    /**
     * 获取指定校区的学员列表
     */
    List<Student> getByCampusId(Long campusId);

    /**
     * 获取指定顾问的学员列表
     */
    List<Student> getByAdvisorId(Long advisorId);

    /**
     * 检查学员是否存在
     */
    boolean exists(Long id);

    /**
     * 更新学员状态
     */
    boolean updateStatus(Long id, String status);
}
