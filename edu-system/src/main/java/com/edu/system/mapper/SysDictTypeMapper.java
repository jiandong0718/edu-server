package com.edu.system.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.system.domain.entity.SysDictType;

/**
 * 字典类型 Mapper
 */
@DS("system")
public interface SysDictTypeMapper extends BaseMapper<SysDictType> {
}
