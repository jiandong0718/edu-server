package com.edu.finance.service;

import com.edu.finance.domain.dto.ReceiptDTO;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * 收据服务接口
 */
public interface ReceiptService {

    /**
     * 获取收据详情
     *
     * @param paymentId 收款记录ID
     * @return 收据详情
     */
    ReceiptDTO getReceiptDetail(Long paymentId);

    /**
     * 生成收据PDF
     *
     * @param paymentId 收款记录ID
     * @return PDF文件URL
     */
    String generateReceiptPdf(Long paymentId);

    /**
     * 生成收据PDF流
     *
     * @param receiptData 收据数据
     * @return PDF字节流
     */
    ByteArrayOutputStream generatePdfStream(ReceiptDTO receiptData);

    /**
     * 预览收据HTML
     *
     * @param paymentId 收款记录ID
     * @return HTML内容
     */
    String previewReceiptHtml(Long paymentId);

    /**
     * 批量生成收据PDF（合并为一个文件）
     *
     * @param paymentIds 收款记录ID列表
     * @return PDF文件URL
     */
    String generateBatchReceiptPdf(List<Long> paymentIds);

    /**
     * 生成收据编号
     *
     * @return 收据编号
     */
    String generateReceiptNo();
}
