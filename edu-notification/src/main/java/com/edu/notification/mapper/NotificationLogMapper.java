package com.edu.notification.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.notification.domain.dto.NotificationLogQueryDTO;
import com.edu.notification.domain.entity.NotificationLog;
import com.edu.notification.domain.vo.NotificationLogVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 通知发送记录Mapper
 */
@Mapper
public interface NotificationLogMapper extends BaseMapper<NotificationLog> {

    /**
     * 分页查询发送记录
     *
     * @param page  分页对象
     * @param query 查询条件
     * @return 分页结果
     */
    IPage<NotificationLogVO> selectLogPage(Page<NotificationLogVO> page, @Param("query") NotificationLogQueryDTO query);

    /**
     * 查询发送记录详情
     *
     * @param id 记录ID
     * @return 记录详情
     */
    NotificationLogVO selectLogById(@Param("id") Long id);

    /**
     * 统计总数
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param campusId  校区ID
     * @return 统计结果
     */
    Map<String, Object> selectTotalStatistics(@Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate,
                                               @Param("campusId") Long campusId);

    /**
     * 按类型统计
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param campusId  校区ID
     * @return 统计结果列表
     */
    List<Map<String, Object>> selectTypeStatistics(@Param("startDate") LocalDateTime startDate,
                                                    @Param("endDate") LocalDateTime endDate,
                                                    @Param("campusId") Long campusId);

    /**
     * 按日期统计
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param campusId  校区ID
     * @return 统计结果列表
     */
    List<Map<String, Object>> selectDateStatistics(@Param("startDate") LocalDateTime startDate,
                                                    @Param("endDate") LocalDateTime endDate,
                                                    @Param("campusId") Long campusId);
}
