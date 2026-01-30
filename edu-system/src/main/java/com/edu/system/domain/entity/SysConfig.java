package com.edu.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统配置实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_config")
public class SysConfig extends BaseEntity {

    /**
     * 配置名称
     */
    private String configName;

    /**
     * 配置键
     */
    private String configKey;

    /**
     * 配置值
     */
    private String configValue;

    /**
     * 配置类型：string-字符串，number-数字，boolean-布尔，json-JSON
     */
    private String configType;

    /**
     * 配置分类
     */
    private String category;

    /**
     * 是否系统内置：0-否，1-是
     */
    private Integer isSystem;

    /**
     * 备注
     */
    private String remark;
}
