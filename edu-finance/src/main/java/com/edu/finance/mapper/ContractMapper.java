package com.edu.finance.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.edu.finance.domain.entity.Contract;
import org.apache.ibatis.annotations.Param;

/**
 * 合同 Mapper
 */
@DS("finance")
public interface ContractMapper extends BaseMapper<Contract> {

    /**
     * 分页查询合同列表
     */
    IPage<Contract> selectContractPage(IPage<Contract> page, @Param("query") Contract query);
}
