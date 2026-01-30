package com.edu.student.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 学员标签实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("stu_tag")
public class StudentTag extends BaseEntity {

    /**
     * 标签名称
     */
    private String name;

    /**
     * 标签颜色
     */
    private String color;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 校区ID（null表示全局标签）
     */
    private Long campusId;
}
