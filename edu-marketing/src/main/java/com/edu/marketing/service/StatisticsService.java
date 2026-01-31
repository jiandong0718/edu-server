package com.edu.marketing.service;

import com.edu.marketing.domain.dto.AdvisorPerformanceQueryDTO;
import com.edu.marketing.domain.dto.ConversionFunnelQueryDTO;
import com.edu.marketing.domain.vo.AdvisorPerformanceVO;
import com.edu.marketing.domain.vo.ConversionFunnelVO;

import java.util.List;

/**
 * 统计分析服务接口
 *
 * @author edu
 * @since 2024-01-31
 */
public interface StatisticsService {

    /**
     * 获取招生转化漏斗统计
     *
     * @param query 查询条件
     * @return 转化漏斗统计数据
     */
    ConversionFunnelVO getConversionFunnel(ConversionFunnelQueryDTO query);

    /**
     * 获取顾问业绩统计列表
     *
     * @param query 查询条件
     * @return 顾问业绩统计列表
     */
    List<AdvisorPerformanceVO> getAdvisorPerformanceList(AdvisorPerformanceQueryDTO query);

    /**
     * 获取顾问业绩排行榜
     *
     * @param query 查询条件
     * @return 顾问业绩排行榜（按成交数量降序）
     */
    List<AdvisorPerformanceVO> getAdvisorPerformanceRanking(AdvisorPerformanceQueryDTO query);
}
