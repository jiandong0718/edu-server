package com.edu.finance.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.Result;
import com.edu.finance.domain.dto.ClassHourAccountCreateDTO;
import com.edu.finance.domain.entity.ClassHourAccount;
import com.edu.finance.domain.vo.ClassHourAccountVO;
import com.edu.finance.service.ClassHourAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 课时账户控制器
 */
@Tag(name = "课时账户管理")
@RestController
@RequestMapping("/finance/class-hour-account")
@RequiredArgsConstructor
public class ClassHourAccountController {

    private final ClassHourAccountService classHourAccountService;

    @Operation(summary = "分页查询课时账户")
    @GetMapping("/page")
    public Result<IPage<ClassHourAccount>> page(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            ClassHourAccount query) {
        Page<ClassHourAccount> page = new Page<>(current, size);
        IPage<ClassHourAccount> result = classHourAccountService.page(page);
        return Result.success(result);
    }

    @Operation(summary = "根据ID查询课时账户")
    @GetMapping("/{id}")
    public Result<ClassHourAccount> getById(@PathVariable Long id) {
        ClassHourAccount account = classHourAccountService.getById(id);
        return Result.success(account);
    }

    @Operation(summary = "查询学员的课时账户列表")
    @GetMapping("/student/{studentId}")
    public Result<List<ClassHourAccountVO>> getByStudentId(@PathVariable Long studentId) {
        List<ClassHourAccountVO> accounts = classHourAccountService.getByStudentId(studentId);
        return Result.success(accounts);
    }

    @Operation(summary = "创建课时账户")
    @PostMapping
    public Result<Boolean> create(@RequestBody ClassHourAccountCreateDTO dto) {
        boolean result = classHourAccountService.createAccount(dto);
        return Result.success(result);
    }

    @Operation(summary = "根据合同创建课时账户")
    @PostMapping("/create-by-contract/{contractId}")
    public Result<Boolean> createByContract(@PathVariable Long contractId) {
        boolean result = classHourAccountService.createAccountByContract(contractId);
        return Result.success(result);
    }

    @Operation(summary = "冻结课时账户")
    @PutMapping("/{id}/freeze")
    public Result<Boolean> freeze(@PathVariable Long id) {
        boolean result = classHourAccountService.freezeAccount(id);
        return Result.success(result);
    }

    @Operation(summary = "解冻课时账户")
    @PutMapping("/{id}/unfreeze")
    public Result<Boolean> unfreeze(@PathVariable Long id) {
        boolean result = classHourAccountService.unfreezeAccount(id);
        return Result.success(result);
    }

    @Operation(summary = "调整课时余额")
    @PutMapping("/{id}/adjust")
    public Result<Boolean> adjust(
            @PathVariable Long id,
            @RequestParam BigDecimal hours,
            @RequestParam(required = false) String remark) {
        boolean result = classHourAccountService.adjustHours(id, hours, remark);
        return Result.success(result);
    }

    @Operation(summary = "删除课时账户")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean result = classHourAccountService.removeById(id);
        return Result.success(result);
    }
}
