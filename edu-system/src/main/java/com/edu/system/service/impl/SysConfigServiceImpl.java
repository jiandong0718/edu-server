package com.edu.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.exception.BusinessException;
import com.edu.system.domain.dto.SysConfigBatchUpdateDTO;
import com.edu.system.domain.dto.SysConfigQueryDTO;
import com.edu.system.domain.entity.SysConfig;
import com.edu.system.mapper.SysConfigMapper;
import com.edu.system.service.SysConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统配置服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    private static final String CACHE_NAME = "sys_config";

    @Override
    public IPage<SysConfig> pageList(IPage<SysConfig> page, SysConfigQueryDTO queryDTO) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();

        if (queryDTO != null) {
            wrapper.like(StrUtil.isNotBlank(queryDTO.getConfigKey()),
                    SysConfig::getConfigKey, queryDTO.getConfigKey())
                .eq(StrUtil.isNotBlank(queryDTO.getConfigGroup()),
                    SysConfig::getConfigGroup, queryDTO.getConfigGroup())
                .eq(StrUtil.isNotBlank(queryDTO.getConfigType()),
                    SysConfig::getConfigType, queryDTO.getConfigType())
                .eq(queryDTO.getIsSystem() != null,
                    SysConfig::getIsSystem, queryDTO.getIsSystem())
                .eq(queryDTO.getStatus() != null,
                    SysConfig::getStatus, queryDTO.getStatus());
        }

        wrapper.orderByAsc(SysConfig::getConfigGroup, SysConfig::getSort);
        return page(page, wrapper);
    }

    @Override
    public List<SysConfig> listByGroup(String configGroup) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StrUtil.isNotBlank(configGroup), SysConfig::getConfigGroup, configGroup)
            .eq(SysConfig::getStatus, 1)
            .orderByAsc(SysConfig::getSort);
        return list(wrapper);
    }

    @Override
    public List<String> listGroups() {
        List<SysConfig> configs = list(new LambdaQueryWrapper<SysConfig>()
            .select(SysConfig::getConfigGroup)
            .groupBy(SysConfig::getConfigGroup)
            .orderByAsc(SysConfig::getConfigGroup));

        return configs.stream()
            .map(SysConfig::getConfigGroup)
            .filter(StrUtil::isNotBlank)
            .distinct()
            .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = CACHE_NAME, key = "#configKey", unless = "#result == null")
    public String getConfigValue(String configKey) {
        return getConfigValue(configKey, null);
    }

    @Override
    @Cacheable(value = CACHE_NAME, key = "#configKey", unless = "#result == #defaultValue")
    public String getConfigValue(String configKey, String defaultValue) {
        SysConfig config = getOne(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigKey, configKey)
                .eq(SysConfig::getStatus, 1));
        return config != null ? config.getConfigValue() : defaultValue;
    }

    @Override
    public Integer getConfigValueAsInt(String configKey, Integer defaultValue) {
        String value = getConfigValue(configKey);
        if (StrUtil.isBlank(value)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            log.warn("配置值转换为整数失败: configKey={}, value={}", configKey, value);
            return defaultValue;
        }
    }

    @Override
    public Boolean getConfigValueAsBoolean(String configKey, Boolean defaultValue) {
        String value = getConfigValue(configKey);
        if (StrUtil.isBlank(value)) {
            return defaultValue;
        }
        return "true".equalsIgnoreCase(value) || "1".equals(value);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchUpdate(SysConfigBatchUpdateDTO batchUpdateDTO) {
        if (batchUpdateDTO == null || batchUpdateDTO.getConfigs() == null || batchUpdateDTO.getConfigs().isEmpty()) {
            throw new BusinessException("配置列表不能为空");
        }

        for (SysConfigBatchUpdateDTO.ConfigItem item : batchUpdateDTO.getConfigs()) {
            SysConfig config = getById(item.getId());
            if (config == null) {
                throw new BusinessException("配置不存在: " + item.getConfigKey());
            }

            // 系统内置参数可以修改值，但不能删除
            config.setConfigValue(item.getConfigValue());
            updateById(config);
        }

        // 清除缓存
        clearCache();
        return true;
    }

    @Override
    @CacheEvict(value = CACHE_NAME, key = "#configKey")
    public boolean updateConfigValue(String configKey, String configValue) {
        SysConfig config = getOne(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigKey, configKey));
        if (config == null) {
            throw new BusinessException("配置不存在: " + configKey);
        }
        config.setConfigValue(configValue);
        return updateById(config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resetToDefault(Long id) {
        SysConfig config = getById(id);
        if (config == null) {
            throw new BusinessException("配置不存在");
        }

        // 这里可以从初始化SQL或配置文件中读取默认值
        // 简化处理：将值设为空
        config.setConfigValue("");
        boolean result = updateById(config);

        if (result) {
            clearCache();
        }
        return result;
    }

    @Override
    public boolean checkConfigKeyUnique(String configKey, Long id) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getConfigKey, configKey);
        if (id != null) {
            wrapper.ne(SysConfig::getId, id);
        }
        return count(wrapper) == 0;
    }

    @Override
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public void refreshCache() {
        log.info("刷新系统配置缓存");
    }

    @Override
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public void clearCache() {
        log.info("清除系统配置缓存");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public boolean save(SysConfig entity) {
        // 检查配置键唯一性
        if (!checkConfigKeyUnique(entity.getConfigKey(), entity.getId())) {
            throw new BusinessException("配置键已存在: " + entity.getConfigKey());
        }
        return super.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public boolean updateById(SysConfig entity) {
        // 检查配置键唯一性
        if (!checkConfigKeyUnique(entity.getConfigKey(), entity.getId())) {
            throw new BusinessException("配置键已存在: " + entity.getConfigKey());
        }
        return super.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public boolean removeById(Long id) {
        SysConfig config = getById(id);
        if (config == null) {
            throw new BusinessException("配置不存在");
        }

        // 系统内置参数不可删除
        if (config.getIsSystem() != null && config.getIsSystem() == 1) {
            throw new BusinessException("系统内置参数不可删除");
        }

        return super.removeById(id);
    }
}
