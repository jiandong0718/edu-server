package com.edu.marketing.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.marketing.domain.entity.Coupon;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券Mapper
 */
@Mapper
public interface CouponMapper extends BaseMapper<Coupon> {
}
