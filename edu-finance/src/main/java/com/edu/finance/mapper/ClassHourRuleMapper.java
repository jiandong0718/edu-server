package com.edu.finance.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.finance.domain.entity.ClassHourRule;

/**
 * 课时消课规则 Mapper
 */
@DS("finance")
public interface ClassHourRuleMapper extends BaseMapper<ClassHourRule> {
}
