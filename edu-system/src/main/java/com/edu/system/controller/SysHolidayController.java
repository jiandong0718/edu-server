package com.edu.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.R;
import com.edu.system.domain.dto.SysHolidayDTO;
import com.edu.system.domain.dto.SysHolidayQueryDTO;
import com.edu.system.domain.entity.SysHoliday;
import com.edu.system.domain.vo.SysHolidayVO;
import com.edu.system.service.SysHolidayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 节假日管理控制器
 */
@Tag(name = "节假日管理")
@RestController
@RequestMapping("/system/holiday")
@RequiredArgsConstructor
public class SysHolidayController {

    private final SysHolidayService holidayService;

    @Operation(summary = "分页查询节假日列表")
    @GetMapping("/page")
    public R<Page<SysHolidayVO>> page(@Validated SysHolidayQueryDTO queryDTO) {
        return R.ok(holidayService.pageHolidays(queryDTO));
    }

    @Operation(summary = "获取节假日列表")
    @GetMapping("/list")
    public R<List<SysHoliday>> list(
            @Parameter(description = "节假日类型") @RequestParam(required = false) Integer type,
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        if (startDate != null && endDate != null) {
            return R.ok(holidayService.getHolidaysByDateRange(startDate, endDate, campusId));
        }

        // 如果没有指定日期范围，返回所有启用的节假日
        return R.ok(holidayService.lambdaQuery()
                .eq(SysHoliday::getStatus, 1)
                .eq(type != null, SysHoliday::getType, type)
                .and(campusId != null, w -> w.isNull(SysHoliday::getCampusId).or().eq(SysHoliday::getCampusId, campusId))
                .orderByAsc(SysHoliday::getStartDate)
                .list());
    }

    @Operation(summary = "获取节假日详情")
    @GetMapping("/{id}")
    public R<SysHolidayVO> getById(@Parameter(description = "节假日ID") @PathVariable Long id) {
        return R.ok(holidayService.getHolidayById(id));
    }

    @Operation(summary = "新增节假日")
    @PostMapping
    public R<Boolean> add(@Validated @RequestBody SysHolidayDTO holidayDTO) {
        return R.ok(holidayService.addHoliday(holidayDTO));
    }

    @Operation(summary = "修改节假日")
    @PutMapping
    public R<Boolean> update(@Validated @RequestBody SysHolidayDTO holidayDTO) {
        return R.ok(holidayService.updateHoliday(holidayDTO));
    }

    @Operation(summary = "删除节假日")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@Parameter(description = "节假日ID") @PathVariable Long id) {
        return R.ok(holidayService.deleteHoliday(id));
    }

    @Operation(summary = "批量删除节假日")
    @DeleteMapping("/batch")
    public R<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        return R.ok(holidayService.batchDeleteHolidays(ids));
    }

    @Operation(summary = "修改状态")
    @PutMapping("/{id}/status")
    public R<Boolean> updateStatus(
            @Parameter(description = "节假日ID") @PathVariable Long id,
            @Parameter(description = "状态：0-禁用，1-启用") @RequestParam Integer status) {
        return R.ok(holidayService.updateStatus(id, status));
    }

    @Operation(summary = "判断指定日期是否为节假日")
    @GetMapping("/check/holiday")
    public R<Boolean> checkHoliday(
            @Parameter(description = "日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId) {
        return R.ok(holidayService.isHoliday(date, campusId));
    }

    @Operation(summary = "判断指定日期是否为工作日（调休上班）")
    @GetMapping("/check/workday")
    public R<Boolean> checkWorkday(
            @Parameter(description = "日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId) {
        return R.ok(holidayService.isWorkday(date, campusId));
    }

    @Operation(summary = "获取指定日期范围内的节假日")
    @GetMapping("/range")
    public R<List<SysHoliday>> getByDateRange(
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId) {
        return R.ok(holidayService.getHolidaysByDateRange(startDate, endDate, campusId));
    }
}
