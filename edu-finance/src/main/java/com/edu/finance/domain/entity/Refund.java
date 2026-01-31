package com.edu.finance.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 退费申请实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fin_refund")
public class Refund extends BaseEntity {

    /**
     * 退费单号
     */
    private String refundNo;

    /**
     * 合同ID
     */
    private Long contractId;

    /**
     * 学员ID
     */
    private Long studentId;

    /**
     * 校区ID
     */
    private Long campusId;

    /**
     * 申请退费金额
     */
    private BigDecimal applyAmount;

    /**
     * 实际退费金额
     */
    private BigDecimal actualAmount;

    /**
     * 违约金
     */
    private BigDecimal penaltyAmount;

    /**
     * 退费原因
     */
    private String reason;

    /**
     * 退费说明
     */
    private String description;

    /**
     * 状态：pending-待审批，approved-已通过，rejected-已拒绝，refunded-已退款
     */
    private String status;

    /**
     * 申请时间
     */
    private LocalDateTime applyTime;

    /**
     * 审批人ID
     */
    private Long approverId;

    /**
     * 审批时间
     */
    private LocalDateTime approveTime;

    /**
     * 审批备注
     */
    private String approveRemark;

    /**
     * 退款时间
     */
    private LocalDateTime refundTime;

    /**
     * 退款方式：wechat-微信，alipay-支付宝，unionpay-银联，cash-现金，bank-银行转账
     */
    private String refundMethod;

    /**
     * 退款交易号
     */
    private String refundTransactionNo;

    /**
     * 学员姓名（非数据库字段）
     */
    @TableField(exist = false)
    private String studentName;

    /**
     * 合同编号（非数据库字段）
     */
    @TableField(exist = false)
    private String contractNo;

    /**
     * 校区名称（非数据库字段）
     */
    @TableField(exist = false)
    private String campusName;

    /**
     * 审批人姓名（非数据库字段）
     */
    @TableField(exist = false)
    private String approverName;
}
