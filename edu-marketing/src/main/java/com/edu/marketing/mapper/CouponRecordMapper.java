package com.edu.marketing.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.marketing.domain.entity.CouponRecord;
import com.edu.marketing.domain.vo.CouponStatisticsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 优惠券发放记录Mapper
 */
@Mapper
public interface CouponRecordMapper extends BaseMapper<CouponRecord> {

    /**
     * 查询优惠券统计信息
     *
     * @param couponId 优惠券ID（可选）
     * @param campusId 校区ID（可选）
     * @return 统计信息列表
     */
    List<CouponStatisticsVO> getCouponStatistics(@Param("couponId") Long couponId,
                                                   @Param("campusId") Long campusId);

    /**
     * 统计学员已领取某优惠券的数量
     *
     * @param studentId 学员ID
     * @param couponId 优惠券ID
     * @return 已领取数量
     */
    int countReceivedByStudent(@Param("studentId") Long studentId,
                                @Param("couponId") Long couponId);

    /**
     * 统计学员已使用某优惠券的数量
     *
     * @param studentId 学员ID
     * @param couponId 优惠券ID
     * @return 已使用数量
     */
    int countUsedByStudent(@Param("studentId") Long studentId,
                           @Param("couponId") Long couponId);
}
