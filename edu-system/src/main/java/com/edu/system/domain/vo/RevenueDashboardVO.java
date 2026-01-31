package com.edu.system.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 营收数据看板VO
 */
@Data
public class RevenueDashboardVO {

    /**
     * 营收概览
     */
    @Data
    public static class RevenueOverview {
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
         * 总欠费金额
         */
        private BigDecimal totalArrears;

        /**
         * 欠费人数
         */
        private Integer arrearsStudentCount;

        /**
         * 欠费合同数
         */
        private Integer arrearsContractCount;

        /**
         * 本月退费金额
         */
        private BigDecimal refundThisMonth;

        /**
         * 退费率（本月退费/本月收入）
         */
        private Double refundRate;
    }

    /**
     * 营收趋势项
     */
    @Data
    public static class RevenueTrendItem {
        /**
         * 日期
         */
        private String date;

        /**
         * 收入金额
         */
        private BigDecimal amount;
    }

    /**
     * 收款方式分布项
     */
    @Data
    public static class PaymentMethodItem {
        /**
         * 支付方式
         */
        private String method;

        /**
         * 支付方式名称
         */
        private String methodName;

        /**
         * 金额
         */
        private BigDecimal amount;

        /**
         * 笔数
         */
        private Integer count;

        /**
         * 占比
         */
        private Double percentage;
    }

    /**
     * 欠费统计项
     */
    @Data
    public static class ArrearsItem {
        /**
         * 学员ID
         */
        private Long studentId;

        /**
         * 学员姓名
         */
        private String studentName;

        /**
         * 合同编号
         */
        private String contractNo;

        /**
         * 合同ID
         */
        private Long contractId;

        /**
         * 应付金额
         */
        private BigDecimal paidAmount;

        /**
         * 已付金额
         */
        private BigDecimal receivedAmount;

        /**
         * 欠费金额
         */
        private BigDecimal arrearsAmount;

        /**
         * 校区名称
         */
        private String campusName;

        /**
         * 签约日期
         */
        private String signDate;

        /**
         * 到期日期
         */
        private String expireDate;
    }

    /**
     * 课程营收排行项
     */
    @Data
    public static class CourseRevenueItem {
        /**
         * 课程ID
         */
        private Long courseId;

        /**
         * 课程名称
         */
        private String courseName;

        /**
         * 营收金额
         */
        private BigDecimal revenue;

        /**
         * 合同数
         */
        private Integer contractCount;

        /**
         * 学员数
         */
        private Integer studentCount;
    }
}
