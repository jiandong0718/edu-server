package com.edu.system.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.edu.system.domain.vo.RevenueDashboardVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 营收数据看板Mapper
 */
@Mapper
public interface RevenueDashboardMapper {

    /**
     * 统计今日收入
     */
    BigDecimal sumIncomeToday(@Param("campusId") Long campusId);

    /**
     * 统计本周收入
     */
    BigDecimal sumIncomeThisWeek(@Param("campusId") Long campusId);

    /**
     * 统计本月收入
     */
    BigDecimal sumIncomeThisMonth(@Param("campusId") Long campusId);

    /**
     * 统计本年收入
     */
    BigDecimal sumIncomeThisYear(@Param("campusId") Long campusId);

    /**
     * 统计总欠费金额
     */
    BigDecimal sumTotalArrears(@Param("campusId") Long campusId);

    /**
     * 统计欠费人数
     */
    Integer countArrearsStudents(@Param("campusId") Long campusId);

    /**
     * 统计欠费合同数
     */
    Integer countArrearsContracts(@Param("campusId") Long campusId);

    /**
     * 统计本月退费金额
     */
    BigDecimal sumRefundThisMonth(@Param("campusId") Long campusId);

    /**
     * 获取营收趋势（按日期）
     */
    List<RevenueDashboardVO.RevenueTrendItem> getRevenueTrend(
            @Param("campusId") Long campusId,
            @Param("days") Integer days
    );

    /**
     * 获取收款方式分布
     */
    List<RevenueDashboardVO.PaymentMethodItem> getPaymentMethodDistribution(
            @Param("campusId") Long campusId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    /**
     * 获取欠费统计列表
     */
    List<RevenueDashboardVO.ArrearsItem> getArrearsList(
            @Param("campusId") Long campusId,
            @Param("limit") Integer limit
    );

    /**
     * 获取课程营收排行
     */
    List<RevenueDashboardVO.CourseRevenueItem> getCourseRevenueRanking(
            @Param("campusId") Long campusId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("limit") Integer limit
    );
}
