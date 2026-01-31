package com.edu.notification.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.exception.BusinessException;
import com.edu.notification.domain.dto.NotificationLogQueryDTO;
import com.edu.notification.domain.entity.NotificationLog;
import com.edu.notification.domain.vo.BatchResendResultVO;
import com.edu.notification.domain.vo.NotificationLogVO;
import com.edu.notification.domain.vo.NotificationStatisticsVO;
import com.edu.notification.mapper.NotificationLogMapper;
import com.edu.notification.service.NotificationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 通知发送记录服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationLogServiceImpl extends ServiceImpl<NotificationLogMapper, NotificationLog>
        implements NotificationLogService {

    private final NotificationLogMapper notificationLogMapper;

    @Override
    public IPage<NotificationLogVO> getLogPage(Page<NotificationLogVO> page, NotificationLogQueryDTO query) {
        return notificationLogMapper.selectLogPage(page, query);
    }

    @Override
    public NotificationLogVO getLogById(Long id) {
        NotificationLogVO logVO = notificationLogMapper.selectLogById(id);
        if (logVO == null) {
            throw new BusinessException("通知发送记录不存在");
        }
        return logVO;
    }

    @Override
    public NotificationStatisticsVO getStatistics(LocalDateTime startDate, LocalDateTime endDate, Long campusId) {
        // 查询总体统计
        Map<String, Object> totalStats = notificationLogMapper.selectTotalStatistics(startDate, endDate, campusId);

        Long totalCount = getLongValue(totalStats, "total_count");
        Long successCount = getLongValue(totalStats, "success_count");
        Long failedCount = getLongValue(totalStats, "failed_count");
        Long pendingCount = getLongValue(totalStats, "pending_count");
        Long sendingCount = getLongValue(totalStats, "sending_count");

        // 计算成功率
        Double successRate = 0.0;
        if (totalCount > 0) {
            successRate = BigDecimal.valueOf(successCount)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalCount), 2, RoundingMode.HALF_UP)
                    .doubleValue();
        }

        // 查询按类型统计
        List<Map<String, Object>> typeStatsList = notificationLogMapper.selectTypeStatistics(startDate, endDate, campusId);
        Map<String, NotificationStatisticsVO.TypeStatistics> typeStatistics = new HashMap<>();

        for (Map<String, Object> typeStats : typeStatsList) {
            String type = (String) typeStats.get("type");
            Long count = getLongValue(typeStats, "count");
            Long typeSuccessCount = getLongValue(typeStats, "success_count");
            Long typeFailedCount = getLongValue(typeStats, "failed_count");

            Double typeSuccessRate = 0.0;
            if (count > 0) {
                typeSuccessRate = BigDecimal.valueOf(typeSuccessCount)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP)
                        .doubleValue();
            }

            typeStatistics.put(type, NotificationStatisticsVO.TypeStatistics.builder()
                    .type(type)
                    .count(count)
                    .successCount(typeSuccessCount)
                    .failedCount(typeFailedCount)
                    .successRate(typeSuccessRate)
                    .build());
        }

        // 查询按日期统计
        List<Map<String, Object>> dateStatsList = notificationLogMapper.selectDateStatistics(startDate, endDate, campusId);
        List<NotificationStatisticsVO.DateStatistics> dateStatistics = dateStatsList.stream()
                .map(dateStats -> NotificationStatisticsVO.DateStatistics.builder()
                        .date((String) dateStats.get("date"))
                        .count(getLongValue(dateStats, "count"))
                        .successCount(getLongValue(dateStats, "success_count"))
                        .failedCount(getLongValue(dateStats, "failed_count"))
                        .build())
                .collect(Collectors.toList());

        return NotificationStatisticsVO.builder()
                .totalCount(totalCount)
                .successCount(successCount)
                .failedCount(failedCount)
                .pendingCount(pendingCount)
                .sendingCount(sendingCount)
                .successRate(successRate)
                .typeStatistics(typeStatistics)
                .dateStatistics(dateStatistics)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resend(Long id) {
        NotificationLog log = getById(id);
        if (log == null) {
            throw new BusinessException("通知发送记录不存在");
        }

        // 检查是否可以重发（只有失败状态才能重发）
        if (!"failed".equals(log.getStatus())) {
            throw new BusinessException("只有发送失败的通知才能重发");
        }

        // 检查重试次数限制（最多重试3次）
        if (log.getRetryCount() != null && log.getRetryCount() >= 3) {
            throw new BusinessException("已达到最大重试次数限制");
        }

        // 异步重发
        asyncResend(log);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BatchResendResultVO batchResend(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要重发的记录");
        }

        List<Long> successIds = new ArrayList<>();
        List<Long> failedIds = new ArrayList<>();
        List<BatchResendResultVO.FailedItem> failedItems = new ArrayList<>();

        for (Long id : ids) {
            try {
                NotificationLog log = getById(id);
                if (log == null) {
                    failedIds.add(id);
                    failedItems.add(BatchResendResultVO.FailedItem.builder()
                            .id(id)
                            .reason("记录不存在")
                            .build());
                    continue;
                }

                if (!"failed".equals(log.getStatus())) {
                    failedIds.add(id);
                    failedItems.add(BatchResendResultVO.FailedItem.builder()
                            .id(id)
                            .reason("只有发送失败的通知才能重发")
                            .build());
                    continue;
                }

                if (log.getRetryCount() != null && log.getRetryCount() >= 3) {
                    failedIds.add(id);
                    failedItems.add(BatchResendResultVO.FailedItem.builder()
                            .id(id)
                            .reason("已达到最大重试次数限制")
                            .build());
                    continue;
                }

                // 异步重发
                asyncResend(log);
                successIds.add(id);

            } catch (Exception e) {
                log.error("重发通知失败，ID: {}", id, e);
                failedIds.add(id);
                failedItems.add(BatchResendResultVO.FailedItem.builder()
                        .id(id)
                        .reason(e.getMessage())
                        .build());
            }
        }

        return BatchResendResultVO.builder()
                .total(ids.size())
                .successCount(successIds.size())
                .failedCount(failedIds.size())
                .successIds(successIds)
                .failedIds(failedIds)
                .failedItems(failedItems)
                .build();
    }

    @Override
    public void saveLog(NotificationLog log) {
        save(log);
    }

    /**
     * 异步重发通知
     *
     * @param log 发送记录
     */
    @Async
    protected void asyncResend(NotificationLog log) {
        try {
            log.setStatus("sending");
            log.setRetryCount(log.getRetryCount() == null ? 1 : log.getRetryCount() + 1);
            log.setUpdateTime(LocalDateTime.now());
            updateById(log);

            // TODO: 实际的重发逻辑，根据不同类型调用不同的发送服务
            // 这里模拟发送过程
            boolean sendSuccess = simulateSend(log);

            if (sendSuccess) {
                log.setStatus("success");
                log.setSendTime(LocalDateTime.now());
                log.setFailReason(null);
            } else {
                log.setStatus("failed");
                log.setFailReason("重发失败");
            }

            updateById(log);
            log.info("通知重发完成，ID: {}, 状态: {}", log.getId(), log.getStatus());

        } catch (Exception e) {
            log.error("通知重发异常，ID: {}", log.getId(), e);
            log.setStatus("failed");
            log.setFailReason("重发异常: " + e.getMessage());
            updateById(log);
        }
    }

    /**
     * 模拟发送（实际项目中应该调用真实的发送服务）
     *
     * @param log 发送记录
     * @return 是否成功
     */
    private boolean simulateSend(NotificationLog log) {
        // TODO: 根据类型调用不同的发送服务
        // 短信: smsService.send()
        // 站内信: userMessageService.send()
        // 邮件: emailService.send()
        // 微信: wechatService.send()
        // 推送: pushService.send()

        // 这里简单模拟，实际应该调用真实服务
        log.info("模拟发送通知，类型: {}, 接收人: {}", log.getType(), log.getReceiver());
        return true;
    }

    /**
     * 从Map中获取Long值
     *
     * @param map Map对象
     * @param key 键
     * @return Long值
     */
    private Long getLongValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return 0L;
        }
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof Integer) {
            return ((Integer) value).longValue();
        }
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).longValue();
        }
        return 0L;
    }
}
