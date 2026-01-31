package com.edu.finance.controller;

import com.edu.common.core.Result;
import com.edu.finance.domain.vo.ClassHourConsumptionVO;
import com.edu.finance.domain.vo.ClassHourStatisticsVO;
import com.edu.finance.domain.vo.ClassHourSummaryVO;
import com.edu.finance.service.ClassHourStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 课时统计控制器
 */
@Tag(name = "课时统计管理")
@RestController
@RequestMapping("/finance/class-hour/statistics")
@RequiredArgsConstructor
public class ClassHourStatisticsController {

    private final ClassHourStatisticsService classHourStatisticsService;

    @Operation(summary = "课时汇总统计", description = "统计总课时、已用课时、剩余课时、消课率、预警数量等")
    @GetMapping("/summary")
    public Result<ClassHourSummaryVO> getSummary(
            @Parameter(description = "校区ID（可选，不传则统计全部）")
            @RequestParam(required = false) Long campusId) {
        ClassHourSummaryVO summary = classHourStatisticsService.getSummary(campusId);
        return Result.success(summary);
    }

    @Operation(summary = "按课程统计", description = "按课程维度统计课时使用情况")
    @GetMapping("/by-course")
    public Result<List<ClassHourStatisticsVO>> statisticsByCourse(
            @Parameter(description = "校区ID（可选，不传则统计全部）")
            @RequestParam(required = false) Long campusId) {
        List<ClassHourStatisticsVO> statistics = classHourStatisticsService.statisticsByCourse(campusId);
        return Result.success(statistics);
    }

    @Operation(summary = "按学员统计", description = "按学员维度统计课时使用情况")
    @GetMapping("/by-student")
    public Result<List<ClassHourStatisticsVO>> statisticsByStudent(
            @Parameter(description = "校区ID（可选，不传则统计全部）")
            @RequestParam(required = false) Long campusId) {
        List<ClassHourStatisticsVO> statistics = classHourStatisticsService.statisticsByStudent(campusId);
        return Result.success(statistics);
    }

    @Operation(summary = "消课统计", description = "统计指定时间段内的消课情况")
    @GetMapping("/consumption")
    public Result<List<ClassHourConsumptionVO>> statisticsConsumption(
            @Parameter(description = "开始日期", required = true)
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期", required = true)
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "校区ID（可选，不传则统计全部）")
            @RequestParam(required = false) Long campusId) {
        List<ClassHourConsumptionVO> statistics = classHourStatisticsService.statisticsConsumption(startDate, endDate, campusId);
        return Result.success(statistics);
    }
}
