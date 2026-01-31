package com.edu.teaching.domain.dto;

import lombok.Data;

/**
 * 价格策略查询DTO
 */
@Data
public class PriceStrategyQueryDTO {

    /**
     * 策略名称（模糊查询）
     */
    private String strategyName;

    /**
     * 策略编码
     */
    private String strategyCode;

    /**
     * 策略类型：TIERED-阶梯价格, MEMBER-会员价, PROMOTION-促销价, CUSTOM-自定义
     */
    private String strategyType;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 状态：ACTIVE-启用, INACTIVE-禁用
     */
    private String status;

    /**
     * 校区ID
     */
    private Long campusId;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;
}
