package com.edu.teaching.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.teaching.domain.entity.TeachClass;

import java.util.List;

/**
 * 班级服务接口
 */
public interface TeachClassService extends IService<TeachClass> {

    /**
     * 分页查询班级列表
     */
    IPage<TeachClass> pageList(IPage<TeachClass> page, TeachClass query);

    /**
     * 开班
     */
    boolean start(Long id);

    /**
     * 结班
     */
    boolean finish(Long id);

    /**
     * 取消班级
     */
    boolean cancel(Long id);

    /**
     * 学员分班
     */
    boolean addStudents(Long classId, List<Long> studentIds);

    /**
     * 学员退班
     */
    boolean removeStudent(Long classId, Long studentId);
}
