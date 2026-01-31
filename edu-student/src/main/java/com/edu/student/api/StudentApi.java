package com.edu.student.api;

import com.edu.student.api.dto.StudentDTO;
import com.edu.student.api.dto.StudentQueryDTO;

import java.util.List;

/**
 * 学员API接口（供其他模块调用）
 */
public interface StudentApi {

    /**
     * 根据ID获取学员信息
     *
     * @param id 学员ID
     * @return 学员信息
     */
    StudentDTO getStudentById(Long id);

    /**
     * 批量获取学员信息
     *
     * @param ids 学员ID列表
     * @return 学员信息列表
     */
    List<StudentDTO> getStudentsByIds(List<Long> ids);

    /**
     * 检查学员是否存在
     *
     * @param id 学员ID
     * @return 是否存在
     */
    boolean checkStudentExists(Long id);

    /**
     * 获取班级的学员列表
     *
     * @param classId 班级ID
     * @return 学员列表
     */
    List<StudentDTO> getStudentsByClassId(Long classId);

    /**
     * 搜索学员
     *
     * @param query 查询条件
     * @return 学员列表
     */
    List<StudentDTO> searchStudents(StudentQueryDTO query);

    /**
     * 根据学员编号获取学员信息
     *
     * @param studentNo 学员编号
     * @return 学员信息
     */
    StudentDTO getStudentByNo(String studentNo);

    /**
     * 根据手机号获取学员信息
     *
     * @param phone 手机号
     * @return 学员信息
     */
    StudentDTO getStudentByPhone(String phone);

    /**
     * 获取指定校区的学员列表
     *
     * @param campusId 校区ID
     * @return 学员列表
     */
    List<StudentDTO> getStudentsByCampusId(Long campusId);

    /**
     * 获取指定顾问的学员列表
     *
     * @param advisorId 顾问ID
     * @return 学员列表
     */
    List<StudentDTO> getStudentsByAdvisorId(Long advisorId);

    /**
     * 更新学员状态
     *
     * @param id     学员ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateStudentStatus(Long id, String status);

    /**
     * 验证学员手机号是否已存在
     *
     * @param phone     手机号
     * @param excludeId 排除的学员ID（用于编辑时排除自己）
     * @return 是否存在
     */
    boolean checkPhoneExists(String phone, Long excludeId);
}
