package com.edu.system.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 系统参数配置 DTO
 */
@Data
@Schema(description = "系统参数配置数据传输对象")
public class SysConfigDTO {

    @Schema(description = "配置ID")
    private Long id;

    @Schema(description = "参数键", required = true)
    @NotBlank(message = "参数键不能为空")
    private String configKey;

    @Schema(description = "参数值")
    private String configValue;

    @Schema(description = "参数类型：string/number/boolean/json", required = true)
    @NotBlank(message = "参数类型不能为空")
    private String configType;

    @Schema(description = "参数分组")
    private String configGroup;

    @Schema(description = "参数说明")
    private String description;

    @Schema(description = "是否系统内置：0-否，1-是")
    private Integer isSystem;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "状态：0-禁用，1-启用")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "备注")
    private String remark;
}
