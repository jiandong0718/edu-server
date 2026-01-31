package com.edu.teaching.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.teaching.domain.entity.PriceStrategy;

/**
 * 价格策略 Mapper
 */
@DS("teaching")
public interface PriceStrategyMapper extends BaseMapper<PriceStrategy> {
}
