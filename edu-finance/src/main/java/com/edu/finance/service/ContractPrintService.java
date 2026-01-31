package com.edu.finance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.finance.domain.dto.ContractPrintDTO;
import com.edu.finance.domain.entity.ContractPrintRecord;
import com.edu.finance.domain.entity.ContractPrintTemplate;

import java.util.List;

/**
 * 合同打印服务接口
 */
public interface ContractPrintService extends IService<ContractPrintRecord> {

    /**
     * 打印合同
     *
     * @param printDTO 打印信息
     * @return 打印记录ID
     */
    Long printContract(ContractPrintDTO printDTO);

    /**
     * 批量打印合同
     *
     * @param contractIds 合同ID列表
     * @param templateId 模板ID
     * @return 打印记录ID列表
     */
    List<Long> batchPrintContracts(List<Long> contractIds, Long templateId);

    /**
     * 获取打印记录
     *
     * @param contractId 合同ID
     * @return 打印记录列表
     */
    List<ContractPrintRecord> getPrintRecords(Long contractId);

    /**
     * 获取打印模板列表
     *
     * @return 模板列表
     */
    List<ContractPrintTemplate> getTemplateList();

    /**
     * 获取默认模板
     *
     * @return 默认模板
     */
    ContractPrintTemplate getDefaultTemplate();

    /**
     * 预览打印内容
     *
     * @param contractId 合同ID
     * @param templateId 模板ID
     * @return HTML内容
     */
    String previewPrint(Long contractId, Long templateId);
}
