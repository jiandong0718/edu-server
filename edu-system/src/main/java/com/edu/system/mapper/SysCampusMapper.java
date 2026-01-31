package com.edu.system.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.system.domain.entity.SysCampus;

/**
 * 校区 Mapper
 */
@DS("system")
public interface SysCampusMapper extends BaseMapper<SysCampus> {
}
