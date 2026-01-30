package com.edu.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.system.domain.entity.SysConfig;

/**
 * 系统配置服务接口
 */
public interface SysConfigService extends IService<SysConfig> {

    /**
     * 根据配置键获取配置值
     */
    String getConfigValue(String configKey);

    /**
     * 根据配置键获取配置值（带默认值）
     */
    String getConfigValue(String configKey, String defaultValue);

    /**
     * 根据配置键获取整数配置值
     */
    Integer getConfigValueAsInt(String configKey, Integer defaultValue);

    /**
     * 根据配置键获取布尔配置值
     */
    Boolean getConfigValueAsBoolean(String configKey, Boolean defaultValue);

    /**
     * 更新配置值
     */
    boolean updateConfigValue(String configKey, String configValue);

    /**
     * 检查配置键是否唯一
     */
    boolean checkConfigKeyUnique(String configKey, Long id);
}
