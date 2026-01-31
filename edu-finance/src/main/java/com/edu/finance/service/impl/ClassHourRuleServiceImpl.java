package com.edu.finance.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.exception.BusinessException;
import com.edu.finance.domain.dto.ClassHourRuleCreateDTO;
import com.edu.finance.domain.dto.ClassHourRuleQueryDTO;
import com.edu.finance.domain.dto.ClassHourRuleUpdateDTO;
import com.edu.finance.domain.entity.ClassHourRule;
import com.edu.finance.domain.vo.ClassHourRuleVO;
import com.edu.finance.mapper.ClassHourRuleMapper;
import com.edu.finance.service.ClassHourRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createRule(ClassHourRuleCreateDTO dto) {
        // 检查是否已存在相同的规则
        LambdaQueryWrapper<ClassHourRule> wrapper = new LambdaQueryWrapper<>();

        if (dto.getCourseId() != null) {
            wrapper.eq(ClassHourRule::getCourseId, dto.getCourseId());
        } else {
            wrapper.isNull(ClassHourRule::getCourseId);
        }

        if (StrUtil.isNotBlank(dto.getClassType())) {
            wrapper.eq(ClassHourRule::getClassType, dto.getClassType());
        } else {
            wrapper.isNull(ClassHourRule::getClassType);
        }

        if (dto.getCampusId() != null) {
            wrapper.eq(ClassHourRule::getCampusId, dto.getCampusId());
        } else {
            wrapper.isNull(ClassHourRule::getCampusId);
        }

        ClassHourRule existingRule = getOne(wrapper);
        if (existingRule != null) {
            throw new BusinessException("已存在相同条件的消课规则");
        }

        // 创建规则
        ClassHourRule rule = BeanUtil.copyProperties(dto, ClassHourRule.class);
        if (StrUtil.isBlank(rule.getStatus())) {
            rule.setStatus("active");
        }

        boolean result = save(rule);

        if (result) {
            log.info("创建消课规则成功: name={}, courseId={}, classType={}, campusId={}",
                    dto.getName(), dto.getCourseId(), dto.getClassType(), dto.getCampusId());
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRule(ClassHourRuleUpdateDTO dto) {
        ClassHourRule rule = getById(dto.getId());
        if (rule == null) {
            throw new BusinessException("消课规则不存在");
        }

        // 更新规则
        BeanUtil.copyProperties(dto, rule, "id");
        boolean result = updateById(rule);

        if (result) {
            log.info("更新消课规则成功: id={}, name={}", dto.getId(), dto.getName());
        }

        return result;
    }

    @Override
    public IPage<ClassHourRuleVO> pageQuery(Page<ClassHourRule> page, ClassHourRuleQueryDTO query) {
        LambdaQueryWrapper<ClassHourRule> wrapper = new LambdaQueryWrapper<>();

        if (StrUtil.isNotBlank(query.getName())) {
            wrapper.like(ClassHourRule::getName, query.getName());
        }
        if (query.getCourseId() != null) {
            wrapper.eq(ClassHourRule::getCourseId, query.getCourseId());
        }
        if (StrUtil.isNotBlank(query.getClassType())) {
            wrapper.eq(ClassHourRule::getClassType, query.getClassType());
        }
        if (StrUtil.isNotBlank(query.getDeductType())) {
            wrapper.eq(ClassHourRule::getDeductType, query.getDeductType());
        }
        if (StrUtil.isNotBlank(query.getStatus())) {
            wrapper.eq(ClassHourRule::getStatus, query.getStatus());
        }
        if (query.getCampusId() != null) {
            wrapper.eq(ClassHourRule::getCampusId, query.getCampusId());
        }

        wrapper.orderByDesc(ClassHourRule::getCreateTime);

        IPage<ClassHourRule> rulePage = page(page, wrapper);

        // 转换为VO
        IPage<ClassHourRuleVO> voPage = new Page<>(rulePage.getCurrent(), rulePage.getSize(), rulePage.getTotal());
        List<ClassHourRuleVO> voList = new ArrayList<>();

        for (ClassHourRule rule : rulePage.getRecords()) {
            ClassHourRuleVO vo = convertToVO(rule);
            voList.add(vo);
        }

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public ClassHourRuleVO getDetailById(Long id) {
        ClassHourRule rule = getById(id);
        if (rule == null) {
            throw new BusinessException("消课规则不存在");
        }
        return convertToVO(rule);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean enableRule(Long id) {
        ClassHourRule rule = getById(id);
        if (rule == null) {
            throw new BusinessException("消课规则不存在");
        }

        if ("active".equals(rule.getStatus())) {
            throw new BusinessException("规则已经是启用状态");
        }

        rule.setStatus("active");
        boolean result = updateById(rule);

        if (result) {
            log.info("启用消课规则成功: id={}, name={}", id, rule.getName());
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean disableRule(Long id) {
        ClassHourRule rule = getById(id);
        if (rule == null) {
            throw new BusinessException("消课规则不存在");
        }

        if ("inactive".equals(rule.getStatus())) {
            throw new BusinessException("规则已经是停用状态");
        }

        rule.setStatus("inactive");
        boolean result = updateById(rule);

        if (result) {
            log.info("停用消课规则成功: id={}, name={}", id, rule.getName());
        }

        return result;
    }

    /**
     * 转换为VO
     */
    private ClassHourRuleVO convertToVO(ClassHourRule rule) {
        ClassHourRuleVO vo = BeanUtil.copyProperties(rule, ClassHourRuleVO.class);

        // 设置描述信息
        vo.setClassTypeDesc(getClassTypeDesc(rule.getClassType()));
        vo.setDeductTypeDesc(getDeductTypeDesc(rule.getDeductType()));
        vo.setStatusDesc(getStatusDesc(rule.getStatus()));

        // TODO: 填充关联信息（课程名称、校区名称、创建人姓名等）

        return vo;
    }

    /**
     * 获取班级类型描述
     */
    private String getClassTypeDesc(String classType) {
        if (StrUtil.isBlank(classType)) {
            return "通用";
        }
        switch (classType) {
            case "one_on_one":
                return "一对一";
            case "small_class":
                return "小班课";
            case "large_class":
                return "大班课";
            default:
                return classType;
        }
    }

    /**
     * 获取扣减类型描述
     */
    private String getDeductTypeDesc(String deductType) {
        if (StrUtil.isBlank(deductType)) {
            return "";
        }
        switch (deductType) {
            case "per_hour":
                return "按课时";
            case "per_class":
                return "按课次";
            case "custom":
                return "自定义";
            default:
                return deductType;
        }
    }

    /**
     * 获取状态描述
     */
    private String getStatusDesc(String status) {
        if (StrUtil.isBlank(status)) {
            return "";
        }
        switch (status) {
            case "active":
                return "启用";
            case "inactive":
                return "停用";
            default:
                return status;
        }
    }
}
