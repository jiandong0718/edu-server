package com.edu.teaching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.teaching.domain.entity.TeacherCertificate;
import com.edu.teaching.mapper.TeacherCertificateMapper;
import com.edu.teaching.service.TeacherCertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 教师资质证书服务实现
 */
@Service
@RequiredArgsConstructor
public class TeacherCertificateServiceImpl extends ServiceImpl<TeacherCertificateMapper, TeacherCertificate> implements TeacherCertificateService {

    @Override
    public List<TeacherCertificate> getByTeacherId(Long teacherId) {
        LambdaQueryWrapper<TeacherCertificate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherCertificate::getTeacherId, teacherId)
                .orderByDesc(TeacherCertificate::getIssueDate);
        return list(wrapper);
    }
}
