package com.edu.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.finance.domain.entity.ContractPrintRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 合同打印记录Mapper
 */
@Mapper
public interface ContractPrintRecordMapper extends BaseMapper<ContractPrintRecord> {

    /**
     * 根据合同ID查询打印记录列表
     *
     * @param contractId 合同ID
     * @return 打印记录列表
     */
    List<ContractPrintRecord> selectByContractId(@Param("contractId") Long contractId);
}
