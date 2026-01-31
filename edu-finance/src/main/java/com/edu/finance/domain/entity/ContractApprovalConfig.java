package com.edu.finance.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 合同审批配置实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fin_contract_approval_config")
public class ContractApprovalConfig extends BaseEntity {

    /**
     * 配置名称
     */
    private String configName;

    /**
     * 审批类型：contract-合同审批，change-变更审批，cancel-作废审批
     */
    private String approvalType;

    /**
     * 金额下限
     */
    private BigDecimal amountMin;

    /**
     * 金额上限（NULL表示无上限）
     */
    private BigDecimal amountMax;

    /**
     * 审批级数
     */
    private Integer approvalLevels;

    /**
     * 审批人配置（JSON格式）
     */
    private String approverConfig;

    /**
     * 是否启用
     */
    private Integer isEnabled;

    /**
     * 优先级（数字越大优先级越高）
     */
    private Integer priority;

    /**
     * 备注
     */
    private String remark;
}
