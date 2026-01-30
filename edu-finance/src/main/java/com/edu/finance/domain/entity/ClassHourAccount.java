package com.edu.finance.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 课时账户实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fin_class_hour_account")
public class ClassHourAccount extends BaseEntity {

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
}
