package com.edu.finance.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.edu.finance.domain.dto.ClassHourReportQueryDTO;
import com.edu.finance.domain.dto.RevenueReportQueryDTO;
import com.edu.finance.domain.vo.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 财务报表 Mapper
 */
@DS("finance")
public interface FinanceReportMapper {

    /**
     * 查询收入汇总统计
     */
    RevenueSummaryVO selectRevenueSummary(@Param("query") RevenueReportQueryDTO query);

    /**
     * 按时间维度统计收入
     */
    List<RevenueReportVO> selectRevenueByTime(@Param("query") RevenueReportQueryDTO query);

    /**
     * 按校区维度统计收入
     */
    List<RevenueReportVO> selectRevenueByCampus(@Param("query") RevenueReportQueryDTO query);

    /**
     * 按课程类型统计收入
     */
    List<RevenueReportVO> selectRevenueByCourseType(@Param("query") RevenueReportQueryDTO query);

    /**
     * 按支付方式统计收入
     */
    List<RevenueReportVO> selectRevenueByPaymentMethod(@Param("query") RevenueReportQueryDTO query);

    /**
     * 查询课消趋势
     */
    List<ClassHourTrendVO> selectClassHourTrend(@Param("query") ClassHourReportQueryDTO query);

    /**
     * 按班级统计课消
     */
    List<ClassHourReportVO> selectClassHourByClass(@Param("query") ClassHourReportQueryDTO query);

    /**
     * 按课程统计课消
     */
    List<ClassHourReportVO> selectClassHourByCourse(@Param("query") ClassHourReportQueryDTO query);

    /**
     * 按教师统计课消
     */
    List<ClassHourReportVO> selectClassHourByTeacher(@Param("query") ClassHourReportQueryDTO query);

    /**
     * 查询课消汇总统计
     */
    ClassHourSummaryVO selectClassHourSummary(@Param("query") ClassHourReportQueryDTO query);
}
