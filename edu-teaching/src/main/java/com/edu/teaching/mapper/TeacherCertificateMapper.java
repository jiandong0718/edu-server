package com.edu.teaching.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.teaching.domain.entity.TeacherCertificate;
import org.apache.ibatis.annotations.Mapper;

/**
 * 教师资质证书 Mapper
 */
@Mapper
public interface TeacherCertificateMapper extends BaseMapper<TeacherCertificate> {
}
