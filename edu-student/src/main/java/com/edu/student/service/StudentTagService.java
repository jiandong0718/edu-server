package com.edu.student.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.student.domain.entity.StudentTag;

import java.util.List;

/**
 * 学员标签服务接口
 *
 * @author edu
 * @since 2024-01-30
 */
public interface StudentTagService extends IService<StudentTag> {

    /**
     * 分页查询标签并补充使用次数
     */
    Page<StudentTag> pageWithUsage(Page<StudentTag> page, LambdaQueryWrapper<StudentTag> wrapper);

    /**
     * 查询标签列表并补充使用次数
     */
    List<StudentTag> listWithUsage(LambdaQueryWrapper<StudentTag> wrapper);

    /**
     * 查询标签详情并补充使用次数
     */
    StudentTag getByIdWithUsage(Long id);
}
