package com.edu.finance.domain.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 课时扣减DTO
 */
@Data
public class ClassHourDeductDTO {

    /**
     * 学员ID
     */
    private Long studentId;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 排课ID
     */
    private Long scheduleId;

    /**
     * 扣减课时数
     */
    private BigDecimal hours;

    /**
     * 备注
     */
    private String remark;
}
