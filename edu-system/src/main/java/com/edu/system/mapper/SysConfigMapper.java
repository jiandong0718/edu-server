package com.edu.system.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.system.domain.entity.SysConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统配置 Mapper
 */
@Mapper
public interface SysConfigMapper extends BaseMapper<SysConfig> {
}
