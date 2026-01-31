package com.edu.teaching.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 价格策略实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tch_price_strategy")
public class PriceStrategy extends BaseEntity {

    /**
     * 策略名称
     */
    private String name;

    /**
     * 策略类型：time_period-时间段，student_type-学员类型
     */
    private String type;

    /**
     * 目标ID（课程ID或课程包ID）
     */
    private Long targetId;

    /**
     * 目标类型：course-课程，package-课程包
     */
    private String targetType;

    /**
     * 折扣类型：percentage-百分比，fixed-固定金额
     */
    private String discountType;

    /**
     * 折扣值（百分比：0-100，固定金额：具体金额）
     */
    private BigDecimal discountValue;

    /**
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;

    /**
     * 学员类型：new-新生，old-老生（仅当type为student_type时有效）
     */
    private String studentType;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 校区ID（null表示全部校区可用）
     */
    private Long campusId;
}
