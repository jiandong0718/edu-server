package com.edu.finance.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.Result;
import com.edu.finance.domain.dto.ClassHourRuleCreateDTO;
import com.edu.finance.domain.dto.ClassHourRuleQueryDTO;
import com.edu.finance.domain.dto.ClassHourRuleUpdateDTO;
import com.edu.finance.domain.vo.ClassHourRuleVO;
import com.edu.finance.service.ClassHourRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;

/**
 * 课时消课规则控制器
 */
@Tag(name = "课时消课规则管理")
@RestController
@RequestMapping("/finance/class-hour/rule")
@RequiredArgsConstructor
public class ClassHourRuleController {

    private final ClassHourRuleService classHourRuleService;

    @Operation(summary = "创建消课规则")
    @PostMapping
    public Result<Boolean> createRule(@Valid @RequestBody ClassHourRuleCreateDTO dto) {
        boolean result = classHourRuleService.createRule(dto);
        return Result.success(result);
    }

    @Operation(summary = "更新消课规则")
    @PutMapping("/{id}")
    public Result<Boolean> updateRule(
            @Parameter(description = "规则ID") @PathVariable Long id,
            @Valid @RequestBody ClassHourRuleUpdateDTO dto) {
        dto.setId(id);
        boolean result = classHourRuleService.updateRule(dto);
        return Result.success(result);
    }

    @Operation(summary = "分页查询消课规则")
    @GetMapping("/page")
    public Result<IPage<ClassHourRuleVO>> pageQuery(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Long size,
            ClassHourRuleQueryDTO query) {
        Page<com.edu.finance.domain.entity.ClassHourRule> page = new Page<>(current, size);
        IPage<ClassHourRuleVO> result = classHourRuleService.pageQuery(page, query);
        return Result.success(result);
    }

    @Operation(summary = "根据ID查询消课规则详情")
    @GetMapping("/{id}")
    public Result<ClassHourRuleVO> getDetailById(
            @Parameter(description = "规则ID") @PathVariable Long id) {
        ClassHourRuleVO vo = classHourRuleService.getDetailById(id);
        return Result.success(vo);
    }

    @Operation(summary = "删除消课规则")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteRule(
            @Parameter(description = "规则ID") @PathVariable Long id) {
        boolean result = classHourRuleService.removeById(id);
        return Result.success(result);
    }

    @Operation(summary = "启用消课规则")
    @PutMapping("/{id}/enable")
    public Result<Boolean> enableRule(
            @Parameter(description = "规则ID") @PathVariable Long id) {
        boolean result = classHourRuleService.enableRule(id);
        return Result.success(result);
    }

    @Operation(summary = "停用消课规则")
    @PutMapping("/{id}/disable")
    public Result<Boolean> disableRule(
            @Parameter(description = "规则ID") @PathVariable Long id) {
        boolean result = classHourRuleService.disableRule(id);
        return Result.success(result);
    }

    @Operation(summary = "获取适用的消课规则")
    @GetMapping("/get-rule")
    public Result<ClassHourRuleVO> getApplicableRule(
            @Parameter(description = "课程ID") @RequestParam(required = false) Long courseId,
            @Parameter(description = "班级类型") @RequestParam(required = false) String classType,
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId) {
        com.edu.finance.domain.entity.ClassHourRule rule = classHourRuleService.getRule(courseId, classType, campusId);
        if (rule == null) {
            return Result.error("未找到适用的消课规则");
        }
        ClassHourRuleVO vo = classHourRuleService.getDetailById(rule.getId());
        return Result.success(vo);
    }

    @Operation(summary = "计算应扣减的课时数")
    @GetMapping("/calculate-deduct")
    public Result<BigDecimal> calculateDeductHours(
            @Parameter(description = "课程ID") @RequestParam(required = false) Long courseId,
            @Parameter(description = "班级类型") @RequestParam(required = false) String classType,
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId,
            @Parameter(description = "实际课时数") @RequestParam Integer classHours) {
        BigDecimal deductHours = classHourRuleService.calculateDeductHours(courseId, classType, campusId, classHours);
        return Result.success(deductHours);
    }
}
