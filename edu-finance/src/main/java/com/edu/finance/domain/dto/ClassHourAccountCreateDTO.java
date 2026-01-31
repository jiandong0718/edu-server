package com.edu.finance.domain.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 课时账户创建DTO
 */
@Data
public class ClassHourAccountCreateDTO {

    /**
     * 学员ID
     */
    private Long studentId;

    /**
     * 合同ID
     */
    private Long contractId;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 校区ID
     */
    private Long campusId;

    /**
     * 总课时
     */
    private BigDecimal totalHours;

    /**
     * 赠送课时
     */
    private BigDecimal giftHours;
}
