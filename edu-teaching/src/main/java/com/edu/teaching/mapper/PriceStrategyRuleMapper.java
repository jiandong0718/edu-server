package com.edu.teaching.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.teaching.domain.entity.PriceStrategyRule;

/**
 * 价格策略规则 Mapper
 */
@DS("teaching")
public interface PriceStrategyRuleMapper extends BaseMapper<PriceStrategyRule> {
}
