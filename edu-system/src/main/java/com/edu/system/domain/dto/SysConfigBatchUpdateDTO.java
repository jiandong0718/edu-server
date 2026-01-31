package com.edu.system.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 系统参数配置批量更新 DTO
 */
@Data
@Schema(description = "系统参数配置批量更新对象")
public class SysConfigBatchUpdateDTO {

    @Schema(description = "配置列表", required = true)
    @NotEmpty(message = "配置列表不能为空")
    private List<ConfigItem> configs;

    @Data
    @Schema(description = "配置项")
    public static class ConfigItem {

        @Schema(description = "配置ID", required = true)
        private Long id;

        @Schema(description = "参数键", required = true)
        private String configKey;

        @Schema(description = "参数值")
        private String configValue;
    }
}
