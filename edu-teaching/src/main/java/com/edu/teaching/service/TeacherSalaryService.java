package com.edu.teaching.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.teaching.domain.entity.TeacherSalary;

import java.util.List;

/**
 * 教师课酬配置服务接口
 */
public interface TeacherSalaryService extends IService<TeacherSalary> {

    /**
     * 获取教师的课酬配置列表
     */
    List<TeacherSalary> getByTeacherId(Long teacherId);

    /**
     * 获取教师指定课程的课酬配置
     */
    TeacherSalary getByCourseId(Long teacherId, Long courseId);
}
