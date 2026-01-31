package com.edu.finance.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审批记录DTO（用于时间线展示）
 */
@Data
public class ApprovalRecordDTO {

    /**
     * 步骤序号
     */
    private Integer stepNo;

    /**
     * 操作类型：submit-提交，approve-审批通过，reject-拒绝，return-退回，cancel-撤销
     */
    private String actionType;

    /**
     * 操作类型名称
     */
    private String actionTypeName;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人姓名
     */
    private String operatorName;

    /**
     * 操作人头像
     */
    private String operatorAvatar;

    /**
     * 操作时间
     */
    private LocalDateTime operateTime;

    /**
     * 操作意见/原因
     */
    private String remark;

    /**
     * 状态：pending-待处理，completed-已完成
     */
    private String status;
}
