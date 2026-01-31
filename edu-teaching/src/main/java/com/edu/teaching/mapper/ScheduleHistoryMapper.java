package com.edu.teaching.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.teaching.domain.entity.ScheduleHistory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 调课历史 Mapper
 */
public interface ScheduleHistoryMapper extends BaseMapper<ScheduleHistory> {

    /**
     * 查询排课的调课历史
     */
    List<ScheduleHistory> selectHistoryByScheduleId(@Param("scheduleId") Long scheduleId);
}
