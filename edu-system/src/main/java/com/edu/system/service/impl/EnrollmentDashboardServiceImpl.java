package com.edu.system.service.impl;

import com.edu.system.domain.vo.EnrollmentDashboardVO;
import com.edu.system.mapper.EnrollmentDashboardMapper;
import com.edu.system.service.EnrollmentDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 招生数据看板服务实现
 */
@Service
@RequiredArgsConstructor
public class EnrollmentDashboardServiceImpl implements EnrollmentDashboardService {

    private final EnrollmentDashboardMapper enrollmentDashboardMapper;

    @Override
    @Cacheable(value = "enrollment:overview", key = "#campusId + ':' + #timeRange + ':' + #startDate + ':' + #endDate", unless = "#result == null")
    public EnrollmentDashboardVO.Overview getOverview(Long campusId, String timeRange, String startDate, String endDate) {
        LocalDate[] dateRange = parseDateRange(timeRange, startDate, endDate);
        LocalDate start = dateRange[0];
        LocalDate end = dateRange[1];

        EnrollmentDashboardVO.Overview overview = new EnrollmentDashboardVO.Overview();

        // 线索统计
        EnrollmentDashboardVO.LeadStats leadStats = new EnrollmentDashboardVO.LeadStats();
        int newLeads = enrollmentDashboardMapper.countNewLeads(campusId, start, end);
        int pendingLeads = enrollmentDashboardMapper.countPendingLeads(campusId, start, end);
        int convertedLeads = enrollmentDashboardMapper.countConvertedLeads(campusId, start, end);
        leadStats.setTotal(newLeads + pendingLeads + convertedLeads);
        leadStats.setNewLeads(newLeads);
        leadStats.setPending(pendingLeads);
        leadStats.setConverted(convertedLeads);
        overview.setLeadStats(leadStats);

        // 试听统计
        EnrollmentDashboardVO.TrialStats trialStats = new EnrollmentDashboardVO.TrialStats();
        int totalTrials = enrollmentDashboardMapper.countTrialsByDateRange(campusId, start, end);
        int scheduledTrials = enrollmentDashboardMapper.countScheduledTrials(campusId, start, end);
        int completedTrials = enrollmentDashboardMapper.countCompletedTrials(campusId, start, end);
        int trialConversions = enrollmentDashboardMapper.countTrialConversions(campusId, start, end);
        trialStats.setTotal(totalTrials);
        trialStats.setScheduled(scheduledTrials);
        trialStats.setCompleted(completedTrials);
        trialStats.setConversionRate(completedTrials > 0 ?
            BigDecimal.valueOf(trialConversions * 100.0 / completedTrials).setScale(1, RoundingMode.HALF_UP).doubleValue() : 0.0);
        overview.setTrialStats(trialStats);

        // 成交统计
        EnrollmentDashboardVO.DealStats dealStats = new EnrollmentDashboardVO.DealStats();
        int dealCount = enrollmentDashboardMapper.countDeals(campusId, start, end);
        Map<String, Object> dealAmountMap = enrollmentDashboardMapper.sumDealAmount(campusId, start, end);
        BigDecimal dealAmount = dealAmountMap != null && dealAmountMap.get("amount") != null ?
            (BigDecimal) dealAmountMap.get("amount") : BigDecimal.ZERO;
        dealStats.setCount(dealCount);
        dealStats.setAmount(dealAmount);
        overview.setDealStats(dealStats);

        // 转化率统计
        EnrollmentDashboardVO.ConversionStats conversionStats = new EnrollmentDashboardVO.ConversionStats();
        conversionStats.setTrialConversionRate(trialStats.getConversionRate());
        conversionStats.setDealConversionRate(newLeads > 0 ?
            BigDecimal.valueOf(dealCount * 100.0 / newLeads).setScale(1, RoundingMode.HALF_UP).doubleValue() : 0.0);
        conversionStats.setOverallConversionRate(newLeads > 0 ?
            BigDecimal.valueOf(convertedLeads * 100.0 / newLeads).setScale(1, RoundingMode.HALF_UP).doubleValue() : 0.0);
        overview.setConversionStats(conversionStats);

        return overview;
    }

    @Override
    @Cacheable(value = "enrollment:trend", key = "#campusId + ':' + #days", unless = "#result == null")
    public EnrollmentDashboardVO.Trend getTrend(Long campusId, Integer days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        EnrollmentDashboardVO.Trend trend = new EnrollmentDashboardVO.Trend();

        // 线索趋势
        List<Map<String, Object>> leadTrendData = enrollmentDashboardMapper.getLeadTrendByDate(campusId, startDate, endDate);
        trend.setLeadTrend(convertToTrendItems(leadTrendData));

        // 试听趋势
        List<Map<String, Object>> trialTrendData = enrollmentDashboardMapper.getTrialTrendByDate(campusId, startDate, endDate);
        trend.setTrialTrend(convertToTrendItems(trialTrendData));

        // 成交趋势
        List<Map<String, Object>> dealTrendData = enrollmentDashboardMapper.getDealTrendByDate(campusId, startDate, endDate);
        trend.setDealTrend(convertToTrendItems(dealTrendData));

        return trend;
    }

    @Override
    @Cacheable(value = "enrollment:funnel", key = "#campusId + ':' + #timeRange + ':' + #startDate + ':' + #endDate", unless = "#result == null")
    public EnrollmentDashboardVO.Funnel getFunnel(Long campusId, String timeRange, String startDate, String endDate) {
        LocalDate[] dateRange = parseDateRange(timeRange, startDate, endDate);
        LocalDate start = dateRange[0];
        LocalDate end = dateRange[1];

        Map<String, Object> funnelData = enrollmentDashboardMapper.getFunnelData(campusId, start, end);

        EnrollmentDashboardVO.Funnel funnel = new EnrollmentDashboardVO.Funnel();
        List<EnrollmentDashboardVO.FunnelStage> stages = new ArrayList<>();

        // 新线索
        int newLeads = getIntValue(funnelData, "new_leads");
        EnrollmentDashboardVO.FunnelStage stage1 = new EnrollmentDashboardVO.FunnelStage();
        stage1.setName("新线索");
        stage1.setCount(newLeads);
        stage1.setConversionRate(100.0);
        stages.add(stage1);

        // 跟进中
        int following = getIntValue(funnelData, "following");
        EnrollmentDashboardVO.FunnelStage stage2 = new EnrollmentDashboardVO.FunnelStage();
        stage2.setName("跟进中");
        stage2.setCount(following);
        stage2.setConversionRate(newLeads > 0 ?
            BigDecimal.valueOf(following * 100.0 / newLeads).setScale(1, RoundingMode.HALF_UP).doubleValue() : 0.0);
        stages.add(stage2);

        // 已预约
        int scheduled = getIntValue(funnelData, "scheduled");
        EnrollmentDashboardVO.FunnelStage stage3 = new EnrollmentDashboardVO.FunnelStage();
        stage3.setName("已预约");
        stage3.setCount(scheduled);
        stage3.setConversionRate(following > 0 ?
            BigDecimal.valueOf(scheduled * 100.0 / following).setScale(1, RoundingMode.HALF_UP).doubleValue() : 0.0);
        stages.add(stage3);

        // 已试听
        int completed = getIntValue(funnelData, "completed");
        EnrollmentDashboardVO.FunnelStage stage4 = new EnrollmentDashboardVO.FunnelStage();
        stage4.setName("已试听");
        stage4.setCount(completed);
        stage4.setConversionRate(scheduled > 0 ?
            BigDecimal.valueOf(completed * 100.0 / scheduled).setScale(1, RoundingMode.HALF_UP).doubleValue() : 0.0);
        stages.add(stage4);

        // 已成交
        int deals = getIntValue(funnelData, "deals");
        EnrollmentDashboardVO.FunnelStage stage5 = new EnrollmentDashboardVO.FunnelStage();
        stage5.setName("已成交");
        stage5.setCount(deals);
        stage5.setConversionRate(completed > 0 ?
            BigDecimal.valueOf(deals * 100.0 / completed).setScale(1, RoundingMode.HALF_UP).doubleValue() : 0.0);
        stages.add(stage5);

        funnel.setStages(stages);
        return funnel;
    }

    @Override
    @Cacheable(value = "enrollment:source", key = "#campusId + ':' + #timeRange + ':' + #startDate + ':' + #endDate", unless = "#result == null")
    public EnrollmentDashboardVO.SourceDistribution getSourceDistribution(Long campusId, String timeRange, String startDate, String endDate) {
        LocalDate[] dateRange = parseDateRange(timeRange, startDate, endDate);
        LocalDate start = dateRange[0];
        LocalDate end = dateRange[1];

        List<Map<String, Object>> sourceData = enrollmentDashboardMapper.getLeadSourceDistributionByDateRange(campusId, start, end);

        int total = sourceData.stream()
            .mapToInt(item -> getIntValue(item, "count"))
            .sum();

        EnrollmentDashboardVO.SourceDistribution distribution = new EnrollmentDashboardVO.SourceDistribution();
        List<EnrollmentDashboardVO.SourceItem> sources = sourceData.stream()
            .map(item -> {
                EnrollmentDashboardVO.SourceItem sourceItem = new EnrollmentDashboardVO.SourceItem();
                sourceItem.setSource(getStringValue(item, "source"));
                int count = getIntValue(item, "count");
                sourceItem.setCount(count);
                sourceItem.setPercentage(total > 0 ?
                    BigDecimal.valueOf(count * 100.0 / total).setScale(1, RoundingMode.HALF_UP).doubleValue() : 0.0);
                return sourceItem;
            })
            .collect(Collectors.toList());

        distribution.setSources(sources);
        return distribution;
    }

    @Override
    @Cacheable(value = "enrollment:advisor", key = "#campusId + ':' + #timeRange + ':' + #startDate + ':' + #endDate + ':' + #limit", unless = "#result == null")
    public EnrollmentDashboardVO.AdvisorRanking getAdvisorRanking(Long campusId, String timeRange, String startDate, String endDate, Integer limit) {
        LocalDate[] dateRange = parseDateRange(timeRange, startDate, endDate);
        LocalDate start = dateRange[0];
        LocalDate end = dateRange[1];

        List<Map<String, Object>> advisorData = enrollmentDashboardMapper.getAdvisorPerformanceRanking(campusId, start, end, limit);

        EnrollmentDashboardVO.AdvisorRanking ranking = new EnrollmentDashboardVO.AdvisorRanking();
        List<EnrollmentDashboardVO.AdvisorItem> advisors = advisorData.stream()
            .map(item -> {
                EnrollmentDashboardVO.AdvisorItem advisor = new EnrollmentDashboardVO.AdvisorItem();
                advisor.setAdvisorId(getLongValue(item, "advisor_id"));
                advisor.setAdvisorName(getStringValue(item, "advisor_name"));
                advisor.setDealCount(getIntValue(item, "deal_count"));
                advisor.setDealAmount(getBigDecimalValue(item, "deal_amount"));
                advisor.setLeadCount(getIntValue(item, "lead_count"));
                int leadCount = advisor.getLeadCount();
                int dealCount = advisor.getDealCount();
                advisor.setConversionRate(leadCount > 0 ?
                    BigDecimal.valueOf(dealCount * 100.0 / leadCount).setScale(1, RoundingMode.HALF_UP).doubleValue() : 0.0);
                return advisor;
            })
            .collect(Collectors.toList());

        ranking.setAdvisors(advisors);
        return ranking;
    }

    /**
     * 解析时间范围
     */
    private LocalDate[] parseDateRange(String timeRange, String startDate, String endDate) {
        LocalDate start;
        LocalDate end = LocalDate.now();

        switch (timeRange) {
            case "today":
                start = end;
                break;
            case "week":
                start = end.minusDays(6);
                break;
            case "month":
                start = end.minusDays(29);
                break;
            case "custom":
                if (startDate != null && endDate != null) {
                    start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
                    end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
                } else {
                    start = end.minusDays(29);
                }
                break;
            default:
                start = end.minusDays(29);
        }

        return new LocalDate[]{start, end};
    }

    /**
     * 转换为趋势项列表
     */
    private List<EnrollmentDashboardVO.TrendItem> convertToTrendItems(List<Map<String, Object>> data) {
        return data.stream()
            .map(item -> {
                EnrollmentDashboardVO.TrendItem trendItem = new EnrollmentDashboardVO.TrendItem();
                trendItem.setDate(getStringValue(item, "date"));
                trendItem.setCount(getIntValue(item, "count"));
                return trendItem;
            })
            .collect(Collectors.toList());
    }

    /**
     * 获取整数值
     */
    private int getIntValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return 0;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return 0;
    }

    /**
     * 获取长整数值
     */
    private Long getLongValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return null;
    }

    /**
     * 获取字符串值
     */
    private String getStringValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : "";
    }

    /**
     * 获取BigDecimal值
     */
    private BigDecimal getBigDecimalValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        }
        return BigDecimal.ZERO;
    }
}
