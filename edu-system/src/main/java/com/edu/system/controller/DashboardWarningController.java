package com.edu.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.Result;
import com.edu.system.domain.dto.WarningConfigDTO;
import com.edu.system.domain.entity.WarningConfig;
import com.edu.system.domain.vo.WarningVO;
import com.edu.system.service.DashboardWarningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据预警控制器
 */
@Tag(name = "数据预警")
@RestController
@RequestMapping("/system/dashboard/warning")
@RequiredArgsConstructor
public class DashboardWarningController {

    private final DashboardWarningService dashboardWarningService;

    /**
     * 获取预警列表
     */
    @Operation(summary = "获取预警列表", description = "分页查询数据预警信息，支持按校区、预警类型、预警级别筛选")
    @GetMapping("/list")
    public Result<Page<WarningVO>> getWarningList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "校区ID，不传则查询所有校区") @RequestParam(required = false) Long campusId,
            @Parameter(description = "预警类型") @RequestParam(required = false) String warningType,
            @Parameter(description = "预警级别：normal-正常，warning-警告，urgent-紧急") @RequestParam(required = false) String warningLevel) {
        Page<WarningVO> page = dashboardWarningService.getWarningList(pageNum, pageSize, campusId, warningType, warningLevel);
        return Result.success(page);
    }

    /**
     * 获取预警汇总
     */
    @Operation(summary = "获取预警汇总", description = "获取预警统计汇总信息，包括各级别预警数量、各类别预警数量、预警类型分布等")
    @GetMapping("/summary")
    public Result<WarningVO.WarningSummary> getWarningSummary(
            @Parameter(description = "校区ID，不传则查询所有校区") @RequestParam(required = false) Long campusId) {
        WarningVO.WarningSummary summary = dashboardWarningService.getWarningSummary(campusId);
        return Result.success(summary);
    }

    /**
     * 配置预警规则
     */
    @Operation(summary = "配置预警规则", description = "创建新的预警规则配置")
    @PostMapping("/config")
    public Result<Long> configWarning(@Valid @RequestBody WarningConfigDTO dto) {
        Long configId = dashboardWarningService.configWarning(dto);
        return Result.success(configId);
    }

    /**
     * 更新预警规则
     */
    @Operation(summary = "更新预警规则", description = "更新已有的预警规则配置")
    @PutMapping("/config/{id}")
    public Result<Boolean> updateWarningConfig(
            @Parameter(description = "配置ID") @PathVariable Long id,
            @Valid @RequestBody WarningConfigDTO dto) {
        Boolean result = dashboardWarningService.updateWarningConfig(id, dto);
        return Result.success(result);
    }

    /**
     * 获取预警配置列表
     */
    @Operation(summary = "获取预警配置列表", description = "查询预警规则配置列表")
    @GetMapping("/config/list")
    public Result<List<WarningConfig>> getWarningConfigs(
            @Parameter(description = "校区ID，不传则查询全局配置") @RequestParam(required = false) Long campusId) {
        List<WarningConfig> configs = dashboardWarningService.getWarningConfigs(campusId);
        return Result.success(configs);
    }

    /**
     * 获取预警配置详情
     */
    @Operation(summary = "获取预警配置详情", description = "根据ID查询预警配置详情")
    @GetMapping("/config/{id}")
    public Result<WarningConfig> getWarningConfig(
            @Parameter(description = "配置ID") @PathVariable Long id) {
        WarningConfig config = dashboardWarningService.getById(id);
        return Result.success(config);
    }

    /**
     * 删除预警配置
     */
    @Operation(summary = "删除预警配置", description = "删除指定的预警规则配置")
    @DeleteMapping("/config/{id}")
    public Result<Boolean> deleteWarningConfig(
            @Parameter(description = "配置ID") @PathVariable Long id) {
        Boolean result = dashboardWarningService.deleteWarningConfig(id);
        return Result.success(result);
    }
}
