package com.edu.teaching.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 学员签到DTO
 */
@Data
public class AttendanceSignInDTO {

    /**
     * 排课ID
     */
    @NotNull(message = "排课ID不能为空")
    private Long scheduleId;

    /**
     * 学员ID
     */
    @NotNull(message = "学员ID不能为空")
    private Long studentId;

    /**
     * 考勤状态：present-出勤，absent-缺勤，late-迟到，leave-请假
     */
    @NotNull(message = "考勤状态不能为空")
    private String status;

    /**
     * 备注
     */
    private String remark;
}
