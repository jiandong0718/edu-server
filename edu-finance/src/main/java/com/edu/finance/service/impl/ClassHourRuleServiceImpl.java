package com.edu.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.finance.domain.entity.ClassHourRule;
import com.edu.finance.mapper.ClassHourRuleMapper;
import com.edu.finance.service.ClassHourRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 课时消课规则服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClassHourRuleServiceImpl extends ServiceImpl<ClassHourRuleMapper, ClassHourRule> implements ClassHourRuleService {

    @Override
    public ClassHourRule getRule(Long courseId, String classType, Long campusId) {
        // 优先级：课程+班级类型+校区 > 课程+班级类型 > 班级类型+校区 > 班级类型 > 默认规则

        // 1. 查找课程+班级类型+校区的规则
        if (courseId != null && classType != null && campusId != null) {
            ClassHourRule rule = getOne(new LambdaQueryWrapper<ClassHourRule>()
                    .eq(ClassHourRule::getCourseId, courseId)
                    .eq(ClassHourRule::getClassType, classType)
                    .eq(ClassHourRule::getCampusId, campusId)
                    .eq(ClassHourRule::getStatus, "active")
                    .last("LIMIT 1"));
            if (rule != null) {
                return rule;
            }
        }

        // 2. 查找课程+班级类型的规则
        if (courseId != null && classType != null) {
            ClassHourRule rule = getOne(new LambdaQueryWrapper<ClassHourRule>()
                    .eq(ClassHourRule::getCourseId, courseId)
                    .eq(ClassHourRule::getClassType, classType)
                    .isNull(ClassHourRule::getCampusId)
                    .eq(ClassHourRule::getStatus, "active")
                    .last("LIMIT 1"));
            if (rule != null) {
                return rule;
            }
        }

        // 3. 查找班级类型+校区的规则
        if (classType != null && campusId != null) {
            ClassHourRule rule = getOne(new LambdaQueryWrapper<ClassHourRule>()
                    .isNull(ClassHourRule::getCourseId)
                    .eq(ClassHourRule::getClassType, classType)
                    .eq(ClassHourRule::getCampusId, campusId)
                    .eq(ClassHourRule::getStatus, "active")
                    .last("LIMIT 1"));
            if (rule != null) {
                return rule;
            }
        }

        // 4. 查找班级类型的规则
        if (classType != null) {
            ClassHourRule rule = getOne(new LambdaQueryWrapper<ClassHourRule>()
                    .isNull(ClassHourRule::getCourseId)
                    .eq(ClassHourRule::getClassType, classType)
                    .isNull(ClassHourRule::getCampusId)
                    .eq(ClassHourRule::getStatus, "active")
                    .last("LIMIT 1"));
            if (rule != null) {
                return rule;
            }
        }

        // 5. 返回默认规则
        ClassHourRule defaultRule = getOne(new LambdaQueryWrapper<ClassHourRule>()
                .isNull(ClassHourRule::getCourseId)
                .isNull(ClassHourRule::getClassType)
                .isNull(ClassHourRule::getCampusId)
                .eq(ClassHourRule::getStatus, "active")
                .last("LIMIT 1"));

        if (defaultRule == null) {
            // 如果没有默认规则，创建一个临时的
            defaultRule = new ClassHourRule();
            defaultRule.setName("默认规则");
            defaultRule.setDeductType("per_hour");
            defaultRule.setDeductAmount(BigDecimal.ONE);
            log.warn("未找到消课规则，使用临时默认规则");
        }

        return defaultRule;
    }

    @Override
    public BigDecimal calculateDeductHours(Long courseId, String classType, Long campusId, Integer classHours) {
        ClassHourRule rule = getRule(courseId, classType, campusId);

        if (rule == null) {
            // 默认按实际课时扣减
            return BigDecimal.valueOf(classHours != null ? classHours : 1);
        }

        switch (rule.getDeductType()) {
            case "per_hour":
                // 按课时扣减：实际课时 * 扣减系数
                int hours = classHours != null ? classHours : 1;
                return rule.getDeductAmount().multiply(BigDecimal.valueOf(hours));

            case "per_class":
                // 按课次扣减：固定扣减数量
                return rule.getDeductAmount();

            case "custom":
                // 自定义扣减：使用配置的扣减数量
                return rule.getDeductAmount();

            default:
                // 默认按实际课时扣减
                return BigDecimal.valueOf(classHours != null ? classHours : 1);
        }
    }
}
