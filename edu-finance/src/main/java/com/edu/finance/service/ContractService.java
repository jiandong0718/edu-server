package com.edu.finance.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.finance.domain.dto.ContractFormDTO;
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
     * 查询合同详情（含关联信息）
     */
    Contract getDetail(Long id);

    /**
     * 创建合同
     */
    boolean createContract(Contract contract);

    /**
     * 前端表单兼容创建合同
     */
    boolean createContract(ContractFormDTO contractForm);

    /**
     * 前端表单兼容更新合同
     */
    boolean updateContract(Long id, ContractFormDTO contractForm);

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
