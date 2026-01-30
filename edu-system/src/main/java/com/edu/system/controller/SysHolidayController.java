package com.edu.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.R;
import com.edu.common.exception.BusinessException;
import com.edu.system.domain.entity.SysHoliday;
import com.edu.system.service.SysHolidayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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
    public R<Page<SysHoliday>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        Page<SysHoliday> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysHoliday> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null, SysHoliday::getName, name)
                .eq(type != null, SysHoliday::getType, type)
                .eq(status != null, SysHoliday::getStatus, status)
                .ge(startDate != null, SysHoliday::getStartDate, startDate)
                .le(endDate != null, SysHoliday::getEndDate, endDate)
                .orderByDesc(SysHoliday::getStartDate);
        holidayService.page(page, wrapper);
        return R.ok(page);
    }

    @Operation(summary = "获取节假日列表")
    @GetMapping("/list")
    public R<List<SysHoliday>> list(
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        LambdaQueryWrapper<SysHoliday> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysHoliday::getStatus, 1)
                .eq(type != null, SysHoliday::getType, type)
                .ge(startDate != null, SysHoliday::getStartDate, startDate)
                .le(endDate != null, SysHoliday::getEndDate, endDate)
                .orderByAsc(SysHoliday::getStartDate);
        return R.ok(holidayService.list(wrapper));
    }

    @Operation(summary = "获取节假日详情")
    @GetMapping("/{id}")
    public R<SysHoliday> getById(@PathVariable Long id) {
        return R.ok(holidayService.getById(id));
    }

    @Operation(summary = "新增节假日")
    @PostMapping
    public R<Boolean> add(@RequestBody SysHoliday holiday) {
        // 验证日期范围
        if (holiday.getStartDate().isAfter(holiday.getEndDate())) {
            throw new BusinessException("开始日期不能晚于结束日期");
        }

        // 检查日期冲突
        if (!holidayService.checkDateConflict(holiday.getStartDate(), holiday.getEndDate(), null)) {
            throw new BusinessException("该日期范围与已有节假日冲突");
        }

        return R.ok(holidayService.save(holiday));
    }

    @Operation(summary = "修改节假日")
    @PutMapping
    public R<Boolean> update(@RequestBody SysHoliday holiday) {
        // 验证日期范围
        if (holiday.getStartDate().isAfter(holiday.getEndDate())) {
            throw new BusinessException("开始日期不能晚于结束日期");
        }

        // 检查日期冲突
        if (!holidayService.checkDateConflict(holiday.getStartDate(), holiday.getEndDate(), holiday.getId())) {
            throw new BusinessException("该日期范围与已有节假日冲突");
        }

        return R.ok(holidayService.updateById(holiday));
    }

    @Operation(summary = "删除节假日")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(holidayService.removeById(id));
    }

    @Operation(summary = "批量删除节假日")
    @DeleteMapping("/batch")
    public R<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        return R.ok(holidayService.removeByIds(ids));
    }

    @Operation(summary = "修改状态")
    @PutMapping("/{id}/status")
    public R<Boolean> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        SysHoliday holiday = new SysHoliday();
        holiday.setId(id);
        holiday.setStatus(status);
        return R.ok(holidayService.updateById(holiday));
    }

    @Operation(summary = "判断指定日期是否为节假日")
    @GetMapping("/check/holiday")
    public R<Boolean> checkHoliday(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return R.ok(holidayService.isHoliday(date));
    }

    @Operation(summary = "判断指定日期是否为工作日")
    @GetMapping("/check/workday")
    public R<Boolean> checkWorkday(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return R.ok(holidayService.isWorkday(date));
    }

    @Operation(summary = "获取指定日期范围内的节假日")
    @GetMapping("/range")
    public R<List<SysHoliday>> getByDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return R.ok(holidayService.getHolidaysByDateRange(startDate, endDate));
    }
}
