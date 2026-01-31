package com.edu.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.finance.domain.entity.ContractApproval;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 合同审批记录Mapper
 */
@Mapper
public interface ContractApprovalMapper extends BaseMapper<ContractApproval> {

    /**
     * 根据合同ID查询审批记录列表
     *
     * @param contractId 合同ID
     * @return 审批记录列表
     */
    List<ContractApproval> selectByContractId(@Param("contractId") Long contractId);

    /**
     * 查询待审批列表
     *
     * @param approverId 审批人ID
     * @return 待审批列表
     */
    List<ContractApproval> selectPendingList(@Param("approverId") Long approverId);
}
