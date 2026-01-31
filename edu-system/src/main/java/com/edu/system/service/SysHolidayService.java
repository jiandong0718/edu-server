package com.edu.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.system.domain.dto.SysHolidayDTO;
import com.edu.system.domain.dto.SysHolidayQueryDTO;
import com.edu.system.domain.entity.SysHoliday;
import com.edu.system.domain.vo.SysHolidayVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 节假日服务接口
 */
public interface SysHolidayService extends IService<SysHoliday> {

    /**
     * 分页查询节假日列表
     */
    Page<SysHolidayVO> pageHolidays(SysHolidayQueryDTO queryDTO);

    /**
     * 获取节假日详情
     */
    SysHolidayVO getHolidayById(Long id);

    /**
     * 新增节假日
     */
    boolean addHoliday(SysHolidayDTO holidayDTO);

    /**
     * 修改节假日
     */
    boolean updateHoliday(SysHolidayDTO holidayDTO);

    /**
     * 删除节假日
     */
    boolean deleteHoliday(Long id);

    /**
     * 批量删除节假日
     */
    boolean batchDeleteHolidays(List<Long> ids);

    /**
     * 修改节假日状态
     */
    boolean updateStatus(Long id, Integer status);

    /**
     * 检查日期范围是否有冲突
     */
    boolean checkDateConflict(LocalDate startDate, LocalDate endDate, Long id, Long campusId);

    /**
     * 判断指定日期是否为节假日
     */
    boolean isHoliday(LocalDate date, Long campusId);

    /**
     * 判断指定日期是否为工作日（调休上班）
     */
    boolean isWorkday(LocalDate date, Long campusId);

    /**
     * 获取指定日期范围内的节假日列表
     */
    List<SysHoliday> getHolidaysByDateRange(LocalDate startDate, LocalDate endDate, Long campusId);
}
