package com.edu.finance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.finance.domain.entity.ClassHourRule;

import java.math.BigDecimal;

/**
 * 课时消课规则服务接口
 */
public interface ClassHourRuleService extends IService<ClassHourRule> {

    /**
     * 根据课程和班级类型获取消课规则
     *
     * @param courseId  课程ID
     * @param classType 班级类型
     * @param campusId  校区ID
     * @return 消课规则
     */
    ClassHourRule getRule(Long courseId, String classType, Long campusId);

    /**
     * 计算应扣减的课时数
     *
     * @param courseId   课程ID
     * @param classType  班级类型
     * @param campusId   校区ID
     * @param classHours 实际课时数
     * @return 应扣减的课时数
     */
    BigDecimal calculateDeductHours(Long courseId, String classType, Long campusId, Integer classHours);
}
