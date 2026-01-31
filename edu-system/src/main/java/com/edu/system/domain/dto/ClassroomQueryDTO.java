package com.edu.system.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 教室查询DTO
 */
@Data
@Schema(description = "教室查询参数")
public class ClassroomQueryDTO {

    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页数量", example = "10")
    private Integer pageSize = 10;

    @Schema(description = "关键词(教室名称或编码)")
    private String keyword;

    @Schema(description = "校区ID")
    private Long campusId;

    @Schema(description = "所属楼栋")
    private String building;

    @Schema(description = "楼层")
    private Integer floor;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;

    @Schema(description = "最小容量")
    private Integer minCapacity;

    @Schema(description = "最大容量")
    private Integer maxCapacity;
}
