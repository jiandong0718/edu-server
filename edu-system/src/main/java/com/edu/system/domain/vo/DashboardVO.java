package com.edu.system.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 数据看板统计VO
 */
@Data
public class DashboardVO {

    /**
     * 学员统计
     */
    private StudentStats studentStats;

    /**
     * 财务统计
     */
    private FinanceStats financeStats;

    /**
     * 教学统计
     */
    private TeachingStats teachingStats;

    /**
     * 营销统计
     */
    private MarketingStats marketingStats;

    /**
     * 学员统计
     */
    @Data
    public static class StudentStats {
        /**
         * 总学员数
         */
        private Integer totalCount;

        /**
         * 在读学员数
         */
        private Integer enrolledCount;

        /**
         * 潜在学员数
         */
        private Integer potentialCount;

        /**
         * 本月新增学员数
         */
        private Integer newCountThisMonth;

        /**
         * 学员状态分布
         */
        private List<Map<String, Object>> statusDistribution;
    }

    /**
     * 财务统计
     */
    @Data
    public static class FinanceStats {
        /**
         * 本月收入
         */
        private BigDecimal incomeThisMonth;

        /**
         * 本月退费
         */
        private BigDecimal refundThisMonth;

        /**
         * 待收款金额
         */
        private BigDecimal pendingAmount;

        /**
         * 合同总数
         */
        private Integer contractCount;

        /**
         * 近7天收入趋势
         */
        private List<Map<String, Object>> incomeTrend;
    }

    /**
     * 教学统计
     */
    @Data
    public static class TeachingStats {
        /**
         * 今日课节数
         */
        private Integer todayScheduleCount;

        /**
         * 本周课节数
         */
        private Integer weekScheduleCount;

        /**
         * 班级总数
         */
        private Integer classCount;

        /**
         * 进行中班级数
         */
        private Integer ongoingClassCount;

        /**
         * 教师总数
         */
        private Integer teacherCount;

        /**
         * 本周出勤率
         */
        private Double attendanceRate;
    }

    /**
     * 营销统计
     */
    @Data
    public static class MarketingStats {
        /**
         * 线索总数
         */
        private Integer leadCount;

        /**
         * 本月新增线索
         */
        private Integer newLeadThisMonth;

        /**
         * 本月转化数
         */
        private Integer convertedThisMonth;

        /**
         * 转化率
         */
        private Double conversionRate;

        /**
         * 线索来源分布
         */
        private List<Map<String, Object>> sourceDistribution;
    }
}
