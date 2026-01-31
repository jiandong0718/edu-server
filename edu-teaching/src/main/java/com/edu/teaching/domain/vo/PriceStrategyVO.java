package com.edu.teaching.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 价格策略响应VO
 */
@Data
public class PriceStrategyVO {

    /**
     * 策略ID
     */
    private Long id;

    /**
     * 策略名称
     */
    private String strategyName;

    /**
     * 策略编码
     */
    private String strategyCode;

    /**
     * 策略描述
     */
    private String description;

    /**
     * 关联课程ID
     */
    private Long courseId;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 策略类型：TIERED-阶梯价格, MEMBER-会员价, PROMOTION-促销价, CUSTOM-自定义
     */
    private String strategyType;

    /**
     * 策略类型名称
     */
    private String strategyTypeName;

    /**
     * 优先级
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
     * 状态名称
     */
    private String statusName;

    /**
     * 校区ID
     */
    private Long campusId;

    /**
     * 校区名称
     */
    private String campusName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 策略规则列表
     */
    private List<PriceStrategyRuleVO> rules;
}
