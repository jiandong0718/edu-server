package com.edu.finance.controller;

import com.edu.common.core.Result;
import com.edu.finance.domain.dto.ClassHourWarningQueryDTO;
import com.edu.finance.domain.vo.ClassHourWarningVO;
import com.edu.finance.service.ClassHourWarningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课时预警控制器
 */
@Tag(name = "课时预警管理")
@RestController
@RequestMapping("/finance/class-hour/warning")
@RequiredArgsConstructor
public class ClassHourWarningController {

    private final ClassHourWarningService classHourWarningService;

    @Operation(summary = "获取预警列表", description = "查询课时预警列表，包括余额不足、即将过期、已过期等预警")
    @GetMapping("/list")
    public Result<List<ClassHourWarningVO>> getWarningList(
            @Parameter(description = "查询条件")
            ClassHourWarningQueryDTO query) {
        List<ClassHourWarningVO> warnings = classHourWarningService.getWarningList(query);
        return Result.success(warnings);
    }

    @Operation(summary = "手动触发预警检查", description = "手动触发课时预警检查并发送通知（通常由定时任务自动执行）")
    @PostMapping("/check")
    public Result<Integer> checkWarnings() {
        int count = classHourWarningService.checkAndSendWarnings();
        return Result.success("预警检查完成，共发现" + count + "个预警", count);
    }
}
