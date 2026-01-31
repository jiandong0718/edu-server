package com.edu.teaching.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 课程包实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tch_course_package")
public class CoursePackage extends BaseEntity {

    /**
     * 课程包名称
     */
    private String name;

    /**
     * 课程包编码
     */
    private String packageCode;

    /**
     * 课程包描述
     */
    private String description;

    /**
     * 封面图片URL
     */
    private String coverImage;

    /**
     * 课程包价格（优惠价）
     */
    private BigDecimal price;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 折扣（如8.5表示8.5折）
     */
    private BigDecimal discount;

    /**
     * 有效天数
     */
    private Integer validDays;

    /**
     * 总课时数
     */
    private Integer totalClassHours;

    /**
     * 状态：0-下架，1-上架
     */
    private Integer status;

    /**
     * 校区ID（null表示全部校区可用）
     */
    private Long campusId;

    /**
     * 排序
     */
    private Integer sortOrder;
}
