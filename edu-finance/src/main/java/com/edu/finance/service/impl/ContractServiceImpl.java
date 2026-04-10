package com.edu.finance.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.exception.BusinessException;
import com.edu.finance.domain.dto.ContractFormDTO;
import com.edu.finance.domain.dto.ContractFormItemDTO;
import com.edu.finance.domain.entity.Contract;
import com.edu.finance.domain.entity.ContractItem;
import com.edu.finance.mapper.ContractItemMapper;
import com.edu.finance.mapper.ContractMapper;
import com.edu.finance.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 合同服务实现
 */
@Service
@RequiredArgsConstructor
public class ContractServiceImpl extends ServiceImpl<ContractMapper, Contract> implements ContractService {

    private final ContractItemMapper contractItemMapper;

    @Override
    public IPage<Contract> pageList(IPage<Contract> page, Contract query) {
        return baseMapper.selectContractPage(page, query);
    }

    @Override
    public Contract getDetail(Long id) {
        return baseMapper.selectContractDetail(id);
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
    @Transactional(rollbackFor = Exception.class)
    public boolean createContract(ContractFormDTO contractForm) {
        Contract contract = new Contract();
        applyContractForm(contract, contractForm);

        createContract(contract);
        syncContractItems(contract.getId(), contractForm.getItems());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateContract(Long id, ContractFormDTO contractForm) {
        Contract contract = getById(id);
        if (contract == null) {
            throw new BusinessException("合同不存在");
        }
        if (!"pending".equals(contract.getStatus())) {
            throw new BusinessException("只有待签署状态的合同才能修改");
        }

        applyContractForm(contract, contractForm);

        BigDecimal receivedAmount = contract.getReceivedAmount() == null ? BigDecimal.ZERO : contract.getReceivedAmount();
        if (receivedAmount.compareTo(contract.getPaidAmount()) > 0) {
            throw new BusinessException("修改后的应付金额不能小于已收金额");
        }

        boolean updated = updateById(contract);
        if (!updated) {
            return false;
        }

        syncContractItems(contract.getId(), contractForm.getItems());
        return true;
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

    private void applyContractForm(Contract contract, ContractFormDTO contractForm) {
        ContractTotals totals = calculateContractTotals(contractForm.getItems());
        contract.setStudentId(contractForm.getStudentId());
        contract.setCampusId(contractForm.getCampusId());
        contract.setType(contractForm.getType());
        contract.setSignDate(contractForm.getSignDate());
        contract.setEffectiveDate(contractForm.getStartDate());
        contract.setExpireDate(contractForm.getEndDate());
        contract.setSalesId(contractForm.getSalesPersonId());
        contract.setRemark(contractForm.getRemark());
        contract.setAmount(totals.totalAmount());
        contract.setPaidAmount(totals.totalAmount());
        contract.setDiscountAmount(totals.totalDiscount());
        contract.setTotalHours(totals.totalHours());
    }

    private ContractTotals calculateContractTotals(List<ContractFormItemDTO> items) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        int totalHours = 0;

        if (items != null) {
            for (ContractFormItemDTO item : items) {
                BigDecimal unitPrice = item.getUnitPrice() == null ? BigDecimal.ZERO : item.getUnitPrice();
                int quantity = item.getQuantity() == null ? 0 : item.getQuantity();
                BigDecimal discount = item.getDiscountAmount() == null ? BigDecimal.ZERO : item.getDiscountAmount();
                totalAmount = totalAmount.add(unitPrice.multiply(BigDecimal.valueOf(quantity)).subtract(discount));
                totalDiscount = totalDiscount.add(discount);
                totalHours += quantity;
            }
        }

        return new ContractTotals(totalAmount, totalDiscount, totalHours);
    }

    private void syncContractItems(Long contractId, List<ContractFormItemDTO> items) {
        contractItemMapper.delete(new LambdaQueryWrapper<ContractItem>()
                .eq(ContractItem::getContractId, contractId));

        if (items == null) {
            return;
        }

        for (ContractFormItemDTO formItem : items) {
            ContractItem item = new ContractItem();
            item.setContractId(contractId);
            item.setCourseId(formItem.getCourseId());
            item.setQuantity(formItem.getQuantity());
            item.setHours(formItem.getQuantity());
            item.setUnitPrice(formItem.getUnitPrice() == null ? BigDecimal.ZERO : formItem.getUnitPrice());
            item.setAmount(
                    (formItem.getUnitPrice() == null ? BigDecimal.ZERO : formItem.getUnitPrice())
                            .multiply(BigDecimal.valueOf(formItem.getQuantity() == null ? 0 : formItem.getQuantity()))
                            .subtract(formItem.getDiscountAmount() == null ? BigDecimal.ZERO : formItem.getDiscountAmount())
            );
            contractItemMapper.insert(item);
        }
    }

    private record ContractTotals(BigDecimal totalAmount, BigDecimal totalDiscount, int totalHours) {
    }
}
