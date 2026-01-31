package com.edu.marketing.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 优惠券查询DTO
 */
@Data
public class CouponQueryDTO {

    /**
     * 优惠券名称
     */
    private String name;

    /**
     * 优惠券类型
     */
    private String type;

    /**
     * 状态
     */
    private String status;

    /**
     * 校区ID
     */
    private Long campusId;

    /**
     * 创建开始时间
     */
    private LocalDateTime createStartTime;

    /**
     * 创建结束时间
     */
    private LocalDateTime createEndTime;

    /**
     * 页码
     */
    private Integer pageNum;

    /**
     * 每页大小
     */
    private Integer pageSize;
}
