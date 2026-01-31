package com.edu.teaching.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 价格策略创建/更新DTO
 */
@Data
public class PriceStrategyDTO {

    /**
     * 策略ID（更新时需要）
     */
    private Long id;

    /**
     * 策略名称
     */
    @NotBlank(message = "策略名称不能为空")
    private String strategyName;

    /**
     * 策略编码（唯一）
     */
    @NotBlank(message = "策略编码不能为空")
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
    @NotBlank(message = "策略类型不能为空")
    private String strategyType;

    /**
     * 优先级（数字越大优先级越高）
     */
    @NotNull(message = "优先级不能为空")
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

    /**
     * 策略规则列表
     */
    @NotNull(message = "策略规则不能为空")
    private List<PriceStrategyRuleDTO> rules;
}
