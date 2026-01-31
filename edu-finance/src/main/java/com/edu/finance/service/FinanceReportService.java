package com.edu.finance.service;

import com.edu.finance.domain.dto.ClassHourReportQueryDTO;
import com.edu.finance.domain.dto.RevenueReportQueryDTO;
import com.edu.finance.domain.vo.*;

import java.util.List;

/**
 * 财务报表服务接口
 */
public interface FinanceReportService {

    /**
     * 获取收入汇总统计
     *
     * @param query 查询条件
     * @return 收入汇总VO
     */
    RevenueSummaryVO getRevenueSummary(RevenueReportQueryDTO query);

    /**
     * 按时间维度统计收入
     *
     * @param query 查询条件
     * @return 收入报表列表
     */
    List<RevenueReportVO> getRevenueByTime(RevenueReportQueryDTO query);

    /**
     * 按校区维度统计收入
     *
     * @param query 查询条件
     * @return 收入报表列表
     */
    List<RevenueReportVO> getRevenueByCampus(RevenueReportQueryDTO query);

    /**
     * 按课程类型统计收入
     *
     * @param query 查询条件
     * @return 收入报表列表
     */
    List<RevenueReportVO> getRevenueByCourseType(RevenueReportQueryDTO query);

    /**
     * 按支付方式统计收入
     *
     * @param query 查询条件
     * @return 收入报表列表
     */
    List<RevenueReportVO> getRevenueByPaymentMethod(RevenueReportQueryDTO query);

    /**
     * 获取课消趋势
     *
     * @param query 查询条件
     * @return 课消趋势列表
     */
    List<ClassHourTrendVO> getClassHourTrend(ClassHourReportQueryDTO query);

    /**
     * 按班级统计课消
     *
     * @param query 查询条件
     * @return 课消报表列表
     */
    List<ClassHourReportVO> getClassHourByClass(ClassHourReportQueryDTO query);

    /**
     * 按课程统计课消
     *
     * @param query 查询条件
     * @return 课消报表列表
     */
    List<ClassHourReportVO> getClassHourByCourse(ClassHourReportQueryDTO query);

    /**
     * 按教师统计课消
     *
     * @param query 查询条件
     * @return 课消报表列表
     */
    List<ClassHourReportVO> getClassHourByTeacher(ClassHourReportQueryDTO query);

    /**
     * 获取课消汇总统计
     *
     * @param query 查询条件
     * @return 课时汇总VO
     */
    ClassHourSummaryVO getClassHourSummary(ClassHourReportQueryDTO query);
}
