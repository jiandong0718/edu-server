package com.edu.teaching.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tch_course")
public class Course extends BaseEntity {

    /**
     * 课程名称
     */
    private String name;

    /**
     * 课程编码
     */
    private String code;

    /**
     * 课程分类ID
     */
    private Long categoryId;

    /**
     * 课程类型：one_to_one-一对一，small_class-小班课，large_class-大班课
     */
    private String type;

    /**
     * 课程简介
     */
    private String description;

    /**
     * 课程封面
     */
    private String coverImage;

    /**
     * 单次课时长（分钟）
     */
    private Integer duration;

    /**
     * 总课时数
     */
    private Integer totalHours;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 售价
     */
    private BigDecimal price;

    /**
     * 单课时价格
     */
    private BigDecimal hourPrice;

    /**
     * 适合年龄段（最小）
     */
    private Integer minAge;

    /**
     * 适合年龄段（最大）
     */
    private Integer maxAge;

    /**
     * 状态：DRAFT-草稿，ON_SALE-在售，OFF_SALE-已下架
     */
    private String status;

    /**
     * 上架时间
     */
    private LocalDateTime onSaleTime;

    /**
     * 下架时间
     */
    private LocalDateTime offSaleTime;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 校区ID（null表示全部校区可用）
     */
    private Long campusId;
}
