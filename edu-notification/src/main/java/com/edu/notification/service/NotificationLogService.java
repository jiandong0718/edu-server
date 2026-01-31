package com.edu.notification.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.notification.domain.dto.NotificationLogQueryDTO;
import com.edu.notification.domain.entity.NotificationLog;
import com.edu.notification.domain.vo.BatchResendResultVO;
import com.edu.notification.domain.vo.NotificationLogVO;
import com.edu.notification.domain.vo.NotificationStatisticsVO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知发送记录服务接口
 */
public interface NotificationLogService extends IService<NotificationLog> {

    /**
     * 分页查询发送记录
     *
     * @param page  分页对象
     * @param query 查询条件
     * @return 分页结果
     */
    IPage<NotificationLogVO> getLogPage(Page<NotificationLogVO> page, NotificationLogQueryDTO query);

    /**
     * 查询发送记录详情
     *
     * @param id 记录ID
     * @return 记录详情
     */
    NotificationLogVO getLogById(Long id);

    /**
     * 获取发送统计
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param campusId  校区ID
     * @return 统计结果
     */
    NotificationStatisticsVO getStatistics(LocalDateTime startDate, LocalDateTime endDate, Long campusId);

    /**
     * 重发失败通知
     *
     * @param id 记录ID
     * @return 是否成功
     */
    boolean resend(Long id);

    /**
     * 批量重发
     *
     * @param ids 记录ID列表
     * @return 批量重发结果
     */
    BatchResendResultVO batchResend(List<Long> ids);

    /**
     * 记录通知发送日志
     *
     * @param log 发送记录
     */
    void saveLog(NotificationLog log);
}
