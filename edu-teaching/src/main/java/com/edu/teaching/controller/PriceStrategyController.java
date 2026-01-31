package com.edu.teaching.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.R;
import com.edu.teaching.domain.dto.PriceCalculateDTO;
import com.edu.teaching.domain.dto.PriceStrategyDTO;
import com.edu.teaching.domain.dto.PriceStrategyQueryDTO;
import com.edu.teaching.domain.vo.PriceCalculateVO;
import com.edu.teaching.domain.vo.PriceStrategyVO;
import com.edu.teaching.service.PriceStrategyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public R<Page<PriceStrategyVO>> page(PriceStrategyQueryDTO queryDTO) {
        Page<PriceStrategyVO> page = priceStrategyService.pageStrategies(queryDTO);
        return R.ok(page);
    }

    @Operation(summary = "获取启用的价格策略列表")
    @GetMapping("/list")
    public R<List<PriceStrategyVO>> list(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long campusId) {
        List<PriceStrategyVO> list = priceStrategyService.getActiveStrategies(courseId, campusId);
        return R.ok(list);
    }

    @Operation(summary = "获取适用的价格策略列表")
    @GetMapping("/applicable")
    public R<List<PriceStrategyVO>> getApplicable(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long campusId) {
        List<PriceStrategyVO> list = priceStrategyService.getApplicableStrategies(courseId, campusId);
        return R.ok(list);
    }

    @Operation(summary = "获取价格策略详情")
    @GetMapping("/{id}")
    public R<PriceStrategyVO> getById(@PathVariable Long id) {
        PriceStrategyVO strategy = priceStrategyService.getStrategyDetail(id);
        if (strategy == null) {
            return R.fail("价格策略不存在");
        }
        return R.ok(strategy);
    }

    @Operation(summary = "新增价格策略")
    @PostMapping
    public R<Boolean> add(@Valid @RequestBody PriceStrategyDTO dto) {
        // 验证策略配置
        if (!priceStrategyService.validateStrategy(dto)) {
            return R.fail("价格策略配置不合法");
        }
        try {
            boolean result = priceStrategyService.createStrategy(dto);
            return result ? R.ok(true) : R.fail("创建价格策略失败");
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
    }

    @Operation(summary = "修改价格策略")
    @PutMapping("/{id}")
    public R<Boolean> update(@PathVariable Long id, @Valid @RequestBody PriceStrategyDTO dto) {
        dto.setId(id);
        // 验证策略配置
        if (!priceStrategyService.validateStrategy(dto)) {
            return R.fail("价格策略配置不合法");
        }
        try {
            boolean result = priceStrategyService.updateStrategy(dto);
            return result ? R.ok(true) : R.fail("更新价格策略失败");
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
    }

    @Operation(summary = "删除价格策略")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        boolean result = priceStrategyService.deleteStrategy(id);
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

    @Operation(summary = "更新价格策略状态")
    @PutMapping("/{id}/status")
    public R<Boolean> updateStatus(@PathVariable Long id, @RequestParam String status) {
        if ("ACTIVE".equals(status)) {
            return enable(id);
        } else if ("INACTIVE".equals(status)) {
            return disable(id);
        } else {
            return R.fail("无效的状态值");
        }
    }

    @Operation(summary = "计算价格")
    @PostMapping("/calculate")
    public R<PriceCalculateVO> calculatePrice(@Valid @RequestBody PriceCalculateDTO request) {
        PriceCalculateVO result = priceStrategyService.calculatePrice(request);
        return R.ok(result);
    }
}
