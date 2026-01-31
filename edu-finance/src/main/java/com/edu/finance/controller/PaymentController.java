package com.edu.finance.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.R;
import com.edu.finance.domain.dto.ArrearsQueryDTO;
import com.edu.finance.domain.entity.Payment;
import com.edu.finance.domain.vo.ArrearsRemindVO;
import com.edu.finance.domain.vo.ArrearsStatisticsVO;
import com.edu.finance.domain.vo.ArrearsVO;
import com.edu.finance.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Operation(summary = "分页查询欠费记录", description = "支持多维度查询：校区、学员、合同、欠费金额范围、欠费天数")
    @GetMapping("/arrears/page")
    public R<Page<ArrearsVO>> getArrearsPage(
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId,
            @Parameter(description = "学员ID") @RequestParam(required = false) Long studentId,
            @Parameter(description = "学员姓名") @RequestParam(required = false) String studentName,
            @Parameter(description = "合同ID") @RequestParam(required = false) Long contractId,
            @Parameter(description = "合同编号") @RequestParam(required = false) String contractNo,
            @Parameter(description = "最小欠费金额") @RequestParam(required = false) java.math.BigDecimal minArrearsAmount,
            @Parameter(description = "最大欠费金额") @RequestParam(required = false) java.math.BigDecimal maxArrearsAmount,
            @Parameter(description = "最小欠费天数") @RequestParam(required = false) Integer minArrearsDays,
            @Parameter(description = "最大欠费天数") @RequestParam(required = false) Integer maxArrearsDays,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {

        ArrearsQueryDTO query = new ArrearsQueryDTO();
        query.setCampusId(campusId);
        query.setStudentId(studentId);
        query.setStudentName(studentName);
        query.setContractId(contractId);
        query.setContractNo(contractNo);
        query.setMinArrearsAmount(minArrearsAmount);
        query.setMaxArrearsAmount(maxArrearsAmount);
        query.setMinArrearsDays(minArrearsDays);
        query.setMaxArrearsDays(maxArrearsDays);
        query.setPageNum(pageNum);
        query.setPageSize(pageSize);

        return R.ok(paymentService.getArrearsPage(query));
    }

    @Operation(summary = "欠费统计", description = "统计总欠费金额、欠费人数、欠费合同数等")
    @GetMapping("/arrears/statistics")
    public R<ArrearsStatisticsVO> getArrearsStatistics(
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId,
            @Parameter(description = "学员ID") @RequestParam(required = false) Long studentId,
            @Parameter(description = "合同ID") @RequestParam(required = false) Long contractId) {

        ArrearsQueryDTO query = new ArrearsQueryDTO();
        query.setCampusId(campusId);
        query.setStudentId(studentId);
        query.setContractId(contractId);

        return R.ok(paymentService.getArrearsStatistics(query));
    }

    @Operation(summary = "获取需要提醒的欠费记录", description = "获取欠费天数超过阈值的记录，用于欠费提醒")
    @GetMapping("/arrears/remind")
    public R<List<ArrearsRemindVO>> getArrearsRemind(
            @Parameter(description = "最小欠费天数阈值（默认7天）") @RequestParam(defaultValue = "7") Integer minDays) {
        return R.ok(paymentService.getArrearsRemind(minDays));
    }
}
