package com.edu.finance.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.exception.BusinessException;
import com.edu.finance.domain.dto.AccountsReceivableQueryDTO;
import com.edu.finance.domain.dto.CollectionReminderDTO;
import com.edu.finance.domain.entity.Contract;
import com.edu.finance.domain.vo.AccountsReceivableVO;
import com.edu.finance.domain.vo.AgingAnalysisVO;
import com.edu.finance.domain.vo.CampusArrearsStatisticsVO;
import com.edu.finance.event.ArrearsRemindEvent;
import com.edu.finance.mapper.AccountsReceivableReportMapper;
import com.edu.finance.mapper.ContractMapper;
import com.edu.finance.service.AccountsReceivableReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * 应收账款报表服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountsReceivableReportServiceImpl implements AccountsReceivableReportService {

    private final AccountsReceivableReportMapper accountsReceivableReportMapper;
    private final ContractMapper contractMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Page<AccountsReceivableVO> getAccountsReceivablePage(AccountsReceivableQueryDTO query) {
        Page<AccountsReceivableVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        return accountsReceivableReportMapper.selectAccountsReceivablePage(page, query);
    }

    @Override
    public AgingAnalysisVO getAgingAnalysis(Long campusId) {
        AgingAnalysisVO result = accountsReceivableReportMapper.selectAgingAnalysis(campusId);
        if (result == null) {
            result = new AgingAnalysisVO();
            result.setWithin30DaysAmount(java.math.BigDecimal.ZERO);
            result.setWithin30DaysCount(0);
            result.setDays30To60Amount(java.math.BigDecimal.ZERO);
            result.setDays30To60Count(0);
            result.setDays60To90Amount(java.math.BigDecimal.ZERO);
            result.setDays60To90Count(0);
            result.setOver90DaysAmount(java.math.BigDecimal.ZERO);
            result.setOver90DaysCount(0);
            result.setTotalArrearsAmount(java.math.BigDecimal.ZERO);
            result.setTotalArrearsCount(0);
        }
        return result;
    }

    @Override
    public List<CampusArrearsStatisticsVO> getCampusArrearsStatistics() {
        return accountsReceivableReportMapper.selectCampusArrearsStatistics();
    }

    @Override
    public Integer sendCollectionReminder(CollectionReminderDTO dto) {
        if (dto.getContractIds() == null || dto.getContractIds().isEmpty()) {
            throw new BusinessException("合同ID列表不能为空");
        }

        int successCount = 0;
        for (Long contractId : dto.getContractIds()) {
            try {
                // 查询合同信息
                Contract contract = contractMapper.selectById(contractId);
                if (contract == null) {
                    log.warn("合同不存在，跳过催缴提醒: contractId={}", contractId);
                    continue;
                }

                // 发布催缴提醒事件
                ArrearsRemindEvent event = new ArrearsRemindEvent(this);
                event.setContractId(contractId);
                event.setStudentId(contract.getStudentId());
                event.setCampusId(contract.getCampusId());
                event.setArrearsAmount(contract.getPaidAmount().subtract(
                        contract.getReceivedAmount() != null ? contract.getReceivedAmount() : java.math.BigDecimal.ZERO
                ));
                event.setReminderType(dto.getReminderType());
                event.setCustomMessage(dto.getCustomMessage());

                eventPublisher.publishEvent(event);
                successCount++;

                log.info("发送催缴提醒成功: contractId={}, studentId={}", contractId, contract.getStudentId());
            } catch (Exception e) {
                log.error("发送催缴提醒失败: contractId={}", contractId, e);
            }
        }

        return successCount;
    }

    @Override
    public byte[] exportAccountsReceivable(AccountsReceivableQueryDTO query) {
        try {
            // 查询数据
            List<AccountsReceivableVO> dataList = accountsReceivableReportMapper.selectAccountsReceivableList(query);

            // 使用EasyExcel导出
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            EasyExcel.write(outputStream, AccountsReceivableVO.class)
                    .sheet("应收账款报表")
                    .doWrite(dataList);

            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("导出应收账款报表失败", e);
            throw new BusinessException("导出失败: " + e.getMessage());
        }
    }
}
