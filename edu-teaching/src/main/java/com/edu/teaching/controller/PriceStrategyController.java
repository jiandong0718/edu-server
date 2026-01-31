package com.edu.teaching.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.R;
import com.edu.teaching.domain.dto.PriceStrategyDTO;
import com.edu.teaching.domain.entity.PriceStrategy;
import com.edu.teaching.service.PriceStrategyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 价格策略管理控制器
 */
@Tag(name = "价格策略管理")
@RestController
@RequestMapping("/teaching/price-strategy")
@RequiredArgsConstructor
public class PriceStrategyController {

    private final PriceStrategyService priceStrategyService;

    @Operation(summary = "分页查询价格策略列表")
    @GetMapping("/page")
    public R<Page<PriceStrategy>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer status) {
        Page<PriceStrategy> page = priceStrategyService.pageStrategies(pageNum, pageSize, name, type, status);
        return R.ok(page);
    }

    @Operation(summary = "获取价格策略详情")
    @GetMapping("/{id}")
    public R<PriceStrategy> getById(@PathVariable Long id) {
        PriceStrategy strategy = priceStrategyService.getById(id);
        if (strategy == null) {
            return R.fail("价格策略不存在");
        }
        return R.ok(strategy);
    }

    @Operation(summary = "新增价格策略")
    @PostMapping
    public R<Boolean> add(@RequestBody PriceStrategyDTO dto) {
        // 验证策略配置
        if (!priceStrategyService.validateStrategy(dto)) {
            return R.fail("价格策略配置不合法");
        }
        boolean result = priceStrategyService.createStrategy(dto);
        return result ? R.ok(true) : R.fail("创建价格策略失败");
    }

    @Operation(summary = "修改价格策略")
    @PutMapping
    public R<Boolean> update(@RequestBody PriceStrategyDTO dto) {
        if (dto.getId() == null) {
            return R.fail("价格策略ID不能为空");
        }
        // 验证策略配置
        if (!priceStrategyService.validateStrategy(dto)) {
            return R.fail("价格策略配置不合法");
        }
        boolean result = priceStrategyService.updateStrategy(dto);
        return result ? R.ok(true) : R.fail("更新价格策略失败");
    }

    @Operation(summary = "删除价格策略")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        boolean result = priceStrategyService.removeById(id);
        return result ? R.ok(true) : R.fail("删除价格策略失败");
    }

    @Operation(summary = "启用价格策略")
    @PutMapping("/{id}/enable")
    public R<Boolean> enable(@PathVariable Long id) {
        boolean result = priceStrategyService.enableStrategy(id);
        return result ? R.ok(true) : R.fail("启用价格策略失败");
    }

    @Operation(summary = "禁用价格策略")
    @PutMapping("/{id}/disable")
    public R<Boolean> disable(@PathVariable Long id) {
        boolean result = priceStrategyService.disableStrategy(id);
        return result ? R.ok(true) : R.fail("禁用价格策略失败");
    }

    @Operation(summary = "计算折扣后价格")
    @GetMapping("/calculate-price")
    public R<BigDecimal> calculatePrice(
            @RequestParam Long targetId,
            @RequestParam String targetType,
            @RequestParam BigDecimal originalPrice,
            @RequestParam(required = false) String studentType) {
        BigDecimal discountPrice = priceStrategyService.calculateDiscountPrice(targetId, targetType, originalPrice, studentType);
        return R.ok(discountPrice);
    }
}
