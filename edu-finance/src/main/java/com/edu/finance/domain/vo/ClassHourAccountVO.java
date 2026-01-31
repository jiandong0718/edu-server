package com.edu.finance.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课时账户VO
 */
@Data
public class ClassHourAccountVO {

    /**
     * ID
     */
    private Long id;

    /**
     * 学员ID
     */
    private Long studentId;

    /**
     * 学员姓名
     */
    private String studentName;

    /**
     * 合同ID
     */
    private Long contractId;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 校区ID
     */
    private Long campusId;

    /**
     * 校区名称
     */
    private String campusName;

    /**
     * 总课时
     */
    private BigDecimal totalHours;

    /**
     * 已消耗课时
     */
    private BigDecimal usedHours;

    /**
     * 剩余课时
     */
    private BigDecimal remainingHours;

    /**
     * 赠送课时
     */
    private BigDecimal giftHours;

    /**
     * 状态：active-正常，frozen-冻结，exhausted-已用完
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
