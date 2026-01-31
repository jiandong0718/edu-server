package com.edu.system.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.system.domain.entity.SysHoliday;
import org.apache.ibatis.annotations.Mapper;

/**
 * 节假日 Mapper
 */
@Mapper
public interface SysHolidayMapper extends BaseMapper<SysHoliday> {
}
