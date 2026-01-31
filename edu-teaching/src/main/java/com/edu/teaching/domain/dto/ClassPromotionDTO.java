package com.edu.teaching.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 班级升班请求DTO
 */
@Data
@Schema(description = "班级升班请求DTO")
public class ClassPromotionDTO {

    @NotNull(message = "班级ID不能为空")
    @Schema(description = "当前班级ID")
    private Long classId;

    @NotNull(message = "目标课程ID不能为空")
    @Schema(description = "目标课程ID（下一级别课程）")
    private Long targetCourseId;

    @Schema(description = "目标班级ID（如果指定，则转入已有班级；否则创建新班级）")
    private Long targetClassId;

    @Schema(description = "新班级名称（创建新班级时必填）")
    private String newClassName;

    @Schema(description = "新班级编码（创建新班级时必填）")
    private String newClassCode;

    @Schema(description = "主讲教师ID（创建新班级时）")
    private Long teacherId;

    @Schema(description = "助教ID（创建新班级时）")
    private Long assistantId;

    @Schema(description = "教室ID（创建新班级时）")
    private Long classroomId;

    @Schema(description = "班级容量（创建新班级时）")
    private Integer capacity;

    @Schema(description = "是否保留原班级（true-保留为历史记录，false-删除）", defaultValue = "true")
    private Boolean keepOriginalClass = true;

    @Schema(description = "备注")
    private String remark;
}
