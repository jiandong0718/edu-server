package com.edu.marketing.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 优惠券记录查询DTO
 */
@Data
public class CouponRecordQueryDTO {

    /**
     * 优惠券ID
     */
    private Long couponId;

    /**
     * 学员ID
     */
    private Long studentId;

    /**
     * 学员姓名
     */
    private String studentName;

    /**
     * 校区ID
     */
    private Long campusId;

    /**
     * 状态
     */
    private String status;

    /**
     * 领取开始时间
     */
    private LocalDateTime receiveStartTime;

    /**
     * 领取结束时间
     */
    private LocalDateTime receiveEndTime;

    /**
     * 使用开始时间
     */
    private LocalDateTime useStartTime;

    /**
     * 使用结束时间
     */
    private LocalDateTime useEndTime;

    /**
     * 页码
     */
    private Integer pageNum;

    /**
     * 每页大小
     */
    private Integer pageSize;
}
