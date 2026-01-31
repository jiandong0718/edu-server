package com.edu.teaching.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 教师请假DTO
 */
@Data
public class TeacherLeaveDTO {

    /**
     * 排课ID
     */
    @NotNull(message = "排课ID不能为空")
    private Long scheduleId;

    /**
     * 教师ID
     */
    @NotNull(message = "教师ID不能为空")
    private Long teacherId;

    /**
     * 请假原因
     */
    @NotNull(message = "请假原因不能为空")
    private String remark;
}
