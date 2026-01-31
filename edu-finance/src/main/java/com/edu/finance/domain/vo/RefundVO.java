package com.edu.finance.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 退费申请VO
 */
@Data
@Schema(description = "退费申请VO")
public class RefundVO {

    /**
     * 退费申请ID
     */
    @Schema(description = "退费申请ID")
    private Long id;

    /**
     * 退费单号
     */
    @Schema(description = "退费单号")
    private String refundNo;

    /**
     * 合同ID
     */
    @Schema(description = "合同ID")
    private Long contractId;

    /**
     * 合同编号
     */
    @Schema(description = "合同编号")
    private String contractNo;

    /**
     * 学员ID
     */
    @Schema(description = "学员ID")
    private Long studentId;

    /**
     * 学员姓名
     */
    @Schema(description = "学员姓名")
    private String studentName;

    /**
     * 校区ID
     */
    @Schema(description = "校区ID")
    private Long campusId;

    /**
     * 校区名称
     */
    @Schema(description = "校区名称")
    private String campusName;

    /**
     * 申请退费金额
     */
    @Schema(description = "申请退费金额")
    private BigDecimal applyAmount;

    /**
     * 实际退费金额
     */
    @Schema(description = "实际退费金额")
    private BigDecimal actualAmount;

    /**
     * 违约金
     */
    @Schema(description = "违约金")
    private BigDecimal penaltyAmount;

    /**
     * 退费原因
     */
    @Schema(description = "退费原因")
    private String reason;

    /**
     * 退费说明
     */
    @Schema(description = "退费说明")
    private String description;

    /**
     * 状态：pending-待审批，approved-已通过，rejected-已拒绝，refunded-已退款
     */
    @Schema(description = "状态")
    private String status;

    /**
     * 申请时间
     */
    @Schema(description = "申请时间")
    private LocalDateTime applyTime;

    /**
     * 审批人ID
     */
    @Schema(description = "审批人ID")
    private Long approverId;

    /**
     * 审批人姓名
     */
    @Schema(description = "审批人姓名")
    private String approverName;

    /**
     * 审批时间
     */
    @Schema(description = "审批时间")
    private LocalDateTime approveTime;

    /**
     * 审批备注
     */
    @Schema(description = "审批备注")
    private String approveRemark;

    /**
     * 退款时间
     */
    @Schema(description = "退款时间")
    private LocalDateTime refundTime;

    /**
     * 退款方式
     */
    @Schema(description = "退款方式")
    private String refundMethod;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
