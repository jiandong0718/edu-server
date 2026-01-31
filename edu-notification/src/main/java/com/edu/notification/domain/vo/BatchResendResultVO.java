package com.edu.notification.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 批量重发结果VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchResendResultVO {

    /**
     * 总数
     */
    private Integer total;

    /**
     * 成功数
     */
    private Integer successCount;

    /**
     * 失败数
     */
    private Integer failedCount;

    /**
     * 成功的ID列表
     */
    private List<Long> successIds;

    /**
     * 失败的ID列表
     */
    private List<Long> failedIds;

    /**
     * 失败原因列表
     */
    private List<FailedItem> failedItems;

    /**
     * 失败项详情
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FailedItem {
        /**
         * ID
         */
        private Long id;

        /**
         * 失败原因
         */
        private String reason;
    }
}
