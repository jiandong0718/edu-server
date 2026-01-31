package com.edu.finance.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.finance.domain.entity.ContractItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 合同明细 Mapper
 */
@DS("finance")
public interface ContractItemMapper extends BaseMapper<ContractItem> {

    /**
     * 根据合同ID查询明细列表
     *
     * @param contractId 合同ID
     * @return 合同明细列表
     */
    List<ContractItem> selectByContractId(@Param("contractId") Long contractId);
}
