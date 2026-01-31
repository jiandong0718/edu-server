package com.edu.notification.domain.vo;

import lombok.Data;

/**
 * 批量发送结果VO
 */
@Data
public class BatchSendResultVO {

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 总数
     */
    private Integer totalCount;

    /**
     * 成功数
     */
    private Integer successCount;

    /**
     * 失败数
     */
    private Integer failedCount;

    /**
     * 发送状态：pending-待发送，sending-发送中，completed-已完成，failed-失败
     */
    private String status;

    /**
     * 进度百分比
     */
    private Integer progress;

    /**
     * 错误信息
     */
    private String errorMessage;
}
