package com.edu.teaching.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 班级升班结果VO
 */
@Data
@Schema(description = "班级升班结果VO")
public class ClassPromotionResultVO {

    @Schema(description = "原班级ID")
    private Long originalClassId;

    @Schema(description = "原班级名称")
    private String originalClassName;

    @Schema(description = "目标班级ID")
    private Long targetClassId;

    @Schema(description = "目标班级名称")
    private String targetClassName;

    @Schema(description = "是否创建了新班级")
    private Boolean newClassCreated;

    @Schema(description = "转移的学员数量")
    private Integer transferredStudentCount;

    @Schema(description = "转移的学员ID列表")
    private List<Long> transferredStudentIds;

    @Schema(description = "升班时间")
    private LocalDateTime promotionTime;

    @Schema(description = "是否成功")
    private Boolean success;

    @Schema(description = "失败原因")
    private String failureReason;
}
