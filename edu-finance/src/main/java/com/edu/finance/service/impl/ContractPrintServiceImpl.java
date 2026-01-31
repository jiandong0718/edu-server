package com.edu.finance.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.exception.BusinessException;
import com.edu.finance.domain.dto.ContractPrintDTO;
import com.edu.finance.domain.entity.Contract;
import com.edu.finance.domain.entity.ContractPrintRecord;
import com.edu.finance.domain.entity.ContractPrintTemplate;
import com.edu.finance.mapper.ContractMapper;
import com.edu.finance.mapper.ContractPrintRecordMapper;
import com.edu.finance.mapper.ContractPrintTemplateMapper;
import com.edu.finance.service.ContractPdfService;
import com.edu.finance.service.ContractPrintService;
import com.edu.framework.security.SecurityContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 合同打印服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContractPrintServiceImpl extends ServiceImpl<ContractPrintRecordMapper, ContractPrintRecord>
        implements ContractPrintService {

    private final ContractPrintRecordMapper printRecordMapper;
    private final ContractPrintTemplateMapper printTemplateMapper;
    private final ContractMapper contractMapper;
    private final ContractPdfService contractPdfService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long printContract(ContractPrintDTO printDTO) {
        // 验证合同是否存在
        Contract contract = contractMapper.selectById(printDTO.getContractId());
        if (contract == null) {
            throw new BusinessException("合同不存在");
        }

        // 获取打印模板
        ContractPrintTemplate template = null;
        if (printDTO.getTemplateId() != null) {
            template = printTemplateMapper.selectById(printDTO.getTemplateId());
            if (template == null) {
                throw new BusinessException("打印模板不存在");
            }
        } else {
            template = printTemplateMapper.selectDefaultTemplate();
            if (template == null) {
                throw new BusinessException("未找到默认打印模板");
            }
        }

        // 生成PDF文件（如果是PDF打印）
        String fileUrl = null;
        if ("pdf".equals(printDTO.getPrintType())) {
            fileUrl = contractPdfService.generateContractPdf(printDTO.getContractId());
        }

        // 创建打印记录
        ContractPrintRecord record = new ContractPrintRecord();
        record.setContractId(printDTO.getContractId());
        record.setPrintNo(generatePrintNo());
        record.setTemplateId(template.getId());
        record.setTemplateName(template.getTemplateName());
        record.setPrintType(printDTO.getPrintType());
        record.setPrintCount(printDTO.getPrintCount());
        record.setFileUrl(fileUrl);
        record.setPrinterId(SecurityContextHolder.getUserId());
        record.setPrinterName(SecurityContextHolder.getUsername());
        record.setPrintTime(LocalDateTime.now());
        record.setRemark(printDTO.getRemark());

        printRecordMapper.insert(record);

        log.info("合同打印成功: printId={}, contractId={}, printType={}",
                record.getId(), printDTO.getContractId(), printDTO.getPrintType());
        return record.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Long> batchPrintContracts(List<Long> contractIds, Long templateId) {
        List<Long> printIds = new ArrayList<>();

        for (Long contractId : contractIds) {
            try {
                ContractPrintDTO printDTO = new ContractPrintDTO();
                printDTO.setContractId(contractId);
                printDTO.setTemplateId(templateId);
                printDTO.setPrintType("pdf");
                printDTO.setPrintCount(1);

                Long printId = printContract(printDTO);
                printIds.add(printId);
            } catch (Exception e) {
                log.error("批量打印合同失败: contractId={}", contractId, e);
                // 继续处理下一个合同
            }
        }

        log.info("批量打印合同完成: total={}, success={}", contractIds.size(), printIds.size());
        return printIds;
    }

    @Override
    public List<ContractPrintRecord> getPrintRecords(Long contractId) {
        return printRecordMapper.selectByContractId(contractId);
    }

    @Override
    public List<ContractPrintTemplate> getTemplateList() {
        LambdaQueryWrapper<ContractPrintTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ContractPrintTemplate::getStatus, "active")
                .orderByAsc(ContractPrintTemplate::getSortOrder)
                .orderByDesc(ContractPrintTemplate::getCreateTime);
        return printTemplateMapper.selectList(wrapper);
    }

    @Override
    public ContractPrintTemplate getDefaultTemplate() {
        return printTemplateMapper.selectDefaultTemplate();
    }

    @Override
    public String previewPrint(Long contractId, Long templateId) {
        // 验证合同是否存在
        Contract contract = contractMapper.selectById(contractId);
        if (contract == null) {
            throw new BusinessException("合同不存在");
        }

        // 获取打印模板
        ContractPrintTemplate template = null;
        if (templateId != null) {
            template = printTemplateMapper.selectById(templateId);
        } else {
            template = printTemplateMapper.selectDefaultTemplate();
        }

        if (template == null) {
            throw new BusinessException("打印模板不存在");
        }

        // 如果模板有自定义内容，使用模板内容
        // 否则使用默认的PDF预览
        if (StrUtil.isNotBlank(template.getTemplateContent())) {
            // TODO: 实现模板变量替换
            return template.getTemplateContent();
        } else {
            // 使用默认的PDF预览
            return contractPdfService.previewContractHtml(contractId);
        }
    }

    /**
     * 生成打印单号
     */
    private String generatePrintNo() {
        String prefix = "PR" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        LambdaQueryWrapper<ContractPrintRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(ContractPrintRecord::getPrintNo, prefix)
                .orderByDesc(ContractPrintRecord::getPrintNo)
                .last("LIMIT 1");
        ContractPrintRecord lastRecord = getOne(wrapper);

        int seq = 1;
        if (lastRecord != null && StrUtil.isNotBlank(lastRecord.getPrintNo())) {
            String lastNo = lastRecord.getPrintNo();
            if (lastNo.length() > prefix.length()) {
                seq = Integer.parseInt(lastNo.substring(prefix.length())) + 1;
            }
        }
        return prefix + String.format("%04d", seq);
    }
}
