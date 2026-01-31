package com.edu.notification.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 通知发送统计VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationStatisticsVO {

    /**
     * 总发送数
     */
    private Long totalCount;

    /**
     * 成功数
     */
    private Long successCount;

    /**
     * 失败数
     */
    private Long failedCount;

    /**
     * 待发送数
     */
    private Long pendingCount;

    /**
     * 发送中数
     */
    private Long sendingCount;

    /**
     * 成功率（百分比）
     */
    private Double successRate;

    /**
     * 按类型统计
     * key: 类型（sms/site/email/wechat/push）
     * value: TypeStatistics
     */
    private Map<String, TypeStatistics> typeStatistics;

    /**
     * 按日期统计
     * List<DateStatistics>
     */
    private List<DateStatistics> dateStatistics;

    /**
     * 类型统计详情
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TypeStatistics {
        /**
         * 类型
         */
        private String type;

        /**
         * 总数
         */
        private Long count;

        /**
         * 成功数
         */
        private Long successCount;

        /**
         * 失败数
         */
        private Long failedCount;

        /**
         * 成功率
         */
        private Double successRate;
    }

    /**
     * 日期统计详情
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DateStatistics {
        /**
         * 日期
         */
        private String date;

        /**
         * 总数
         */
        private Long count;

        /**
         * 成功数
         */
        private Long successCount;

        /**
         * 失败数
         */
        private Long failedCount;
    }
}
