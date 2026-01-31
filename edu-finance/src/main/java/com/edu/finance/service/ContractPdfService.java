package com.edu.finance.service;

import com.edu.finance.domain.dto.ContractPdfDTO;

import java.io.ByteArrayOutputStream;

/**
 * 合同PDF服务接口
 */
public interface ContractPdfService {

    /**
     * 生成合同PDF
     *
     * @param contractId 合同ID
     * @return 文件URL
     */
    String generateContractPdf(Long contractId);

    /**
     * 生成合同PDF（使用自定义数据）
     *
     * @param pdfData PDF数据
     * @return PDF字节流
     */
    ByteArrayOutputStream generatePdfStream(ContractPdfDTO pdfData);

    /**
     * 预览合同PDF（返回HTML）
     *
     * @param contractId 合同ID
     * @return HTML内容
     */
    String previewContractHtml(Long contractId);
}
