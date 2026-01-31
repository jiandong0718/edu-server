package com.edu.teaching.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 批量签到DTO
 */
@Data
public class BatchAttendanceSignInDTO {

    /**
     * 排课ID
     */
    @NotNull(message = "排课ID不能为空")
    private Long scheduleId;

    /**
     * 学员ID列表
     */
    @NotEmpty(message = "学员ID列表不能为空")
    private List<Long> studentIds;

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
