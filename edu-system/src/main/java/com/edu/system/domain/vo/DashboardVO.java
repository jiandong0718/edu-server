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
         * 今日收入
         */
        private BigDecimal incomeToday;

        /**
         * 本周收入
         */
        private BigDecimal incomeThisWeek;

        /**
         * 本月收入
         */
        private BigDecimal incomeThisMonth;

        /**
         * 本年收入
         */
        private BigDecimal incomeThisYear;

        /**
         * 本月退费
         */
        private BigDecimal refundThisMonth;

        /**
         * 待收款金额（欠费）
         */
        private BigDecimal pendingAmount;

        /**
         * 逾期欠费金额
         */
        private BigDecimal overdueAmount;

        /**
         * 合同总数
         */
        private Integer contractCount;

        /**
         * 收款方式分布
         */
        private List<Map<String, Object>> paymentMethodDistribution;

        /**
         * 近30天收入趋势
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
         * 已结业班级数
         */
        private Integer completedClassCount;

        /**
         * 教师总数
         */
        private Integer teacherCount;

        /**
         * 在职教师数
         */
        private Integer activeTeacherCount;

        /**
         * 休假教师数
         */
        private Integer onLeaveTeacherCount;

        /**
         * 课程总数
         */
        private Integer courseCount;

        /**
         * 学员总数
         */
        private Integer studentCount;

        /**
         * 在读学员数
         */
        private Integer enrolledStudentCount;

        /**
         * 试听学员数
         */
        private Integer trialStudentCount;

        /**
         * 潜在学员数
         */
        private Integer potentialStudentCount;

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
         * 待跟进线索数
         */
        private Integer pendingLeadCount;

        /**
         * 已转化线索数
         */
        private Integer convertedLeadCount;

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
         * 试听总数
         */
        private Integer trialCount;

        /**
         * 本月试听数
         */
        private Integer trialThisMonth;

        /**
         * 试听转化数
         */
        private Integer trialConvertedCount;

        /**
         * 试听转化率
         */
        private Double trialConversionRate;

        /**
         * 线索来源分布
         */
        private List<Map<String, Object>> sourceDistribution;

        /**
         * 线索趋势（近30天）
         */
        private List<Map<String, Object>> leadTrend;

        /**
         * 转化趋势（近30天）
         */
        private List<Map<String, Object>> conversionTrend;
    }
}
