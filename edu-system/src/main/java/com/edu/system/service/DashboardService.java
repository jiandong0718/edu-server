package com.edu.system.service;

import com.edu.system.domain.vo.DashboardVO;

/**
 * 数据看板服务接口
 */
public interface DashboardService {

    /**
     * 获取数据看板统计
     */
    DashboardVO getDashboard(Long campusId);

    /**
     * 获取学员统计
     */
    DashboardVO.StudentStats getStudentStats(Long campusId);

    /**
     * 获取财务统计
     */
    DashboardVO.FinanceStats getFinanceStats(Long campusId);

    /**
     * 获取教学统计
     */
    DashboardVO.TeachingStats getTeachingStats(Long campusId);

    /**
     * 获取营销统计
     */
    DashboardVO.MarketingStats getMarketingStats(Long campusId);
}
