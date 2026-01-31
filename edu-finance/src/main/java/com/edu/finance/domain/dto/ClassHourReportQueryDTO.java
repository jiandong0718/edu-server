package com.edu.finance.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * 课消报表查询DTO
 */
@Data
@Schema(description = "课消报表查询DTO")
public class ClassHourReportQueryDTO {

    /**
     * 开始日期
     */
    @Schema(description = "开始日期")
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @Schema(description = "结束日期")
    private LocalDate endDate;

    /**
     * 校区ID
     */
    @Schema(description = "校区ID")
    private Long campusId;

    /**
     * 班级ID
     */
    @Schema(description = "班级ID")
    private Long classId;

    /**
     * 课程ID
     */
    @Schema(description = "课程ID")
    private Long courseId;

    /**
     * 教师ID
     */
    @Schema(description = "教师ID")
    private Long teacherId;

    /**
     * 统计维度：class-班级，course-课程，teacher-教师
     */
    @Schema(description = "统计维度：class-班级，course-课程，teacher-教师")
    private String dimension;

    /**
     * 排序字段
     */
    @Schema(description = "排序字段")
    private String sortField;

    /**
     * 排序方式：asc-升序，desc-降序
     */
    @Schema(description = "排序方式：asc-升序，desc-降序")
    private String sortOrder;
}
