package com.edu.teaching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.teaching.domain.entity.Teacher;
import com.edu.teaching.mapper.TeacherMapper;
import com.edu.teaching.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 教师服务实现
 */
@Service
@RequiredArgsConstructor
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {

    @Override
    public boolean checkTeacherNoUnique(String teacherNo, Long id) {
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teacher::getTeacherNo, teacherNo);
        if (id != null) {
            wrapper.ne(Teacher::getId, id);
        }
        return count(wrapper) == 0;
    }

    @Override
    public Teacher getByUserId(Long userId) {
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teacher::getUserId, userId);
        return getOne(wrapper);
    }

    @Override
    public boolean updateStatus(Long id, String status) {
        Teacher teacher = new Teacher();
        teacher.setId(id);
        teacher.setStatus(status);
        return updateById(teacher);
    }

    @Override
    public List<Teacher> getTeachersByCampusId(Long campusId) {
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teacher::getCampusId, campusId)
                .eq(Teacher::getStatus, "active")
                .orderByAsc(Teacher::getTeacherNo);
        return list(wrapper);
    }
}
