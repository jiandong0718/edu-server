package com.edu.teaching.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 课程包明细实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tch_course_package_item")
public class CoursePackageItem extends BaseEntity {

    /**
     * 课程包ID
     */
    private Long packageId;

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
