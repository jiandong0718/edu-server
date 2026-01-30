package com.edu.student.api.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.edu.student.api.StudentApi;
import com.edu.student.domain.entity.Student;
import com.edu.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 学员API接口实现
 */
@Service
@RequiredArgsConstructor
public class StudentApiImpl implements StudentApi {

    private final StudentService studentService;

    @Override
    public Student getById(Long id) {
        return studentService.getById(id);
    }

    @Override
    public Student getByStudentNo(String studentNo) {
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Student::getStudentNo, studentNo);
        return studentService.getOne(wrapper);
    }

    @Override
    public Student getByPhone(String phone) {
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Student::getPhone, phone);
        return studentService.getOne(wrapper);
    }

    @Override
    public List<Student> getByIds(List<Long> ids) {
        return studentService.listByIds(ids);
    }

    @Override
    public List<Student> getByCampusId(Long campusId) {
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Student::getCampusId, campusId);
        return studentService.list(wrapper);
    }

    @Override
    public List<Student> getByAdvisorId(Long advisorId) {
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Student::getAdvisorId, advisorId);
        return studentService.list(wrapper);
    }

    @Override
    public boolean exists(Long id) {
        return studentService.getById(id) != null;
    }

    @Override
    public boolean updateStatus(Long id, String status) {
        return studentService.updateStatus(id, status);
    }
}
