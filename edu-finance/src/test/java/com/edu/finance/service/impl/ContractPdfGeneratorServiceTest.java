package com.edu.finance.service.impl;

import com.edu.finance.domain.entity.Contract;
import com.edu.finance.mapper.ContractMapper;
import com.edu.finance.service.ContractPdfGeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileOutputStream;

/**
 * 合同PDF生成服务测试
 */
@Slf4j
@SpringBootTest
public class ContractPdfGeneratorServiceTest {

    @Autowired
    private ContractPdfGeneratorService contractPdfGeneratorService;

    @Autowired
    private ContractMapper contractMapper;

    /**
     * 测试生成并保存PDF
     */
    @Test
    public void testGenerateAndSavePdf() {
        // 查询一个已签署的合同
        Contract contract = contractMapper.selectById(1L);
        if (contract != null && "signed".equals(contract.getStatus())) {
            String fileUrl = contractPdfGeneratorService.generateAndSavePdf(1L);
            log.info("PDF生成成功，文件URL: {}", fileUrl);
        } else {
            log.warn("未找到已签署的合同");
        }
    }

    /**
     * 测试下载PDF
     */
    @Test
    public void testDownloadPdf() throws Exception {
        byte[] pdfBytes = contractPdfGeneratorService.downloadPdf(1L);

        // 保存到本地文件
        String outputPath = "/tmp/contract_test.pdf";
        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
            fos.write(pdfBytes);
            log.info("PDF已保存到: {}", outputPath);
        }
    }

    /**
     * 测试检查是否可以生成PDF
     */
    @Test
    public void testCanGeneratePdf() {
        boolean canGenerate = contractPdfGeneratorService.canGeneratePdf(1L);
        log.info("合同ID=1是否可以生成PDF: {}", canGenerate);
    }

    /**
     * 测试生成PDF到流
     */
    @Test
    public void testGeneratePdfToStream() throws Exception {
        String outputPath = "/tmp/contract_stream_test.pdf";
        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
            contractPdfGeneratorService.generatePdfToStream(1L, fos);
            log.info("PDF已通过流保存到: {}", outputPath);
        }
    }
}
