package com.edu.finance.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.finance.domain.entity.ContractApprovalConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 合同审批配置Mapper
 */
@Mapper
public interface ContractApprovalConfigMapper extends BaseMapper<ContractApprovalConfig> {

    /**
     * 根据审批类型和金额查询匹配的审批配置
     *
     * @param approvalType 审批类型
     * @param amount 金额
     * @return 审批配置
     */
    ContractApprovalConfig selectByTypeAndAmount(@Param("approvalType") String approvalType,
                                                   @Param("amount") BigDecimal amount);

    /**
     * 查询启用的审批配置列表
     *
     * @param approvalType 审批类型
     * @return 审批配置列表
     */
    List<ContractApprovalConfig> selectEnabledList(@Param("approvalType") String approvalType);
}
