package com.edu.finance.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.finance.domain.entity.Contract;

/**
 * 合同服务接口
 */
public interface ContractService extends IService<Contract> {

    /**
     * 分页查询合同列表
     */
    IPage<Contract> pageList(IPage<Contract> page, Contract query);

    /**
     * 创建合同
     */
    boolean createContract(Contract contract);

    /**
     * 签署合同
     */
    boolean signContract(Long id);

    /**
     * 作废合同
     */
    boolean cancelContract(Long id);

    /**
     * 生成合同编号
     */
    String generateContractNo();
}
