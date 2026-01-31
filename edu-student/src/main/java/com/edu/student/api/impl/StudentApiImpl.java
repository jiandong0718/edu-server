package com.edu.student.api.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.edu.student.api.StudentApi;
import com.edu.student.api.dto.StudentContactDTO;
import com.edu.student.api.dto.StudentDTO;
import com.edu.student.api.dto.StudentQueryDTO;
import com.edu.student.domain.entity.Student;
import com.edu.student.domain.entity.StudentContact;
import com.edu.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 学员API接口实现
 */
@Service
@RequiredArgsConstructor
public class StudentApiImpl implements StudentApi {

    private final StudentService studentService;

    @Override
    public StudentDTO getStudentById(Long id) {
        Student student = studentService.getDetail(id);
        return convertToDTO(student);
    }

    @Override
    public List<StudentDTO> getStudentsByIds(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        List<Student> students = studentService.listByIds(ids);
        return students.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean checkStudentExists(Long id) {
        return studentService.getById(id) != null;
    }

    @Override
    public List<StudentDTO> getStudentsByClassId(Long classId) {
        // TODO: 需要在教学模块实现班级学员关联后，通过关联表查询
        // 这里暂时返回空列表，实际应该查询 tch_class_student 关联表
        return Collections.emptyList();
    }

    @Override
    public List<StudentDTO> searchStudents(StudentQueryDTO query) {
        if (query == null) {
            return Collections.emptyList();
        }

        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(query.getName()), Student::getName, query.getName())
                .eq(StrUtil.isNotBlank(query.getStudentNo()), Student::getStudentNo, query.getStudentNo())
                .eq(StrUtil.isNotBlank(query.getPhone()), Student::getPhone, query.getPhone())
                .eq(StrUtil.isNotBlank(query.getStatus()), Student::getStatus, query.getStatus())
                .eq(query.getCampusId() != null, Student::getCampusId, query.getCampusId())
                .eq(query.getAdvisorId() != null, Student::getAdvisorId, query.getAdvisorId());

        List<Student> students = studentService.list(wrapper);
        return students.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public StudentDTO getStudentByNo(String studentNo) {
        if (StrUtil.isBlank(studentNo)) {
            return null;
        }
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Student::getStudentNo, studentNo);
        Student student = studentService.getOne(wrapper);
        return convertToDTO(student);
    }

    @Override
    public StudentDTO getStudentByPhone(String phone) {
        if (StrUtil.isBlank(phone)) {
            return null;
        }
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Student::getPhone, phone);
        Student student = studentService.getOne(wrapper);
        return convertToDTO(student);
    }

    @Override
    public List<StudentDTO> getStudentsByCampusId(Long campusId) {
        if (campusId == null) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Student::getCampusId, campusId);
        List<Student> students = studentService.list(wrapper);
        return students.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentDTO> getStudentsByAdvisorId(Long advisorId) {
        if (advisorId == null) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Student::getAdvisorId, advisorId);
        List<Student> students = studentService.list(wrapper);
        return students.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean updateStudentStatus(Long id, String status) {
        return studentService.updateStatus(id, status);
    }

    @Override
    public boolean checkPhoneExists(String phone, Long excludeId) {
        return studentService.checkPhoneExists(phone, excludeId);
    }

    /**
     * 转换为DTO
     */
    private StudentDTO convertToDTO(Student student) {
        if (student == null) {
            return null;
        }
        StudentDTO dto = new StudentDTO();
        BeanUtil.copyProperties(student, dto);

        // 转换联系人列表
        if (CollUtil.isNotEmpty(student.getContacts())) {
            List<StudentContactDTO> contactDTOs = student.getContacts().stream()
                    .map(this::convertContactToDTO)
                    .collect(Collectors.toList());
            dto.setContacts(contactDTOs);
        }

        return dto;
    }

    /**
     * 转换联系人为DTO
     */
    private StudentContactDTO convertContactToDTO(StudentContact contact) {
        if (contact == null) {
            return null;
        }
        StudentContactDTO dto = new StudentContactDTO();
        BeanUtil.copyProperties(contact, dto);
        return dto;
    }
}
