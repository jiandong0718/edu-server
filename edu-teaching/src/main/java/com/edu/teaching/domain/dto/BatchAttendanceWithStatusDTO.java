package com.edu.teaching.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 批量签到（带状态）DTO
 * 支持为不同学员设置不同的考勤状态
 */
@Data
public class BatchAttendanceWithStatusDTO {

    /**
     * 排课ID
     */
    @NotNull(message = "排课ID不能为空")
    private Long scheduleId;

    /**
     * 学员考勤状态映射
     * key: 学员ID, value: 考勤状态（present-出勤，absent-缺勤，late-迟到，leave-请假）
     */
    @NotEmpty(message = "学员考勤状态不能为空")
    private Map<Long, String> studentStatusMap;

    /**
     * 备注
     */
    private String remark;
}
