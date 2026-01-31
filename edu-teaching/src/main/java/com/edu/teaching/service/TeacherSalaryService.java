package com.edu.teaching.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.teaching.domain.dto.TeacherSalaryDTO;
import com.edu.teaching.domain.entity.TeacherSalary;
import com.edu.teaching.domain.vo.TeacherSalaryVO;

import java.util.List;

/**
 * 教师课酬配置服务接口
 */
public interface TeacherSalaryService extends IService<TeacherSalary> {

    /**
     * 分页查询课酬配置列表
     */
    Page<TeacherSalaryVO> pageQuery(Integer pageNum, Integer pageSize, Long teacherId, Long courseId, String classType, Long campusId);

    /**
     * 获取教师的课酬配置列表
     */
    List<TeacherSalaryVO> getByTeacherId(Long teacherId);

    /**
     * 获取教师当前有效的课酬配置列表
     */
    List<TeacherSalaryVO> getCurrentValidSalaries(Long teacherId);

    /**
     * 获取课酬配置详情
     */
    TeacherSalaryVO getDetailById(Long id);

    /**
     * 获取教师指定课程和班级类型的课酬配置
     */
    TeacherSalaryVO getEffectiveSalary(Long teacherId, Long courseId, String classType);

    /**
     * 新增课酬配置
     */
    Long addSalary(TeacherSalaryDTO dto);

    /**
     * 修改课酬配置
     */
    boolean updateSalary(TeacherSalaryDTO dto);

    /**
     * 删除课酬配置
     */
    boolean deleteSalary(Long id);

    /**
     * 批量删除课酬配置
     */
    boolean deleteBatch(List<Long> ids);

    /**
     * 获取课酬历史记录
     */
    List<TeacherSalaryVO> getSalaryHistory(Long teacherId, Long courseId, String classType);
}
