package com.edu.finance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.R;
import com.edu.finance.domain.dto.ClassHourAdjustDTO;
import com.edu.finance.domain.dto.ClassHourBalanceQueryDTO;
import com.edu.finance.domain.dto.ClassHourBatchAdjustDTO;
import com.edu.finance.domain.entity.ClassHourRecord;
import com.edu.finance.domain.vo.ClassHourBalanceVO;
import com.edu.finance.domain.vo.ClassHourRecordVO;
import com.edu.finance.domain.vo.ClassHourStatisticsVO;
import com.edu.finance.service.ClassHourAccountService;
import com.edu.finance.service.ClassHourRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 课时管理控制器
 */
@Tag(name = "课时管理")
@RestController
@RequestMapping("/finance/class-hour")
@RequiredArgsConstructor
public class ClassHourController {

    private final ClassHourAccountService classHourAccountService;
    private final ClassHourRecordService classHourRecordService;

    @Operation(summary = "课时调整（赠送/扣减/撤销）")
    @PostMapping("/adjust")
    public R<Boolean> adjustClassHour(@Valid @RequestBody ClassHourAdjustDTO dto) {
        return R.ok(classHourAccountService.adjustClassHour(dto));
    }

    @Operation(summary = "批量课时调整")
    @PostMapping("/adjust/batch")
    public R<Map<Long, Boolean>> batchAdjustClassHour(@Valid @RequestBody ClassHourBatchAdjustDTO dto) {
        Map<Long, Boolean> result = classHourAccountService.batchAdjustClassHour(dto);
        return R.ok(result);
    }

    @Operation(summary = "查询课时余额")
    @GetMapping("/balance")
    public R<List<ClassHourBalanceVO>> queryBalance(ClassHourBalanceQueryDTO query) {
        return R.ok(classHourAccountService.queryBalance(query));
    }

    @Operation(summary = "查询学员课时余额")
    @GetMapping("/balance/student/{studentId}")
    public R<List<ClassHourBalanceVO>> getStudentBalance(
            @Parameter(description = "学员ID") @PathVariable Long studentId) {
        ClassHourBalanceQueryDTO query = new ClassHourBalanceQueryDTO();
        query.setStudentId(studentId);
        return R.ok(classHourAccountService.queryBalance(query));
    }

    @Operation(summary = "查询课程课时余额")
    @GetMapping("/balance/course/{courseId}")
    public R<List<ClassHourBalanceVO>> getCourseBalance(
            @Parameter(description = "课程ID") @PathVariable Long courseId) {
        ClassHourBalanceQueryDTO query = new ClassHourBalanceQueryDTO();
        query.setCourseId(courseId);
        return R.ok(classHourAccountService.queryBalance(query));
    }

    @Operation(summary = "查询预警账户列表")
    @GetMapping("/warning")
    public R<List<ClassHourBalanceVO>> getWarningAccounts(
            @Parameter(description = "预警类型：low_balance-余额不足") @RequestParam(defaultValue = "low_balance") String warningType,
            @Parameter(description = "阈值") @RequestParam(defaultValue = "5") BigDecimal threshold) {
        return R.ok(classHourAccountService.getWarningAccounts(warningType, threshold));
    }

    @Operation(summary = "查询课时消耗记录")
    @GetMapping("/record")
    public R<Page<ClassHourRecord>> getRecords(
            @Parameter(description = "学员ID") @RequestParam(required = false) Long studentId,
            @Parameter(description = "账户ID") @RequestParam(required = false) Long accountId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<ClassHourRecord> page = new Page<>(pageNum, pageSize);
        // TODO: 实现分页查询
        return R.ok(page);
    }

    @Operation(summary = "统计学员课时使用情况")
    @GetMapping("/statistics/student/{studentId}")
    public R<ClassHourStatisticsVO> statisticsByStudent(
            @Parameter(description = "学员ID") @PathVariable Long studentId) {
        return R.ok(classHourAccountService.statisticsByStudent(studentId));
    }

    @Operation(summary = "统计课程消课情况")
    @GetMapping("/statistics/course/{courseId}")
    public R<ClassHourStatisticsVO> statisticsByCourse(
            @Parameter(description = "课程ID") @PathVariable Long courseId) {
        return R.ok(classHourAccountService.statisticsByCourse(courseId));
    }

    @Operation(summary = "统计校区课时数据")
    @GetMapping("/statistics/campus/{campusId}")
    public R<ClassHourStatisticsVO> statisticsByCampus(
            @Parameter(description = "校区ID") @PathVariable Long campusId) {
        return R.ok(classHourAccountService.statisticsByCampus(campusId));
    }

    @Operation(summary = "冻结课时账户")
    @PutMapping("/account/{id}/freeze")
    public R<Boolean> freezeAccount(
            @Parameter(description = "账户ID") @PathVariable Long id) {
        return R.ok(classHourAccountService.freezeAccount(id));
    }

    @Operation(summary = "解冻课时账户")
    @PutMapping("/account/{id}/unfreeze")
    public R<Boolean> unfreezeAccount(
            @Parameter(description = "账户ID") @PathVariable Long id) {
        return R.ok(classHourAccountService.unfreezeAccount(id));
    }
}
