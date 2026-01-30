package com.edu.teaching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.teaching.domain.entity.TeacherSalary;
import com.edu.teaching.mapper.TeacherSalaryMapper;
import com.edu.teaching.service.TeacherSalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * 教师课酬配置服务实现
 */
@Service
@RequiredArgsConstructor
public class TeacherSalaryServiceImpl extends ServiceImpl<TeacherSalaryMapper, TeacherSalary> implements TeacherSalaryService {

    @Override
    public List<TeacherSalary> getByTeacherId(Long teacherId) {
        LambdaQueryWrapper<TeacherSalary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherSalary::getTeacherId, teacherId)
                .eq(TeacherSalary::getStatus, 1)
                .orderByDesc(TeacherSalary::getEffectiveDate);
        return list(wrapper);
    }

    @Override
    public TeacherSalary getByCourseId(Long teacherId, Long courseId) {
        LambdaQueryWrapper<TeacherSalary> wrapper = new LambdaQueryWrapper<>();
        LocalDate now = LocalDate.now();
        wrapper.eq(TeacherSalary::getTeacherId, teacherId)
                .eq(courseId != null, TeacherSalary::getCourseId, courseId)
                .eq(TeacherSalary::getStatus, 1)
                .le(TeacherSalary::getEffectiveDate, now)
                .and(w -> w.isNull(TeacherSalary::getExpiryDate)
                        .or()
                        .ge(TeacherSalary::getExpiryDate, now))
                .orderByDesc(TeacherSalary::getEffectiveDate)
                .last("LIMIT 1");
        return getOne(wrapper);
    }
}
