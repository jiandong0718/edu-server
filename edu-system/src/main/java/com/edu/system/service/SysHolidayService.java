package com.edu.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.system.domain.entity.SysHoliday;

import java.time.LocalDate;
import java.util.List;

/**
 * 节假日服务接口
 */
public interface SysHolidayService extends IService<SysHoliday> {

    /**
     * 检查日期范围是否有冲突
     */
    boolean checkDateConflict(LocalDate startDate, LocalDate endDate, Long id);

    /**
     * 判断指定日期是否为节假日
     */
    boolean isHoliday(LocalDate date);

    /**
     * 判断指定日期是否为工作日
     */
    boolean isWorkday(LocalDate date);

    /**
     * 获取指定日期范围内的节假日列表
     */
    List<SysHoliday> getHolidaysByDateRange(LocalDate startDate, LocalDate endDate);
}
