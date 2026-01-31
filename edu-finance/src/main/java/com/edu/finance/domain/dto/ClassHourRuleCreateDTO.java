package com.edu.finance.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 课时消课规则创建DTO
 */
@Data
@Schema(description = "课时消课规则创建DTO")
public class ClassHourRuleCreateDTO {

    /**
     * 规则名称
     */
    @NotBlank(message = "规则名称不能为空")
    @Schema(description = "规则名称", required = true, example = "一对一课程规则")
    private String name;

    /**
     * 课程ID（为空表示通用规则）
     */
    @Schema(description = "课程ID（为空表示通用规则）", example = "1")
    private Long courseId;

    /**
     * 班级类型：one_on_one-一对一，small_class-小班课，large_class-大班课
     */
    @Schema(description = "班级类型：one_on_one-一对一，small_class-小班课，large_class-大班课", example = "one_on_one")
    private String classType;

    /**
     * 扣减类型：per_hour-按课时，per_class-按课次，custom-自定义
     */
    @NotBlank(message = "扣减类型不能为空")
    @Schema(description = "扣减类型：per_hour-按课时，per_class-按课次，custom-自定义", required = true, example = "per_class")
    private String deductType;

    /**
     * 扣减数量
     */
    @NotNull(message = "扣减数量不能为空")
    @Schema(description = "扣减数量", required = true, example = "1.0")
    private BigDecimal deductAmount;

    /**
     * 状态：active-启用，inactive-停用
     */
    @Schema(description = "状态：active-启用，inactive-停用", example = "active")
    private String status;

    /**
     * 校区ID
     */
    @Schema(description = "校区ID", example = "1")
    private Long campusId;

    /**
     * 备注
     */
    @Schema(description = "备注", example = "一对一课程按课次扣减")
    private String remark;
}
