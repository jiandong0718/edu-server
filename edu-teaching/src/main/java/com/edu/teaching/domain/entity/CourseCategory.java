package com.edu.teaching.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 课程分类实体
 *
 * @author edu
 * @since 2024-01-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tch_course_category")
public class CourseCategory extends BaseEntity {

    /**
     * 分类名称
     */
    private String name;

    /**
     * 父分类ID（0表示顶级分类）
     */
    private Long parentId;

    /**
     * 排序号
     */
    private Integer sortOrder;

    /**
     * 图标
     */
    private String icon;

    /**
     * 描述
     */
    private String description;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 校区ID（null表示全部校区可用）
     */
    private Long campusId;

    /**
     * 子分类列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<CourseCategory> children;
}
