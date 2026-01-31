package com.edu.teaching.domain.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 课程包创建/更新DTO
 */
@Data
public class CoursePackageDTO {

    /**
     * 课程包ID（更新时需要）
     */
    private Long id;

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
     * 校区ID
     */
    private Long campusId;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 课程包明细列表
     */
    private List<CoursePackageItemDTO> items;

    @Data
    public static class CoursePackageItemDTO {
        /**
         * 课程ID
         */
        private Long courseId;

        /**
         * 课程数量（课时数）
         */
        private Integer courseCount;

        /**
         * 排序
         */
        private Integer sortOrder;
    }
}
