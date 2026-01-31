package com.edu.finance.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 课时消课规则查询DTO
 */
@Data
@Schema(description = "课时消课规则查询DTO")
public class ClassHourRuleQueryDTO {

    /**
     * 规则名称（模糊查询）
     */
    @Schema(description = "规则名称（模糊查询）", example = "一对一")
    private String name;

    /**
     * 课程ID
     */
    @Schema(description = "课程ID", example = "1")
    private Long courseId;

    /**
     * 班级类型
     */
    @Schema(description = "班级类型：one_on_one-一对一，small_class-小班课，large_class-大班课", example = "one_on_one")
    private String classType;

    /**
     * 扣减类型
     */
    @Schema(description = "扣减类型：per_hour-按课时，per_class-按课次，custom-自定义", example = "per_class")
    private String deductType;

    /**
     * 状态
     */
    @Schema(description = "状态：active-启用，inactive-停用", example = "active")
    private String status;

    /**
     * 校区ID
     */
    @Schema(description = "校区ID", example = "1")
    private Long campusId;
}
