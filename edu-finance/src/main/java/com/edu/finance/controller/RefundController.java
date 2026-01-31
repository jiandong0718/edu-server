package com.edu.finance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.R;
import com.edu.finance.domain.dto.RefundApplyDTO;
import com.edu.finance.domain.dto.RefundApproveDTO;
import com.edu.finance.domain.dto.RefundCalculationDTO;
import com.edu.finance.domain.entity.Refund;
import com.edu.finance.service.RefundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 退费管理控制器
 */
@Tag(name = "退费管理")
@RestController
@RequestMapping("/finance/refund")
@RequiredArgsConstructor
public class RefundController {

    private final RefundService refundService;

    @Operation(summary = "分页查询退费申请列表")
    @GetMapping("/page")
    public R<Page<Refund>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            Refund query) {
        Page<Refund> page = new Page<>(pageNum, pageSize);
        refundService.pageList(page, query);
        return R.ok(page);
    }

    @Operation(summary = "获取退费申请详情")
    @GetMapping("/{id}")
    public R<Refund> getById(@Parameter(description = "退费申请ID") @PathVariable Long id) {
        return R.ok(refundService.getById(id));
    }

    @Operation(summary = "计算退费金额")
    @GetMapping("/calculate/{contractId}")
    public R<RefundCalculationDTO> calculateRefundAmount(
            @Parameter(description = "合同ID") @PathVariable Long contractId) {
        return R.ok(refundService.calculateRefundAmount(contractId));
    }

    @Operation(summary = "提交退费申请")
    @PostMapping("/apply")
    public R<Long> applyRefund(@Validated @RequestBody RefundApplyDTO applyDTO) {
        Long refundId = refundService.applyRefund(applyDTO);
        return R.ok(refundId);
    }

    @Operation(summary = "审批退费申请")
    @PostMapping("/approve")
    public R<Boolean> approveRefund(@Validated @RequestBody RefundApproveDTO approveDTO) {
        return R.ok(refundService.approveRefund(approveDTO));
    }

    @Operation(summary = "执行退款")
    @PostMapping("/{id}/execute")
    public R<Boolean> executeRefund(@Parameter(description = "退费申请ID") @PathVariable Long id) {
        return R.ok(refundService.executeRefund(id));
    }

    @Operation(summary = "删除退费申请")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@Parameter(description = "退费申请ID") @PathVariable Long id) {
        Refund refund = refundService.getById(id);
        if (refund != null && "pending".equals(refund.getStatus())) {
            return R.ok(refundService.removeById(id));
        }
        return R.fail("只能删除待审批状态的退费申请");
    }
}
