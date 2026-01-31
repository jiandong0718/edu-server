package com.edu.teaching.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 教师课酬配置VO
 */
@Data
@Schema(description = "教师课酬配置VO")
public class TeacherSalaryVO {

    @Schema(description = "课酬配置ID")
    private Long id;

    @Schema(description = "教师ID")
    private Long teacherId;

    @Schema(description = "教师姓名")
    private String teacherName;

    @Schema(description = "课程ID")
    private Long courseId;

    @Schema(description = "课程名称")
    private String courseName;

    @Schema(description = "班级类型")
    private String classType;

    @Schema(description = "班级类型名称")
    private String classTypeName;

    @Schema(description = "课酬类型")
    private String salaryType;

    @Schema(description = "课酬类型名称")
    private String salaryTypeName;

    @Schema(description = "课酬金额")
    private BigDecimal amount;

    @Schema(description = "生效日期")
    private LocalDate effectiveDate;

    @Schema(description = "失效日期")
    private LocalDate expiryDate;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;

    @Schema(description = "是否当前有效")
    private Boolean currentlyValid;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
