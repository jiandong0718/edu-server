package com.edu.marketing.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * 优惠券发放DTO
 */
@Data
public class CouponIssueDTO {

    /**
     * 优惠券ID
     */
    private Long couponId;

    /**
     * 学员ID列表
     */
    private List<Long> studentIds;

    /**
     * 发放方式：manual-手动发放，system-系统赠送
     */
    private String issueType;

    /**
     * 备注
     */
    private String remark;
}
