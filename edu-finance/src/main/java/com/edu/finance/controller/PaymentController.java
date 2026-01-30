package com.edu.finance.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.R;
import com.edu.finance.domain.entity.Payment;
import com.edu.finance.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 收款管理控制器
 */
@Tag(name = "收款管理")
@RestController
@RequestMapping("/finance/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "分页查询收款记录")
    @GetMapping("/page")
    public R<Page<Payment>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long contractId,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) String status) {
        Page<Payment> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Payment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(contractId != null, Payment::getContractId, contractId)
                .eq(studentId != null, Payment::getStudentId, studentId)
                .eq(status != null, Payment::getStatus, status)
                .orderByDesc(Payment::getCreateTime);
        paymentService.page(page, wrapper);
        return R.ok(page);
    }

    @Operation(summary = "获取收款详情")
    @GetMapping("/{id}")
    public R<Payment> getById(@PathVariable Long id) {
        return R.ok(paymentService.getById(id));
    }

    @Operation(summary = "创建收款记录")
    @PostMapping
    public R<Boolean> create(@RequestBody Payment payment) {
        return R.ok(paymentService.createPayment(payment));
    }

    @Operation(summary = "确认收款")
    @PutMapping("/{id}/confirm")
    public R<Boolean> confirm(@PathVariable Long id, @RequestParam(required = false) String transactionNo) {
        return R.ok(paymentService.confirmPayment(id, transactionNo));
    }
}
