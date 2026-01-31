package com.edu.teaching.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
    private String strategyName;

    /**
     * 策略编码（唯一）
     */
    private String strategyCode;

    /**
     * 策略描述
     */
    private String description;

    /**
     * 关联课程ID（可为空表示通用策略）
     */
    private Long courseId;

    /**
     * 策略类型：TIERED-阶梯价格, MEMBER-会员价, PROMOTION-促销价, CUSTOM-自定义
     */
    private String strategyType;

    /**
     * 优先级（数字越大优先级越高）
     */
    private Integer priority;

    /**
     * 有效期开始日期
     */
    private LocalDate startDate;

    /**
     * 有效期结束日期
     */
    private LocalDate endDate;

    /**
     * 状态：ACTIVE-启用, INACTIVE-禁用
     */
    private String status;

    /**
     * 校区ID（null表示全部校区可用）
     */
    private Long campusId;
}
