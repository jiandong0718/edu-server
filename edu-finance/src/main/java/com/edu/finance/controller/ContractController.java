package com.edu.finance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.edu.common.core.R;
import com.edu.finance.domain.dto.ApprovalRecordDTO;
import com.edu.finance.domain.dto.ContractBatchPrintDTO;
import com.edu.finance.domain.dto.ContractApprovalProcessDTO;
import com.edu.finance.domain.dto.ContractApprovalQueryDTO;
import com.edu.finance.domain.dto.ContractFormDTO;
import com.edu.finance.domain.dto.ContractApprovalSubmitDTO;
import com.edu.finance.domain.dto.ContractPrintDTO;
import com.edu.finance.domain.entity.Contract;
import com.edu.finance.domain.entity.ContractApproval;
import com.edu.finance.domain.entity.ContractApprovalFlow;
import com.edu.finance.domain.entity.ContractItem;
import com.edu.finance.domain.entity.ContractPrintRecord;
import com.edu.finance.domain.entity.ContractPrintTemplate;
import com.edu.finance.domain.entity.Payment;
import com.edu.finance.domain.entity.Refund;
import com.edu.finance.domain.entity.ClassHourAccount;
import com.edu.finance.domain.vo.ContractApprovalVO;
import com.edu.finance.mapper.ContractItemMapper;
import com.edu.finance.service.ClassHourAccountService;
import com.edu.finance.service.ContractApprovalService;
import com.edu.finance.service.ContractPdfGeneratorService;
import com.edu.finance.service.ContractPdfService;
import com.edu.finance.service.ContractPrintService;
import com.edu.finance.service.ContractService;
import com.edu.finance.service.PaymentService;
import com.edu.finance.service.RefundService;
import com.edu.framework.security.SecurityContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
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
    private final ContractItemMapper contractItemMapper;
    private final PaymentService paymentService;
    private final RefundService refundService;
    private final ClassHourAccountService classHourAccountService;

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

    @Operation(summary = "分页查询合同列表（兼容前端旧路径）")
    @GetMapping("/list")
    public R<Page<Contract>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            Contract query) {
        Page<Contract> result = new Page<>(page, pageSize);
        contractService.pageList(result, query);
        return R.ok(result);
    }

    @Operation(summary = "获取合同详情")
    @GetMapping("/{id}")
    public R<Contract> getById(@PathVariable Long id) {
        return R.ok(contractService.getDetail(id));
    }

    @Operation(summary = "创建合同")
    @PostMapping
    public R<Boolean> create(@RequestBody ContractFormDTO contract) {
        return R.ok(contractService.createContract(contract));
    }

    @Operation(summary = "修改合同")
    @PutMapping
    public R<Boolean> update(@RequestBody Contract contract) {
        return R.ok(contractService.updateById(contract));
    }

    @Operation(summary = "修改合同（兼容前端旧路径）")
    @PutMapping("/{id}")
    public R<Boolean> updateWithId(@PathVariable Long id, @RequestBody ContractFormDTO contract) {
        return R.ok(contractService.updateContract(id, contract));
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

    @Operation(summary = "获取合同明细列表")
    @GetMapping("/{id}/items")
    public R<List<ContractItem>> getItems(@PathVariable Long id) {
        return R.ok(contractItemMapper.selectByContractId(id));
    }

    @Operation(summary = "获取合同收款记录列表")
    @GetMapping("/{id}/payments")
    public R<List<Payment>> getPayments(@PathVariable Long id) {
        List<Payment> payments = paymentService.list(
                new LambdaQueryWrapper<Payment>()
                        .eq(Payment::getContractId, id)
                        .orderByDesc(Payment::getCreateTime)
        );
        return R.ok(payments);
    }

    @Operation(summary = "获取合同课时账户列表")
    @GetMapping("/{id}/hour-accounts")
    public R<List<ClassHourAccount>> getHourAccounts(@PathVariable Long id) {
        List<ClassHourAccount> accounts = classHourAccountService.list(
                new LambdaQueryWrapper<ClassHourAccount>()
                        .eq(ClassHourAccount::getContractId, id)
                        .orderByDesc(ClassHourAccount::getCreateTime)
        );
        return R.ok(accounts);
    }

    @Operation(summary = "获取合同退费申请列表")
    @GetMapping("/{id}/refunds")
    public R<List<Refund>> getRefunds(@PathVariable Long id) {
        List<Refund> refunds = refundService.list(
                new LambdaQueryWrapper<Refund>()
                        .eq(Refund::getContractId, id)
                        .orderByDesc(Refund::getCreateTime)
        );
        return R.ok(refunds);
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

    @Operation(summary = "分页查询待审批列表")
    @GetMapping("/approval/pending/page")
    public R<Page<ContractApprovalVO>> getPendingApprovalsPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long approverId = SecurityContextHolder.getUserId();
        Page<ContractApprovalVO> page = new Page<>(pageNum, pageSize);
        contractApprovalService.getPendingApprovalsPage(page, approverId);
        return R.ok(page);
    }

    @Operation(summary = "分页查询审批记录")
    @GetMapping("/approval/page")
    public R<Page<ContractApprovalVO>> getApprovalPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            ContractApprovalQueryDTO queryDTO) {
        Page<ContractApprovalVO> page = new Page<>(pageNum, pageSize);
        contractApprovalService.getApprovalPage(page, queryDTO);
        return R.ok(page);
    }

    @Operation(summary = "获取审批详情")
    @GetMapping("/approval/{id}/detail")
    public R<ContractApprovalVO> getApprovalDetail(@PathVariable Long id) {
        ContractApprovalVO detail = contractApprovalService.getApprovalDetail(id);
        return R.ok(detail);
    }

    @Operation(summary = "获取审批记录时间线")
    @GetMapping("/approval/{id}/timeline")
    public R<List<ApprovalRecordDTO>> getApprovalTimeline(@PathVariable Long id) {
        List<ApprovalRecordDTO> timeline = contractApprovalService.getApprovalTimeline(id);
        return R.ok(timeline);
    }

    @Operation(summary = "检查审批权限")
    @GetMapping("/approval/{id}/check-permission")
    public R<Boolean> checkApprovalPermission(@PathVariable Long id) {
        Long userId = SecurityContextHolder.getUserId();
        Boolean hasPermission = contractApprovalService.checkApprovalPermission(id, userId);
        return R.ok(hasPermission);
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
            @RequestBody(required = false) ContractBatchPrintDTO body,
            @RequestParam(required = false) List<Long> contractIds,
            @RequestParam(required = false) Long templateId) {
        List<Long> resolvedContractIds = contractIds;
        Long resolvedTemplateId = templateId;

        if (body != null) {
            if (body.getContractIds() != null && !body.getContractIds().isEmpty()) {
                resolvedContractIds = body.getContractIds();
            }
            if (body.getTemplateId() != null) {
                resolvedTemplateId = body.getTemplateId();
            }
        }

        if (resolvedContractIds == null || resolvedContractIds.isEmpty()) {
            return R.fail("合同ID列表不能为空");
        }

        List<Long> printIds = contractPrintService.batchPrintContracts(resolvedContractIds, resolvedTemplateId);
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

    @Operation(summary = "下载合同PDF（兼容前端旧路径）")
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable Long id) {
        return downloadPdf(id);
    }
}
