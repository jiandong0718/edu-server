package com.edu.teaching.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 教师签到DTO
 */
@Data
public class TeacherSignInDTO {

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
     * 备注
     */
    private String remark;
}
