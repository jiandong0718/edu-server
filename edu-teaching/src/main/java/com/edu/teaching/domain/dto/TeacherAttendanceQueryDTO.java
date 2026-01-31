package com.edu.teaching.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * 教师考勤查询DTO
 */
@Data
@Schema(description = "教师考勤查询DTO")
public class TeacherAttendanceQueryDTO {

    /**
     * 教师ID
     */
    @Schema(description = "教师ID")
    private Long teacherId;

    /**
     * 班级ID
     */
    @Schema(description = "班级ID")
    private Long classId;

    /**
     * 排课ID
     */
    @Schema(description = "排课ID")
    private Long scheduleId;

    /**
     * 状态：present-出勤，absent-缺勤，late-迟到，early_leave-早退，leave-请假
     */
    @Schema(description = "状态：present-出勤，absent-缺勤，late-迟到，early_leave-早退，leave-请假")
    private String status;

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
     * 教师姓名
     */
    @Schema(description = "教师姓名")
    private String teacherName;

    /**
     * 班级名称
     */
    @Schema(description = "班级名称")
    private String className;
}
