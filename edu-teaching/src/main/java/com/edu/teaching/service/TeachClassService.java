package com.edu.teaching.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.teaching.domain.dto.BatchClassGraduationDTO;
import com.edu.teaching.domain.dto.BatchClassPromotionDTO;
import com.edu.teaching.domain.dto.ClassGraduationDTO;
import com.edu.teaching.domain.dto.ClassPromotionDTO;
import com.edu.teaching.domain.entity.TeachClass;
import com.edu.teaching.domain.vo.BatchClassGraduationResultVO;
import com.edu.teaching.domain.vo.BatchClassPromotionResultVO;
import com.edu.teaching.domain.vo.ClassGraduationResultVO;
import com.edu.teaching.domain.vo.ClassPromotionResultVO;

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

    /**
     * 班级升班
     */
    ClassPromotionResultVO promoteClass(ClassPromotionDTO dto);

    /**
     * 批量班级升班
     */
    BatchClassPromotionResultVO batchPromoteClass(BatchClassPromotionDTO dto);

    /**
     * 班级结业
     */
    ClassGraduationResultVO graduateClass(ClassGraduationDTO dto);

    /**
     * 批量班级结业
     */
    BatchClassGraduationResultVO batchGraduateClass(BatchClassGraduationDTO dto);
}
