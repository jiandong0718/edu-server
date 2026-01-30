package com.edu.finance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.R;
import com.edu.finance.domain.entity.Contract;
import com.edu.finance.service.ContractService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 合同管理控制器
 */
@Tag(name = "合同管理")
@RestController
@RequestMapping("/finance/contract")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;

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
}
