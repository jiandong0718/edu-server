package com.edu.finance.controller;

import com.edu.common.core.R;
import com.edu.finance.domain.dto.ReceiptDTO;
import com.edu.finance.service.ReceiptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * 收据管理控制器
 */
@Tag(name = "收据管理")
@RestController
@RequestMapping("/finance/payment/receipt")
@RequiredArgsConstructor
public class ReceiptController {

    private final ReceiptService receiptService;

    @Operation(summary = "获取收据详情", description = "根据收款记录ID获取收据详细信息")
    @GetMapping("/{paymentId}")
    public R<ReceiptDTO> getReceiptDetail(
            @Parameter(description = "收款记录ID") @PathVariable Long paymentId) {
        return R.ok(receiptService.getReceiptDetail(paymentId));
    }

    @Operation(summary = "生成收据PDF", description = "生成收据PDF文件并返回文件URL")
    @PostMapping("/generate")
    public R<String> generateReceiptPdf(
            @Parameter(description = "收款记录ID") @RequestParam Long paymentId) {
        String fileUrl = receiptService.generateReceiptPdf(paymentId);
        return R.ok(fileUrl);
    }

    @Operation(summary = "下载收据PDF", description = "直接下载收据PDF文件")
    @GetMapping("/download/{paymentId}")
    public ResponseEntity<byte[]> downloadReceiptPdf(
            @Parameter(description = "收款记录ID") @PathVariable Long paymentId) {
        ReceiptDTO receiptData = receiptService.getReceiptDetail(paymentId);
        ByteArrayOutputStream pdfStream = receiptService.generatePdfStream(receiptData);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "receipt_" + receiptData.getReceiptNo() + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfStream.toByteArray());
    }

    @Operation(summary = "预览收据", description = "预览收据HTML内容")
    @GetMapping("/preview/{paymentId}")
    public ResponseEntity<String> previewReceipt(
            @Parameter(description = "收款记录ID") @PathVariable Long paymentId) {
        String htmlContent = receiptService.previewReceiptHtml(paymentId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);

        return ResponseEntity.ok()
                .headers(headers)
                .body(htmlContent);
    }

    @Operation(summary = "批量生成收据PDF", description = "批量生成多个收据并合并为一个PDF文件")
    @PostMapping("/batch/generate")
    public R<String> generateBatchReceiptPdf(
            @Parameter(description = "收款记录ID列表") @RequestBody List<Long> paymentIds) {
        if (paymentIds == null || paymentIds.isEmpty()) {
            return R.fail("收款记录ID列表不能为空");
        }
        String fileUrl = receiptService.generateBatchReceiptPdf(paymentIds);
        return R.ok(fileUrl);
    }

    @Operation(summary = "批量下载收据PDF", description = "批量下载多个收据并合并为一个PDF文件")
    @PostMapping("/batch/download")
    public ResponseEntity<byte[]> downloadBatchReceiptPdf(
            @Parameter(description = "收款记录ID列表") @RequestBody List<Long> paymentIds) {
        if (paymentIds == null || paymentIds.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // 生成批量PDF
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (Long paymentId : paymentIds) {
            ReceiptDTO receiptData = receiptService.getReceiptDetail(paymentId);
            ByteArrayOutputStream pdfStream = receiptService.generatePdfStream(receiptData);
            try {
                outputStream.write(pdfStream.toByteArray());
            } catch (Exception e) {
                return ResponseEntity.internalServerError().build();
            }
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "receipts_batch_" + System.currentTimeMillis() + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(outputStream.toByteArray());
    }
}
