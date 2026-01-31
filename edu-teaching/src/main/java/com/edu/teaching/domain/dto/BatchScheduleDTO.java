package com.edu.teaching.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 批量排课DTO
 */
@Data
@Schema(description = "批量排课DTO")
public class BatchScheduleDTO {

    @NotNull(message = "班级ID不能为空")
    @Schema(description = "班级ID")
    private Long classId;

    @Schema(description = "课程ID（可选，默认使用班级关联的课程）")
    private Long courseId;

    @Schema(description = "教师ID（可选，默认使用班级关联的教师）")
    private Long teacherId;

    @Schema(description = "教室ID（可选，默认使用班级关联的教室）")
    private Long classroomId;

    @NotNull(message = "开始日期不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "开始日期", example = "2024-01-01")
    private LocalDate startDate;

    @Schema(description = "结束日期（与总课次二选一）", example = "2024-06-30")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Schema(description = "总课次（与结束日期二选一）", example = "40")
    private Integer totalLessons;

    @NotEmpty(message = "上课星期不能为空")
    @Schema(description = "上课星期：1-7（1表示周一，7表示周日）", example = "[1, 3, 5]")
    private List<Integer> weekdays;

    @NotNull(message = "开始时间不能为空")
    @DateTimeFormat(pattern = "HH:mm")
    @Schema(description = "开始时间", example = "09:00")
    private LocalTime startTime;

    @NotNull(message = "结束时间不能为空")
    @DateTimeFormat(pattern = "HH:mm")
    @Schema(description = "结束时间", example = "11:00")
    private LocalTime endTime;

    @Schema(description = "每节课时数", example = "2", defaultValue = "2")
    private Integer classHours;

    @Schema(description = "是否跳过节假日", defaultValue = "true")
    private Boolean skipHolidays;

    @Schema(description = "是否跳过周末", defaultValue = "false")
    private Boolean skipWeekends;

    @Schema(description = "重复规则：weekly-每周，monthly-每月", example = "weekly", defaultValue = "weekly")
    private String repeatRule;

    @Schema(description = "课节主题前缀", example = "第{n}课")
    private String topicPrefix;

    @Schema(description = "备注")
    private String remark;
}
