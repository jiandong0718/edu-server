package com.edu.finance.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.Result;
import com.edu.finance.domain.entity.ClassHourRule;
import com.edu.finance.service.ClassHourRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 课时消课规则控制器
 */
@Tag(name = "课时消课规则管理")
@RestController
@RequestMapping("/finance/class-hour-rule")
@RequiredArgsConstructor
public class ClassHourRuleController {

    private final ClassHourRuleService classHourRuleService;

    @Operation(summary = "分页查询消课规则")
    @GetMapping("/page")
    public Result<IPage<ClassHourRule>> page(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            ClassHourRule query) {
        Page<ClassHourRule> page = new Page<>(current, size);
        IPage<ClassHourRule> result = classHourRuleService.page(page);
        return Result.success(result);
    }

    @Operation(summary = "根据ID查询消课规则")
    @GetMapping("/{id}")
    public Result<ClassHourRule> getById(@PathVariable Long id) {
        ClassHourRule rule = classHourRuleService.getById(id);
        return Result.success(rule);
    }

    @Operation(summary = "获取适用的消课规则")
    @GetMapping("/get-rule")
    public Result<ClassHourRule> getRule(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String classType,
            @RequestParam(required = false) Long campusId) {
        ClassHourRule rule = classHourRuleService.getRule(courseId, classType, campusId);
        return Result.success(rule);
    }

    @Operation(summary = "计算应扣减的课时数")
    @GetMapping("/calculate-deduct")
    public Result<BigDecimal> calculateDeduct(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String classType,
            @RequestParam(required = false) Long campusId,
            @RequestParam Integer classHours) {
        BigDecimal deductHours = classHourRuleService.calculateDeductHours(courseId, classType, campusId, classHours);
        return Result.success(deductHours);
    }

    @Operation(summary = "创建消课规则")
    @PostMapping
    public Result<Boolean> create(@RequestBody ClassHourRule rule) {
        boolean result = classHourRuleService.save(rule);
        return Result.success(result);
    }

    @Operation(summary = "更新消课规则")
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody ClassHourRule rule) {
        rule.setId(id);
        boolean result = classHourRuleService.updateById(rule);
        return Result.success(result);
    }

    @Operation(summary = "删除消课规则")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean result = classHourRuleService.removeById(id);
        return Result.success(result);
    }

    @Operation(summary = "启用消课规则")
    @PutMapping("/{id}/enable")
    public Result<Boolean> enable(@PathVariable Long id) {
        ClassHourRule rule = classHourRuleService.getById(id);
        if (rule != null) {
            rule.setStatus("active");
            boolean result = classHourRuleService.updateById(rule);
            return Result.success(result);
        }
        return Result.error("规则不存在");
    }

    @Operation(summary = "停用消课规则")
    @PutMapping("/{id}/disable")
    public Result<Boolean> disable(@PathVariable Long id) {
        ClassHourRule rule = classHourRuleService.getById(id);
        if (rule != null) {
            rule.setStatus("inactive");
            boolean result = classHourRuleService.updateById(rule);
            return Result.success(result);
        }
        return Result.error("规则不存在");
    }
}
