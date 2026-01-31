package com.edu.system.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 招生数据看板 Mapper
 */
@DS("system")
public interface EnrollmentDashboardMapper {

    // ========== 线索统计 ==========

    /**
     * 统计时间范围内的新增线索数
     */
    int countNewLeads(@Param("campusId") Long campusId,
                      @Param("startDate") LocalDate startDate,
                      @Param("endDate") LocalDate endDate);

    /**
     * 统计时间范围内待跟进线索数
     */
    int countPendingLeads(@Param("campusId") Long campusId,
                          @Param("startDate") LocalDate startDate,
                          @Param("endDate") LocalDate endDate);

    /**
     * 统计时间范围内已转化线索数
     */
    int countConvertedLeads(@Param("campusId") Long campusId,
                            @Param("startDate") LocalDate startDate,
                            @Param("endDate") LocalDate endDate);

    // ========== 试听统计 ==========

    /**
     * 统计时间范围内的试听总数
     */
    int countTrialsByDateRange(@Param("campusId") Long campusId,
                               @Param("startDate") LocalDate startDate,
                               @Param("endDate") LocalDate endDate);

    /**
     * 统计时间范围内已预约试听数
     */
    int countScheduledTrials(@Param("campusId") Long campusId,
                             @Param("startDate") LocalDate startDate,
                             @Param("endDate") LocalDate endDate);

    /**
     * 统计时间范围内已完成试听数
     */
    int countCompletedTrials(@Param("campusId") Long campusId,
                             @Param("startDate") LocalDate startDate,
                             @Param("endDate") LocalDate endDate);

    /**
     * 统计时间范围内试听转化数
     */
    int countTrialConversions(@Param("campusId") Long campusId,
                              @Param("startDate") LocalDate startDate,
                              @Param("endDate") LocalDate endDate);

    // ========== 成交统计 ==========

    /**
     * 统计时间范围内的成交数
     */
    int countDeals(@Param("campusId") Long campusId,
                   @Param("startDate") LocalDate startDate,
                   @Param("endDate") LocalDate endDate);

    /**
     * 统计时间范围内的成交金额
     */
    Map<String, Object> sumDealAmount(@Param("campusId") Long campusId,
                                      @Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);

    // ========== 趋势数据 ==========

    /**
     * 获取线索趋势（按日期）
     */
    List<Map<String, Object>> getLeadTrendByDate(@Param("campusId") Long campusId,
                                                  @Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate);

    /**
     * 获取试听趋势（按日期）
     */
    List<Map<String, Object>> getTrialTrendByDate(@Param("campusId") Long campusId,
                                                   @Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);

    /**
     * 获取成交趋势（按日期）
     */
    List<Map<String, Object>> getDealTrendByDate(@Param("campusId") Long campusId,
                                                  @Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate);

    // ========== 转化漏斗 ==========

    /**
     * 获取转化漏斗数据
     */
    Map<String, Object> getFunnelData(@Param("campusId") Long campusId,
                                      @Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);

    // ========== 线索来源分布 ==========

    /**
     * 获取线索来源分布
     */
    List<Map<String, Object>> getLeadSourceDistributionByDateRange(@Param("campusId") Long campusId,
                                                                    @Param("startDate") LocalDate startDate,
                                                                    @Param("endDate") LocalDate endDate);

    // ========== 顾问排行 ==========

    /**
     * 获取顾问业绩排行
     */
    List<Map<String, Object>> getAdvisorPerformanceRanking(@Param("campusId") Long campusId,
                                                            @Param("startDate") LocalDate startDate,
                                                            @Param("endDate") LocalDate endDate,
                                                            @Param("limit") Integer limit);
}
