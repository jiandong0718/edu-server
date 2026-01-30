package com.edu.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.system.domain.entity.SysDictData;
import com.edu.system.domain.entity.SysDictType;

import java.util.List;

/**
 * 字典服务接口
 */
public interface SysDictService extends IService<SysDictType> {

    /**
     * 根据字典编码获取字典数据
     */
    List<SysDictData> getDictDataByCode(String dictCode);

    /**
     * 新增字典类型
     */
    boolean addDictType(SysDictType dictType);

    /**
     * 修改字典类型
     */
    boolean updateDictType(SysDictType dictType);

    /**
     * 删除字典类型
     */
    boolean deleteDictType(Long dictTypeId);

    /**
     * 新增字典数据
     */
    boolean addDictData(SysDictData dictData);

    /**
     * 修改字典数据
     */
    boolean updateDictData(SysDictData dictData);

    /**
     * 删除字典数据
     */
    boolean deleteDictData(Long dictDataId);

    /**
     * 检查字典类型编码是否唯一
     */
    boolean checkDictTypeCodeUnique(String code, Long dictTypeId);

    /**
     * 刷新字典缓存
     */
    void refreshCache();
}
