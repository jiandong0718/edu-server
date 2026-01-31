package com.edu.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.finance.domain.entity.ContractApprovalFlow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 合同审批流程Mapper
 */
@Mapper
public interface ContractApprovalFlowMapper extends BaseMapper<ContractApprovalFlow> {

    /**
     * 根据审批ID查询审批流程列表
     *
     * @param approvalId 审批ID
     * @return 审批流程列表
     */
    List<ContractApprovalFlow> selectByApprovalId(@Param("approvalId") Long approvalId);
}
