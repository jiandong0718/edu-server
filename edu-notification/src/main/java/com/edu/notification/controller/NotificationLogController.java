package com.edu.notification.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.Result;
import com.edu.notification.domain.dto.BatchResendDTO;
import com.edu.notification.domain.dto.NotificationLogQueryDTO;
import com.edu.notification.domain.vo.BatchResendResultVO;
import com.edu.notification.domain.vo.NotificationLogVO;
import com.edu.notification.domain.vo.NotificationStatisticsVO;
import com.edu.notification.service.NotificationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 通知发送记录控制器
 */
@Tag(name = "通知发送记录管理", description = "通知发送记录的查询、统计和重发功能")
@RestController
@RequestMapping("/notification/log")
@RequiredArgsConstructor
public class NotificationLogController {

    private final NotificationLogService notificationLogService;

    /**
     * 分页查询发送记录
     */
    @Operation(summary = "分页查询发送记录", description = "根据条件分页查询通知发送记录")
    @GetMapping("/page")
    public Result<IPage<NotificationLogVO>> page(
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量", example = "10")
            @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "通知类型：sms-短信，site-站内信，email-邮件，wechat-微信，push-推送")
            @RequestParam(required = false) String type,
            @Parameter(description = "发送状态：pending-待发送，sending-发送中，success-成功，failed-失败")
            @RequestParam(required = false) String status,
            @Parameter(description = "接收人（手机号/邮箱/用户ID/姓名）")
            @RequestParam(required = false) String receiver,
            @Parameter(description = "开始日期", example = "2024-01-01 00:00:00")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @Parameter(description = "结束日期", example = "2024-12-31 23:59:59")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate,
            @Parameter(description = "校区ID")
            @RequestParam(required = false) Long campusId,
            @Parameter(description = "业务类型")
            @RequestParam(required = false) String bizType,
            @Parameter(description = "模板编码")
            @RequestParam(required = false) String templateCode) {

        NotificationLogQueryDTO query = new NotificationLogQueryDTO();
        query.setType(type);
        query.setStatus(status);
        query.setReceiver(receiver);
        query.setStartDate(startDate);
        query.setEndDate(endDate);
        query.setCampusId(campusId);
        query.setBizType(bizType);
        query.setTemplateCode(templateCode);

        Page<NotificationLogVO> pageParam = new Page<>(page, pageSize);
        IPage<NotificationLogVO> result = notificationLogService.getLogPage(pageParam, query);

        return Result.success(result);
    }

    /**
     * 查询发送记录详情
     */
    @Operation(summary = "查询发送记录详情", description = "根据ID查询通知发送记录的完整信息")
    @GetMapping("/{id}")
    public Result<NotificationLogVO> getById(
            @Parameter(description = "记录ID", required = true)
            @PathVariable Long id) {
        NotificationLogVO logVO = notificationLogService.getLogById(id);
        return Result.success(logVO);
    }

    /**
     * 发送统计
     */
    @Operation(summary = "发送统计", description = "统计通知发送情况，包括总数、成功数、失败数、按类型统计、按日期统计")
    @GetMapping("/statistics")
    public Result<NotificationStatisticsVO> statistics(
            @Parameter(description = "开始日期", example = "2024-01-01 00:00:00")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @Parameter(description = "结束日期", example = "2024-12-31 23:59:59")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate,
            @Parameter(description = "校区ID")
            @RequestParam(required = false) Long campusId) {

        // 如果没有指定日期范围，默认查询最近30天
        if (startDate == null && endDate == null) {
            endDate = LocalDateTime.now();
            startDate = endDate.minusDays(30);
        }

        NotificationStatisticsVO statistics = notificationLogService.getStatistics(startDate, endDate, campusId);
        return Result.success(statistics);
    }

    /**
     * 重发失败通知
     */
    @Operation(summary = "重发失败通知", description = "重新发送失败的通知，只能重发状态为失败的通知")
    @PostMapping("/{id}/resend")
    public Result<Void> resend(
            @Parameter(description = "记录ID", required = true)
            @PathVariable Long id) {
        notificationLogService.resend(id);
        return Result.success();
    }

    /**
     * 批量重发
     */
    @Operation(summary = "批量重发", description = "批量重新发送失败的通知，返回批量操作结果")
    @PostMapping("/batch-resend")
    public Result<BatchResendResultVO> batchResend(
            @RequestBody BatchResendDTO dto) {
        BatchResendResultVO result = notificationLogService.batchResend(dto.getIds());
        return Result.success(result);
    }
}
