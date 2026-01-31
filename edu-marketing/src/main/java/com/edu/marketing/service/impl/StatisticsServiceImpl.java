package com.edu.marketing.service.impl;

import com.edu.marketing.domain.dto.AdvisorPerformanceQueryDTO;
import com.edu.marketing.domain.dto.ConversionFunnelQueryDTO;
import com.edu.marketing.domain.vo.AdvisorPerformanceVO;
import com.edu.marketing.domain.vo.ConversionFunnelVO;
import com.edu.marketing.mapper.StatisticsMapper;
import com.edu.marketing.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 统计分析服务实现
 *
 * @author edu
 * @since 2024-01-31
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final StatisticsMapper statisticsMapper;

    @Override
    public ConversionFunnelVO getConversionFunnel(ConversionFunnelQueryDTO query) {
        log.info("查询招生转化漏斗统计，查询条件：{}", query);

        ConversionFunnelVO result = statisticsMapper.selectConversionFunnel(query);

        // 如果查询结果为空，返回默认值
        if (result == null) {
            result = new ConversionFunnelVO();
            result.setNewLeadCount(0);
            result.setFollowingCount(0);
            result.setAppointedCount(0);
            result.setTrialedCount(0);
            result.setConvertedCount(0);
            result.setLostCount(0);
            result.setAppointmentRate(BigDecimal.ZERO);
            result.setTrialRate(BigDecimal.ZERO);
            result.setConversionRate(BigDecimal.ZERO);
            result.setOverallRate(BigDecimal.ZERO);
        }

        log.info("招生转化漏斗统计结果：{}", result);
        return result;
    }

    @Override
    public List<AdvisorPerformanceVO> getAdvisorPerformanceList(AdvisorPerformanceQueryDTO query) {
        log.info("查询顾问业绩统计列表，查询条件：{}", query);

        List<AdvisorPerformanceVO> result = statisticsMapper.selectAdvisorPerformanceList(query);

        log.info("查询到顾问业绩统计记录数：{}", result.size());
        return result;
    }

    @Override
    public List<AdvisorPerformanceVO> getAdvisorPerformanceRanking(AdvisorPerformanceQueryDTO query) {
        log.info("查询顾问业绩排行榜，查询条件：{}", query);

        // 设置默认排序和限制
        if (query.getOrderBy() == null || query.getOrderBy().isEmpty()) {
            query.setOrderBy("conversionCount");
        }
        if (query.getOrderDirection() == null || query.getOrderDirection().isEmpty()) {
            query.setOrderDirection("desc");
        }
        if (query.getLimit() == null || query.getLimit() <= 0) {
            query.setLimit(10); // 默认返回前10名
        }

        List<AdvisorPerformanceVO> result = statisticsMapper.selectAdvisorPerformanceList(query);

        log.info("查询到顾问业绩排行榜记录数：{}", result.size());
        return result;
    }
}
