package com.edu.teaching.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 批量班级升班请求DTO
 */
@Data
@Schema(description = "批量班级升班请求DTO")
public class BatchClassPromotionDTO {

    @NotEmpty(message = "班级ID列表不能为空")
    @Schema(description = "班级ID列表")
    private List<Long> classIds;

    @NotNull(message = "目标课程ID不能为空")
    @Schema(description = "目标课程ID（下一级别课程）")
    private Long targetCourseId;

    @Schema(description = "是否保留原班级（true-保留为历史记录，false-删除）", defaultValue = "true")
    private Boolean keepOriginalClass = true;

    @Schema(description = "备注")
    private String remark;
}
