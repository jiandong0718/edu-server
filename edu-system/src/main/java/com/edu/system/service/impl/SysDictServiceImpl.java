package com.edu.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.exception.BusinessException;
import com.edu.system.domain.entity.SysDictData;
import com.edu.system.domain.entity.SysDictType;
import com.edu.system.mapper.SysDictDataMapper;
import com.edu.system.mapper.SysDictTypeMapper;
import com.edu.system.service.SysDictService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 字典服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysDictServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictType> implements SysDictService {

    private final SysDictDataMapper dictDataMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String DICT_CACHE_PREFIX = "sys:dict:";
    private static final long DICT_CACHE_EXPIRE = 24; // 小时

    @PostConstruct
    public void init() {
        refreshCache();
    }

    @Override
    public List<SysDictData> getDictDataByCode(String dictCode) {
        // 先从缓存获取
        String cacheKey = DICT_CACHE_PREFIX + dictCode;
        @SuppressWarnings("unchecked")
        List<SysDictData> dictDataList = (List<SysDictData>) redisTemplate.opsForValue().get(cacheKey);

        if (dictDataList == null) {
            // 从数据库查询
            dictDataList = dictDataMapper.selectByDictCode(dictCode);
            // 放入缓存
            if (dictDataList != null && !dictDataList.isEmpty()) {
                redisTemplate.opsForValue().set(cacheKey, dictDataList, DICT_CACHE_EXPIRE, TimeUnit.HOURS);
            }
        }

        return dictDataList;
    }

    @Override
    public boolean addDictType(SysDictType dictType) {
        if (!checkDictTypeCodeUnique(dictType.getCode(), null)) {
            throw new BusinessException("字典类型编码已存在");
        }
        return save(dictType);
    }

    @Override
    public boolean updateDictType(SysDictType dictType) {
        if (!checkDictTypeCodeUnique(dictType.getCode(), dictType.getId())) {
            throw new BusinessException("字典类型编码已存在");
        }
        boolean result = updateById(dictType);
        if (result) {
            // 清除缓存
            redisTemplate.delete(DICT_CACHE_PREFIX + dictType.getCode());
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDictType(Long dictTypeId) {
        SysDictType dictType = getById(dictTypeId);
        if (dictType == null) {
            return false;
        }

        // 删除字典数据
        dictDataMapper.delete(new LambdaQueryWrapper<SysDictData>()
                .eq(SysDictData::getDictTypeId, dictTypeId));

        // 删除字典类型
        boolean result = removeById(dictTypeId);

        if (result) {
            // 清除缓存
            redisTemplate.delete(DICT_CACHE_PREFIX + dictType.getCode());
        }

        return result;
    }

    @Override
    public boolean addDictData(SysDictData dictData) {
        boolean result = dictDataMapper.insert(dictData) > 0;
        if (result) {
            // 清除缓存
            SysDictType dictType = getById(dictData.getDictTypeId());
            if (dictType != null) {
                redisTemplate.delete(DICT_CACHE_PREFIX + dictType.getCode());
            }
        }
        return result;
    }

    @Override
    public boolean updateDictData(SysDictData dictData) {
        boolean result = dictDataMapper.updateById(dictData) > 0;
        if (result) {
            // 清除缓存
            SysDictType dictType = getById(dictData.getDictTypeId());
            if (dictType != null) {
                redisTemplate.delete(DICT_CACHE_PREFIX + dictType.getCode());
            }
        }
        return result;
    }

    @Override
    public boolean deleteDictData(Long dictDataId) {
        SysDictData dictData = dictDataMapper.selectById(dictDataId);
        if (dictData == null) {
            return false;
        }

        boolean result = dictDataMapper.deleteById(dictDataId) > 0;
        if (result) {
            // 清除缓存
            SysDictType dictType = getById(dictData.getDictTypeId());
            if (dictType != null) {
                redisTemplate.delete(DICT_CACHE_PREFIX + dictType.getCode());
            }
        }
        return result;
    }

    @Override
    public boolean checkDictTypeCodeUnique(String code, Long dictTypeId) {
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictType::getCode, code);
        if (dictTypeId != null) {
            wrapper.ne(SysDictType::getId, dictTypeId);
        }
        return count(wrapper) == 0;
    }

    @Override
    public void refreshCache() {
        log.info("刷新字典缓存...");
        // 获取所有字典类型
        List<SysDictType> dictTypes = list(new LambdaQueryWrapper<SysDictType>()
                .eq(SysDictType::getStatus, 1));

        for (SysDictType dictType : dictTypes) {
            String cacheKey = DICT_CACHE_PREFIX + dictType.getCode();
            List<SysDictData> dictDataList = dictDataMapper.selectByDictCode(dictType.getCode());
            if (dictDataList != null && !dictDataList.isEmpty()) {
                redisTemplate.opsForValue().set(cacheKey, dictDataList, DICT_CACHE_EXPIRE, TimeUnit.HOURS);
            }
        }
        log.info("字典缓存刷新完成，共 {} 个字典类型", dictTypes.size());
    }
}
