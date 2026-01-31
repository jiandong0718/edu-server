package com.edu.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.system.domain.dto.SysConfigBatchUpdateDTO;
import com.edu.system.domain.dto.SysConfigQueryDTO;
import com.edu.system.domain.entity.SysConfig;

import java.util.List;
import java.util.Map;

/**
 * 系统配置服务接口
 */
public interface SysConfigService extends IService<SysConfig> {

    /**
     * 分页查询配置列表
     */
    IPage<SysConfig> pageList(IPage<SysConfig> page, SysConfigQueryDTO queryDTO);

    /**
     * 根据分组查询配置列表
     */
    List<SysConfig> listByGroup(String configGroup);

    /**
     * 获取所有分组
     */
    List<String> listGroups();

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
     * 批量更新配置值
     */
    boolean batchUpdate(SysConfigBatchUpdateDTO batchUpdateDTO);

    /**
     * 更新配置值
     */
    boolean updateConfigValue(String configKey, String configValue);

    /**
     * 重置为默认值
     */
    boolean resetToDefault(Long id);

    /**
     * 检查配置键是否唯一
     */
    boolean checkConfigKeyUnique(String configKey, Long id);

    /**
     * 刷新缓存
     */
    void refreshCache();

    /**
     * 清除缓存
     */
    void clearCache();

    /**
     * 删除配置
     */
    boolean removeById(Long id);
}
