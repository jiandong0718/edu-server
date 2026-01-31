package com.edu.system.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 系统参数配置查询 DTO
 */
@Data
@Schema(description = "系统参数配置查询对象")
public class SysConfigQueryDTO {

    @Schema(description = "参数键（模糊查询）")
    private String configKey;

    @Schema(description = "参数分组")
    private String configGroup;

    @Schema(description = "参数类型")
    private String configType;

    @Schema(description = "是否系统内置：0-否，1-是")
    private Integer isSystem;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;
}
