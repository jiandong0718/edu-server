package com.edu.marketing.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.marketing.domain.entity.CouponRule;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券规则Mapper
 */
@Mapper
public interface CouponRuleMapper extends BaseMapper<CouponRule> {
}
