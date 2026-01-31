package com.edu.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统参数配置实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_config")
@Schema(description = "系统参数配置")
public class SysConfig extends BaseEntity {

    /**
     * 参数键
     */
    @Schema(description = "参数键")
    private String configKey;

    /**
     * 参数值
     */
    @Schema(description = "参数值")
    private String configValue;

    /**
     * 参数类型：string/number/boolean/json
     */
    @Schema(description = "参数类型")
    private String configType;

    /**
     * 参数分组
     */
    @Schema(description = "参数分组")
    private String configGroup;

    /**
     * 参数说明
     */
    @Schema(description = "参数说明")
    private String description;

    /**
     * 是否系统内置：0-否，1-是
     */
    @Schema(description = "是否系统内置")
    private Integer isSystem;

    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer sort;

    /**
     * 状态：0-禁用，1-启用
     */
    @Schema(description = "状态")
    private Integer status;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
}
