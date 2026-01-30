package com.edu.teaching.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.teaching.domain.entity.TeacherCertificate;

import java.util.List;

/**
 * 教师资质证书服务接口
 */
public interface TeacherCertificateService extends IService<TeacherCertificate> {

    /**
     * 获取教师的证书列表
     */
    List<TeacherCertificate> getByTeacherId(Long teacherId);
}
