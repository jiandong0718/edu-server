package com.edu.marketing.controller;

import com.edu.common.core.R;
import com.edu.marketing.domain.dto.AdvisorPerformanceQueryDTO;
import com.edu.marketing.domain.dto.ConversionFunnelQueryDTO;
import com.edu.marketing.domain.vo.AdvisorPerformanceVO;
import com.edu.marketing.domain.vo.ConversionFunnelVO;
import com.edu.marketing.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 统计分析控制器
 *
 * @author edu
 * @since 2024-01-31
 */
@Tag(name = "统计分析")
@RestController
@RequestMapping("/marketing/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @Operation(summary = "获取招生转化漏斗统计", description = "统计线索从新建到成交的各阶段数量和转化率")
    @GetMapping("/conversion-funnel")
    public R<ConversionFunnelVO> getConversionFunnel(
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId,
            @Parameter(description = "顾问ID") @RequestParam(required = false) Long advisorId,
            @Parameter(description = "开始时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {

        ConversionFunnelQueryDTO query = new ConversionFunnelQueryDTO();
        query.setCampusId(campusId);
        query.setAdvisorId(advisorId);
        query.setStartTime(startTime);
        query.setEndTime(endTime);

        ConversionFunnelVO result = statisticsService.getConversionFunnel(query);
        return R.ok(result);
    }

    @Operation(summary = "获取顾问业绩统计列表", description = "统计各顾问的线索数、试听数、成交数等业绩指标")
    @GetMapping("/advisor-performance")
    public R<List<AdvisorPerformanceVO>> getAdvisorPerformanceList(
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId,
            @Parameter(description = "顾问ID") @RequestParam(required = false) Long advisorId,
            @Parameter(description = "开始时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @Parameter(description = "排序字段") @RequestParam(required = false) String orderBy,
            @Parameter(description = "排序方向") @RequestParam(required = false) String orderDirection,
            @Parameter(description = "返回记录数") @RequestParam(required = false) Integer limit) {

        AdvisorPerformanceQueryDTO query = new AdvisorPerformanceQueryDTO();
        query.setCampusId(campusId);
        query.setAdvisorId(advisorId);
        query.setStartTime(startTime);
        query.setEndTime(endTime);
        query.setOrderBy(orderBy);
        query.setOrderDirection(orderDirection);
        query.setLimit(limit);

        List<AdvisorPerformanceVO> result = statisticsService.getAdvisorPerformanceList(query);
        return R.ok(result);
    }

    @Operation(summary = "获取顾问业绩排行榜", description = "按成交数量降序返回顾问业绩排行榜，默认返回前10名")
    @GetMapping("/advisor-ranking")
    public R<List<AdvisorPerformanceVO>> getAdvisorPerformanceRanking(
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId,
            @Parameter(description = "开始时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @Parameter(description = "排序字段：totalLeadCount-线索数，trialCount-试听数，conversionCount-成交数，conversionRate-转化率，conversionAmount-成交金额")
            @RequestParam(required = false, defaultValue = "conversionCount") String orderBy,
            @Parameter(description = "返回记录数，默认10") @RequestParam(required = false, defaultValue = "10") Integer limit) {

        AdvisorPerformanceQueryDTO query = new AdvisorPerformanceQueryDTO();
        query.setCampusId(campusId);
        query.setStartTime(startTime);
        query.setEndTime(endTime);
        query.setOrderBy(orderBy);
        query.setOrderDirection("desc");
        query.setLimit(limit);

        List<AdvisorPerformanceVO> result = statisticsService.getAdvisorPerformanceRanking(query);
        return R.ok(result);
    }
}
