package com.edu.teaching.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 教师课酬配置DTO
 */
@Data
@Schema(description = "教师课酬配置DTO")
public class TeacherSalaryDTO {

    @Schema(description = "课酬配置ID（修改时必填）")
    private Long id;

    @NotNull(message = "教师ID不能为空")
    @Schema(description = "教师ID")
    private Long teacherId;

    @Schema(description = "课程ID（为空表示通用配置）")
    private Long courseId;

    @Schema(description = "班级类型：one_to_one-一对一，small_class-小班课，large_class-大班课")
    private String classType;

    @NotNull(message = "课酬类型不能为空")
    @Schema(description = "课酬类型：per_hour-按课时，per_class-按课次，fixed-固定")
    private String salaryType;

    @NotNull(message = "课酬金额不能为空")
    @DecimalMin(value = "0.01", message = "课酬金额必须大于0")
    @Schema(description = "课酬金额")
    private BigDecimal amount;

    @NotNull(message = "生效日期不能为空")
    @Schema(description = "生效日期")
    private LocalDate effectiveDate;

    @Schema(description = "失效日期（为空表示长期有效）")
    private LocalDate expiryDate;

    @Schema(description = "状态：0-禁用，1-启用", defaultValue = "1")
    private Integer status;

    @Schema(description = "备注")
    private String remark;
}
