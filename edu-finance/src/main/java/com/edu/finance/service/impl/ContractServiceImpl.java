package com.edu.finance.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.exception.BusinessException;
import com.edu.finance.domain.entity.Contract;
import com.edu.finance.mapper.ContractMapper;
import com.edu.finance.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 合同服务实现
 */
@Service
@RequiredArgsConstructor
public class ContractServiceImpl extends ServiceImpl<ContractMapper, Contract> implements ContractService {

    @Override
    public IPage<Contract> pageList(IPage<Contract> page, Contract query) {
        return baseMapper.selectContractPage(page, query);
    }

    @Override
    public boolean createContract(Contract contract) {
        if (StrUtil.isBlank(contract.getContractNo())) {
            contract.setContractNo(generateContractNo());
        }
        contract.setStatus("pending");
        contract.setReceivedAmount(java.math.BigDecimal.ZERO);
        return save(contract);
    }

    @Override
    public boolean signContract(Long id) {
        Contract contract = getById(id);
        if (contract == null) {
            throw new BusinessException("合同不存在");
        }
        if (!"pending".equals(contract.getStatus())) {
            throw new BusinessException("只有待签署状态的合同才能签署");
        }
        contract.setStatus("signed");
        contract.setSignDate(LocalDate.now());
        return updateById(contract);
    }

    @Override
    public boolean cancelContract(Long id) {
        Contract contract = getById(id);
        if (contract == null) {
            throw new BusinessException("合同不存在");
        }
        if ("completed".equals(contract.getStatus()) || "refunded".equals(contract.getStatus())) {
            throw new BusinessException("已完成或已退费的合同不能作废");
        }
        contract.setStatus("cancelled");
        return updateById(contract);
    }

    @Override
    public String generateContractNo() {
        String prefix = "HT" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        LambdaQueryWrapper<Contract> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(Contract::getContractNo, prefix)
                .orderByDesc(Contract::getContractNo)
                .last("LIMIT 1");
        Contract lastContract = getOne(wrapper);

        int seq = 1;
        if (lastContract != null && lastContract.getContractNo() != null) {
            String lastNo = lastContract.getContractNo();
            if (lastNo.length() > prefix.length()) {
                seq = Integer.parseInt(lastNo.substring(prefix.length())) + 1;
            }
        }
        return prefix + String.format("%04d", seq);
    }
}
