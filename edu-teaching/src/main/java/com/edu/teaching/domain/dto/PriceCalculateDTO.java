package com.edu.teaching.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 价格计算请求DTO
 */
@Data
public class PriceCalculateDTO {

    /**
     * 课程ID（可选）
     */
    private Long courseId;

    /**
     * 原价
     */
    @NotNull(message = "原价不能为空")
    private BigDecimal originalPrice;

    /**
     * 课时数（用于阶梯价格计算）
     */
    private Integer classHours;

    /**
     * 会员等级（用于会员价计算）：NORMAL-普通, SILVER-银卡, GOLD-金卡, DIAMOND-钻石
     */
    private String memberLevel;

    /**
     * 购买金额（用于金额条件判断）
     */
    private BigDecimal amount;

    /**
     * 校区ID
     */
    private Long campusId;
}
