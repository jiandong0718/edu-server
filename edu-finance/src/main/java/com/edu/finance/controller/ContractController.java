package com.edu.finance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.R;
import com.edu.finance.domain.dto.ContractApprovalProcessDTO;
import com.edu.finance.domain.dto.ContractApprovalSubmitDTO;
import com.edu.finance.domain.dto.ContractPrintDTO;
import com.edu.finance.domain.entity.Contract;
import com.edu.finance.domain.entity.ContractApproval;
import com.edu.finance.domain.entity.ContractApprovalFlow;
import com.edu.finance.domain.entity.ContractPrintRecord;
import com.edu.finance.domain.entity.ContractPrintTemplate;
import com.edu.finance.service.ContractApprovalService;
import com.edu.finance.service.ContractPdfGeneratorService;
import com.edu.finance.service.ContractPdfService;
import com.edu.finance.service.ContractPrintService;
import com.edu.finance.service.ContractService;
import com.edu.framework.security.SecurityContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 合同管理控制器
 */
@Tag(name = "合同管理")
@RestController
@RequestMapping("/finance/contract")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;
    private final ContractPdfService contractPdfService;
    private final ContractPdfGeneratorService contractPdfGeneratorService;
    private final ContractApprovalService contractApprovalService;
    private final ContractPrintService contractPrintService;

    @Operation(summary = "分页查询合同列表")
    @GetMapping("/page")
    public R<Page<Contract>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            Contract query) {
        Page<Contract> page = new Page<>(pageNum, pageSize);
        contractService.pageList(page, query);
        return R.ok(page);
    }

    @Operation(summary = "获取合同详情")
    @GetMapping("/{id}")
    public R<Contract> getById(@PathVariable Long id) {
        return R.ok(contractService.getById(id));
    }

    @Operation(summary = "创建合同")
    @PostMapping
    public R<Boolean> create(@RequestBody Contract contract) {
        return R.ok(contractService.createContract(contract));
    }

    @Operation(summary = "修改合同")
    @PutMapping
    public R<Boolean> update(@RequestBody Contract contract) {
        return R.ok(contractService.updateById(contract));
    }

    @Operation(summary = "签署合同")
    @PutMapping("/{id}/sign")
    public R<Boolean> sign(@PathVariable Long id) {
        return R.ok(contractService.signContract(id));
    }

    @Operation(summary = "作废合同")
    @PutMapping("/{id}/cancel")
    public R<Boolean> cancel(@PathVariable Long id) {
        return R.ok(contractService.cancelContract(id));
    }

    @Operation(summary = "删除合同")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(contractService.removeById(id));
    }

    @Operation(summary = "生成合同PDF")
    @PostMapping("/{id}/pdf")
    public R<String> generatePdf(@PathVariable Long id) {
        String fileUrl = contractPdfService.generateContractPdf(id);
        return R.ok(fileUrl);
    }

    @Operation(summary = "预览合同HTML")
    @GetMapping("/{id}/preview")
    public R<String> previewHtml(@PathVariable Long id) {
        String html = contractPdfService.previewContractHtml(id);
        return R.ok(html);
    }

    // ==================== 审批相关接口 ====================

    @Operation(summary = "提交审批")
    @PostMapping("/approval/submit")
    public R<Long> submitApproval(@Valid @RequestBody ContractApprovalSubmitDTO submitDTO) {
        Long approvalId = contractApprovalService.submitApproval(submitDTO);
        return R.ok(approvalId);
    }

    @Operation(summary = "处理审批")
    @PostMapping("/approval/process")
    public R<Boolean> processApproval(@Valid @RequestBody ContractApprovalProcessDTO processDTO) {
        Boolean result = contractApprovalService.processApproval(processDTO);
        return R.ok(result);
    }

    @Operation(summary = "撤销审批")
    @PostMapping("/approval/{id}/cancel")
    public R<Boolean> cancelApproval(@PathVariable Long id) {
        Boolean result = contractApprovalService.cancelApproval(id);
        return R.ok(result);
    }

    @Operation(summary = "获取审批历史")
    @GetMapping("/{id}/approval/history")
    public R<List<ContractApproval>> getApprovalHistory(@PathVariable Long id) {
        List<ContractApproval> history = contractApprovalService.getApprovalHistory(id);
        return R.ok(history);
    }

    @Operation(summary = "获取审批流程")
    @GetMapping("/approval/{id}/flow")
    public R<List<ContractApprovalFlow>> getApprovalFlow(@PathVariable Long id) {
        List<ContractApprovalFlow> flow = contractApprovalService.getApprovalFlow(id);
        return R.ok(flow);
    }

    @Operation(summary = "获取待审批列表")
    @GetMapping("/approval/pending")
    public R<List<ContractApproval>> getPendingApprovals() {
        Long approverId = SecurityContextHolder.getUserId();
        List<ContractApproval> approvals = contractApprovalService.getPendingApprovals(approverId);
        return R.ok(approvals);
    }

    // ==================== 打印相关接口 ====================

    @Operation(summary = "打印合同")
    @PostMapping("/print")
    public R<Long> printContract(@Valid @RequestBody ContractPrintDTO printDTO) {
        Long printId = contractPrintService.printContract(printDTO);
        return R.ok(printId);
    }

    @Operation(summary = "批量打印合同")
    @PostMapping("/print/batch")
    public R<List<Long>> batchPrintContracts(
            @RequestParam List<Long> contractIds,
            @RequestParam(required = false) Long templateId) {
        List<Long> printIds = contractPrintService.batchPrintContracts(contractIds, templateId);
        return R.ok(printIds);
    }

    @Operation(summary = "获取打印记录")
    @GetMapping("/{id}/print/records")
    public R<List<ContractPrintRecord>> getPrintRecords(@PathVariable Long id) {
        List<ContractPrintRecord> records = contractPrintService.getPrintRecords(id);
        return R.ok(records);
    }

    @Operation(summary = "获取打印模板列表")
    @GetMapping("/print/templates")
    public R<List<ContractPrintTemplate>> getTemplateList() {
        List<ContractPrintTemplate> templates = contractPrintService.getTemplateList();
        return R.ok(templates);
    }

    @Operation(summary = "预览打印内容")
    @GetMapping("/{id}/print/preview")
    public R<String> previewPrint(
            @PathVariable Long id,
            @RequestParam(required = false) Long templateId) {
        String html = contractPrintService.previewPrint(id, templateId);
        return R.ok(html);
    }

    // ==================== PDF生成相关接口（使用iText 7直接生成）====================

    @Operation(summary = "生成合同PDF（iText 7直接生成）")
    @PostMapping("/{id}/generate-pdf")
    public R<String> generatePdf(@PathVariable Long id) {
        String fileUrl = contractPdfGeneratorService.generateAndSavePdf(id);
        return R.ok(fileUrl);
    }

    @Operation(summary = "下载合同PDF")
    @GetMapping("/{id}/download-pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
        try {
            // 获取合同信息用于文件名
            Contract contract = contractService.getById(id);
            String fileName = "contract_" + contract.getContractNo() + ".pdf";

            // 生成PDF字节数组
            byte[] pdfBytes = contractPdfGeneratorService.downloadPdf(id);

            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment",
                    URLEncoder.encode(fileName, StandardCharsets.UTF_8));
            headers.setContentLength(pdfBytes.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "预览合同PDF（在线查看）")
    @GetMapping("/{id}/preview-pdf")
    public ResponseEntity<byte[]> previewPdf(@PathVariable Long id) {
        try {
            // 生成PDF字节数组
            byte[] pdfBytes = contractPdfGeneratorService.downloadPdf(id);

            // 设置响应头为inline，浏览器会尝试在线预览
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "contract.pdf");
            headers.setContentLength(pdfBytes.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
