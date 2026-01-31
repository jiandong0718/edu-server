package com.edu.system.service;

import com.edu.system.domain.vo.RevenueDashboardVO;

import java.util.List;

/**
 * 营收数据看板服务接口
 */
public interface RevenueDashboardService {

    /**
     * 获取营收数据概览
     *
     * @param campusId 校区ID，null表示查询所有校区
     * @return 营收概览数据
     */
    RevenueDashboardVO.RevenueOverview getRevenueOverview(Long campusId);

    /**
     * 获取营收趋势
     *
     * @param campusId 校区ID，null表示查询所有校区
     * @param days 天数，默认30天
     * @return 营收趋势列表
     */
    List<RevenueDashboardVO.RevenueTrendItem> getRevenueTrend(Long campusId, Integer days);

    /**
     * 获取收款方式分布
     *
     * @param campusId 校区ID，null表示查询所有校区
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 收款方式分布列表
     */
    List<RevenueDashboardVO.PaymentMethodItem> getPaymentMethodDistribution(
            Long campusId, String startDate, String endDate);

    /**
     * 获取欠费统计列表
     *
     * @param campusId 校区ID，null表示查询所有校区
     * @param limit 限制数量
     * @return 欠费统计列表
     */
    List<RevenueDashboardVO.ArrearsItem> getArrearsList(Long campusId, Integer limit);

    /**
     * 获取课程营收排行
     *
     * @param campusId 校区ID，null表示查询所有校区
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param limit 限制数量，默认10
     * @return 课程营收排行列表
     */
    List<RevenueDashboardVO.CourseRevenueItem> getCourseRevenueRanking(
            Long campusId, String startDate, String endDate, Integer limit);
}
