package com.edu.teaching.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 批量班级结业结果VO
 */
@Data
@Schema(description = "批量班级结业结果VO")
public class BatchClassGraduationResultVO {

    @Schema(description = "总数")
    private Integer total;

    @Schema(description = "成功数量")
    private Integer successCount;

    @Schema(description = "失败数量")
    private Integer failureCount;

    @Schema(description = "详细结果列表")
    private List<ClassGraduationResultVO> results;
}
