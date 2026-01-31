package com.edu.system.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 招生数据看板VO
 */
@Data
public class EnrollmentDashboardVO {

    /**
     * 招生数据概览
     */
    @Data
    public static class Overview {
        /**
         * 线索统计
         */
        private LeadStats leadStats;

        /**
         * 试听统计
         */
        private TrialStats trialStats;

        /**
         * 转化率统计
         */
        private ConversionStats conversionStats;

        /**
         * 成交统计
         */
        private DealStats dealStats;
    }

    /**
     * 线索统计
     */
    @Data
    public static class LeadStats {
        /**
         * 总线索数
         */
        private Integer total;

        /**
         * 新增线索
         */
        private Integer newLeads;

        /**
         * 待跟进
         */
        private Integer pending;

        /**
         * 已转化
         */
        private Integer converted;
    }

    /**
     * 试听统计
     */
    @Data
    public static class TrialStats {
        /**
         * 总试听数
         */
        private Integer total;

        /**
         * 已预约
         */
        private Integer scheduled;

        /**
         * 已完成
         */
        private Integer completed;

        /**
         * 转化率（%）
         */
        private Double conversionRate;
    }

    /**
     * 转化率统计
     */
    @Data
    public static class ConversionStats {
        /**
         * 试听转化率（%）
         */
        private Double trialConversionRate;

        /**
         * 成交转化率（%）
         */
        private Double dealConversionRate;

        /**
         * 整体转化率（%）
         */
        private Double overallConversionRate;
    }

    /**
     * 成交统计
     */
    @Data
    public static class DealStats {
        /**
         * 成交数
         */
        private Integer count;

        /**
         * 成交金额
         */
        private BigDecimal amount;
    }

    /**
     * 招生趋势
     */
    @Data
    public static class Trend {
        /**
         * 线索趋势
         */
        private List<TrendItem> leadTrend;

        /**
         * 试听趋势
         */
        private List<TrendItem> trialTrend;

        /**
         * 成交趋势
         */
        private List<TrendItem> dealTrend;
    }

    /**
     * 趋势项
     */
    @Data
    public static class TrendItem {
        /**
         * 日期
         */
        private String date;

        /**
         * 数量
         */
        private Integer count;
    }

    /**
     * 转化漏斗
     */
    @Data
    public static class Funnel {
        /**
         * 漏斗阶段列表
         */
        private List<FunnelStage> stages;
    }

    /**
     * 漏斗阶段
     */
    @Data
    public static class FunnelStage {
        /**
         * 阶段名称
         */
        private String name;

        /**
         * 数量
         */
        private Integer count;

        /**
         * 转化率（相对于上一阶段，%）
         */
        private Double conversionRate;
    }

    /**
     * 线索来源分布
     */
    @Data
    public static class SourceDistribution {
        /**
         * 来源列表
         */
        private List<SourceItem> sources;
    }

    /**
     * 来源项
     */
    @Data
    public static class SourceItem {
        /**
         * 来源名称
         */
        private String source;

        /**
         * 数量
         */
        private Integer count;

        /**
         * 占比（%）
         */
        private Double percentage;
    }

    /**
     * 顾问排行榜
     */
    @Data
    public static class AdvisorRanking {
        /**
         * 排行列表
         */
        private List<AdvisorItem> advisors;
    }

    /**
     * 顾问项
     */
    @Data
    public static class AdvisorItem {
        /**
         * 顾问ID
         */
        private Long advisorId;

        /**
         * 顾问姓名
         */
        private String advisorName;

        /**
         * 成交数
         */
        private Integer dealCount;

        /**
         * 成交金额
         */
        private BigDecimal dealAmount;

        /**
         * 线索数
         */
        private Integer leadCount;

        /**
         * 转化率（%）
         */
        private Double conversionRate;
    }
}
