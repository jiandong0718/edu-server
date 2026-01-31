package com.edu.student.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.student.domain.dto.StudentImportResultDTO;
import com.edu.student.domain.entity.Student;
import com.edu.student.domain.entity.StudentContact;

import java.util.List;

/**
 * 学员服务接口
 */
public interface StudentService extends IService<Student> {

    /**
     * 分页查询学员列表
     */
    IPage<Student> pageList(IPage<Student> page, Student query);

    /**
     * 获取学员详情
     */
    Student getDetail(Long id);

    /**
     * 新增学员
     */
    boolean addStudent(Student student);

    /**
     * 修改学员
     */
    boolean updateStudent(Student student);

    /**
     * 删除学员
     */
    boolean deleteStudent(Long id);

    /**
     * 批量删除学员
     */
    boolean deleteStudents(List<Long> ids);

    /**
     * 更新学员状态
     */
    boolean updateStatus(Long id, String status);

    /**
     * 获取学员联系人列表
     */
    List<StudentContact> getContacts(Long studentId);

    /**
     * 保存学员联系人
     */
    boolean saveContact(StudentContact contact);

    /**
     * 删除学员联系人
     */
    boolean deleteContact(Long contactId);

    /**
     * 设置学员标签
     */
    boolean setTags(Long studentId, List<Long> tagIds);

    /**
     * 检查手机号是否已存在
     */
    boolean checkPhoneExists(String phone, Long excludeId);

    /**
     * 导出学员数据到Excel
     */
    byte[] exportToExcel(Student query);

    /**
     * 批量导入学员数据
     */
    boolean importFromExcel(byte[] fileData);

    /**
     * 批量导入学员数据（增强版）
     *
     * @param fileData Excel文件数据
     * @return 导入结果
     */
    StudentImportResultDTO batchImportStudents(byte[] fileData);

    /**
     * 下载导入模板
     *
     * @return Excel模板数据
     */
    byte[] downloadImportTemplate();
}
