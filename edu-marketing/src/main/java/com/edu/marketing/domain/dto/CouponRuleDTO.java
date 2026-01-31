package com.edu.marketing.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * 优惠券规则DTO
 */
@Data
public class CouponRuleDTO {

    /**
     * 规则类型：course-适用课程，student_tag-适用学员标签，contract_type-适用合同类型
     */
    private String ruleType;

    /**
     * 规则值列表（课程ID、标签ID、合同类型等）
     */
    private List<String> ruleValues;

    /**
     * 规则名称（用于显示）
     */
    private String ruleName;
}
