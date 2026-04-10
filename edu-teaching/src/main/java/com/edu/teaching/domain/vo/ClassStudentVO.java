package com.edu.teaching.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * 班级学员列表项
 */
@Data
@Schema(description = "班级学员列表项")
public class ClassStudentVO {

    @Schema(description = "班级学员关系ID")
    private Long id;

    @Schema(description = "班级ID")
    private Long classId;

    @Schema(description = "学员ID")
    private Long studentId;

    @Schema(description = "学员姓名")
    private String studentName;

    @Schema(description = "学员头像")
    private String studentAvatar;

    @Schema(description = "学员手机号")
    private String studentPhone;

    @Schema(description = "家长手机号")
    private String parentPhone;

    @Schema(description = "加入日期")
    private LocalDate joinDate;

    @Schema(description = "退出日期")
    private LocalDate leaveDate;

    @Schema(description = "状态：active-在读，left-已退出，graduated-已结业")
    private String status;

    @Schema(description = "出勤率")
    private Double attendanceRate;

    @Schema(description = "已完成课时")
    private Integer completedHours;
}
