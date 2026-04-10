package com.edu.finance.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 课时余额查询DTO
 */
@Data
@Schema(description = "课时余额查询DTO")
public class ClassHourBalanceQueryDTO {

    /**
     * 学员ID
     */
    @Schema(description = "学员ID")
    private Long studentId;

    /**
     * 课程ID
     */
    @Schema(description = "课程ID")
    private Long courseId;

    /**
     * 校区ID
     */
    @Schema(description = "校区ID")
    private Long campusId;

    /**
     * 状态：active-正常，frozen-冻结，exhausted-已用完
     */
    @Schema(description = "状态：active-正常，frozen-冻结，exhausted-已用完")
    private String status;

    /**
     * 学员姓名（模糊匹配）
     */
    @Schema(description = "学员姓名（模糊匹配）")
    private String studentName;

    /**
     * 学员手机号（模糊匹配）
     */
    @Schema(description = "学员手机号（模糊匹配）")
    private String studentPhone;

    /**
     * 课程名称（模糊匹配）
     */
    @Schema(description = "课程名称（模糊匹配）")
    private String courseName;
}
