package com.edu.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.system.domain.entity.SysDictData;

import java.util.List;

/**
 * 字典数据 Mapper
 */
public interface SysDictDataMapper extends BaseMapper<SysDictData> {

    /**
     * 根据字典类型编码查询字典数据
     */
    List<SysDictData> selectByDictCode(String dictCode);
}
