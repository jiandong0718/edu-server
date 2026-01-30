package com.edu.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.system.domain.entity.SysHoliday;
import com.edu.system.mapper.SysHolidayMapper;
import com.edu.system.service.SysHolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * 节假日服务实现
 */
@Service
@RequiredArgsConstructor
public class SysHolidayServiceImpl extends ServiceImpl<SysHolidayMapper, SysHoliday> implements SysHolidayService {

    @Override
    public boolean checkDateConflict(LocalDate startDate, LocalDate endDate, Long id) {
        LambdaQueryWrapper<SysHoliday> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w
                .le(SysHoliday::getStartDate, endDate)
                .ge(SysHoliday::getEndDate, startDate)
        );
        if (id != null) {
            wrapper.ne(SysHoliday::getId, id);
        }
        return count(wrapper) == 0;
    }

    @Override
    public boolean isHoliday(LocalDate date) {
        LambdaQueryWrapper<SysHoliday> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysHoliday::getStatus, 1)
                .le(SysHoliday::getStartDate, date)
                .ge(SysHoliday::getEndDate, date)
                .eq(SysHoliday::getIsWorkday, 0);
        return count(wrapper) > 0;
    }

    @Override
    public boolean isWorkday(LocalDate date) {
        LambdaQueryWrapper<SysHoliday> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysHoliday::getStatus, 1)
                .le(SysHoliday::getStartDate, date)
                .ge(SysHoliday::getEndDate, date)
                .eq(SysHoliday::getIsWorkday, 1);
        return count(wrapper) > 0;
    }

    @Override
    public List<SysHoliday> getHolidaysByDateRange(LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<SysHoliday> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysHoliday::getStatus, 1)
                .and(w -> w
                        .le(SysHoliday::getStartDate, endDate)
                        .ge(SysHoliday::getEndDate, startDate)
                )
                .orderByAsc(SysHoliday::getStartDate);
        return list(wrapper);
    }
}
