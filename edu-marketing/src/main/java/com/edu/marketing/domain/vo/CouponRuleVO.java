package com.edu.marketing.domain.vo;

import lombok.Data;

/**
 * 优惠券规则VO
 */
@Data
public class CouponRuleVO {

    /**
     * 规则ID
     */
    private Long id;

    /**
     * 规则类型
     */
    private String ruleType;

    /**
     * 规则值
     */
    private String ruleValue;

    /**
     * 规则名称
     */
    private String ruleName;
}
