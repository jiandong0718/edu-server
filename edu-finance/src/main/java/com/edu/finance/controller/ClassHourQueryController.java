package com.edu.finance.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.Result;
import com.edu.finance.domain.dto.ClassHourBalanceQueryDTO;
import com.edu.finance.domain.vo.ClassHourBalanceVO;
import com.edu.finance.service.ClassHourQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 课时查询控制器
 */
@Tag(name = "课时查询管理")
@RestController
@RequestMapping("/finance/class-hour")
@RequiredArgsConstructor
public class ClassHourQueryController {

    private final ClassHourQueryService classHourQueryService;

    @Operation(summary = "查询学员课时余额", description = "根据学员ID查询其课时余额信息")
    @GetMapping("/balance/{studentId}")
    public Result<ClassHourBalanceVO> getBalanceByStudent(
            @Parameter(description = "学员ID", required = true)
            @PathVariable Long studentId) {
        ClassHourBalanceVO balance = classHourQueryService.getBalanceByStudent(studentId);
        return Result.success(balance);
    }

    @Operation(summary = "查询账户详情", description = "根据账户ID查询课时账户详细信息")
    @GetMapping("/balance/detail/{accountId}")
    public Result<ClassHourBalanceVO> getBalanceDetail(
            @Parameter(description = "账户ID", required = true)
            @PathVariable Long accountId) {
        ClassHourBalanceVO detail = classHourQueryService.getBalanceDetail(accountId);
        return Result.success(detail);
    }

    @Operation(summary = "分页查询课时账户", description = "支持按学员、课程、校区、状态筛选")
    @GetMapping("/balance/page")
    public Result<IPage<ClassHourBalanceVO>> pageBalance(
            @Parameter(description = "当前页", required = true)
            @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小", required = true)
            @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "查询条件")
            ClassHourBalanceQueryDTO query) {
        Page<ClassHourBalanceVO> page = new Page<>(current, size);
        IPage<ClassHourBalanceVO> result = classHourQueryService.pageBalance(page, query);
        return Result.success(result);
    }
}
