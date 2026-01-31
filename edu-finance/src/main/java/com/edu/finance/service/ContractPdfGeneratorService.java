package com.edu.finance.service;

import java.io.OutputStream;

/**
 * 合同PDF生成服务接口
 * 使用iText 7直接生成PDF（非HTML转换）
 */
public interface ContractPdfGeneratorService {

    /**
     * 生成合同PDF并保存到文件系统
     *
     * @param contractId 合同ID
     * @return PDF文件URL
     */
    String generateAndSavePdf(Long contractId);

    /**
     * 生成合同PDF到输出流
     *
     * @param contractId 合同ID
     * @param outputStream 输出流
     */
    void generatePdfToStream(Long contractId, OutputStream outputStream);

    /**
     * 下载合同PDF
     *
     * @param contractId 合同ID
     * @return PDF文件字节数组
     */
    byte[] downloadPdf(Long contractId);

    /**
     * 检查合同是否可以生成PDF
     *
     * @param contractId 合同ID
     * @return 是否可以生成
     */
    boolean canGeneratePdf(Long contractId);
}
