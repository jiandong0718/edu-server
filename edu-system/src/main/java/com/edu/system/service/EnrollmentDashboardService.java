package com.edu.system.service;

import com.edu.system.domain.vo.EnrollmentDashboardVO;

/**
 * 招生数据看板服务接口
 */
public interface EnrollmentDashboardService {

    /**
     * 获取招生数据概览
     */
    EnrollmentDashboardVO.Overview getOverview(Long campusId, String timeRange, String startDate, String endDate);

    /**
     * 获取招生趋势
     */
    EnrollmentDashboardVO.Trend getTrend(Long campusId, Integer days);

    /**
     * 获取转化漏斗
     */
    EnrollmentDashboardVO.Funnel getFunnel(Long campusId, String timeRange, String startDate, String endDate);

    /**
     * 获取线索来源分布
     */
    EnrollmentDashboardVO.SourceDistribution getSourceDistribution(Long campusId, String timeRange, String startDate, String endDate);

    /**
     * 获取顾问排行榜
     */
    EnrollmentDashboardVO.AdvisorRanking getAdvisorRanking(Long campusId, String timeRange, String startDate, String endDate, Integer limit);
}
