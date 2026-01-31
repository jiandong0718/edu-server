package com.edu.notification.controller;

import com.edu.common.core.Result;
import com.edu.notification.domain.dto.SmsBatchSendDTO;
import com.edu.notification.domain.dto.SmsSendDTO;
import com.edu.notification.domain.dto.SmsTemplateSendDTO;
import com.edu.notification.domain.vo.SmsBatchSendResultVO;
import com.edu.notification.domain.vo.SmsSendResultVO;
import com.edu.notification.service.SmsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 短信服务Controller
 */
@Slf4j
@RestController
@RequestMapping("/notification/sms")
@Tag(name = "短信服务", description = "短信发送、查询等功能")
public class SmsController {

    private final SmsService smsService;

    public SmsController(SmsService smsService) {
        this.smsService = smsService;
    }

    /**
     * 发送单条短信
     */
    @PostMapping("/send")
    @Operation(summary = "发送单条短信", description = "发送单条短信到指定手机号")
    public Result<SmsSendResultVO> sendSms(@Validated @RequestBody SmsSendDTO dto) {
        log.info("发送单条短信 - 手机号: {}", dto.getPhone());
        SmsSendResultVO result = smsService.sendSms(dto);
        return Result.success(result);
    }

    /**
     * 批量发送短信
     */
    @PostMapping("/send-batch")
    @Operation(summary = "批量发送短信", description = "批量发送短信到多个手机号")
    public Result<SmsBatchSendResultVO> sendBatchSms(@Validated @RequestBody SmsBatchSendDTO dto) {
        log.info("批量发送短信 - 手机号数量: {}", dto.getPhones().size());
        SmsBatchSendResultVO result = smsService.sendBatchSms(dto);
        return Result.success(result);
    }

    /**
     * 发送模板短信
     */
    @PostMapping("/send-template")
    @Operation(summary = "发送模板短信", description = "使用模板发送短信，支持单个或批量")
    public Result<?> sendTemplateSms(@Validated @RequestBody SmsTemplateSendDTO dto) {
        log.info("发送模板短信 - 模板: {}", dto.getTemplateCode());

        // 判断是单个还是批量
        if (dto.getPhone() != null && !dto.getPhone().isEmpty()) {
            // 单个发送
            SmsSendResultVO result = smsService.sendTemplateSms(dto);
            return Result.success(result);
        } else if (dto.getPhones() != null && !dto.getPhones().isEmpty()) {
            // 批量发送
            SmsBatchSendResultVO result = smsService.sendBatchTemplateSms(dto);
            return Result.success(result);
        } else {
            return Result.error("手机号不能为空");
        }
    }

    /**
     * 查询发送状态
     */
    @GetMapping("/status/{id}")
    @Operation(summary = "查询发送状态", description = "根据发送记录ID查询短信发送状态")
    @Parameter(name = "id", description = "发送记录ID", required = true)
    public Result<Map<String, Object>> querySendStatus(@PathVariable Long id) {
        log.info("查询发送状态 - 记录ID: {}", id);
        Map<String, Object> result = smsService.querySendStatus(id);
        return Result.success(result);
    }

    /**
     * 重试发送
     */
    @PostMapping("/retry/{id}")
    @Operation(summary = "重试发送", description = "重试发送失败的短信")
    @Parameter(name = "id", description = "发送记录ID", required = true)
    public Result<SmsSendResultVO> retrySend(@PathVariable Long id) {
        log.info("重试发送 - 记录ID: {}", id);
        SmsSendResultVO result = smsService.retrySend(id);
        return Result.success(result);
    }
}
