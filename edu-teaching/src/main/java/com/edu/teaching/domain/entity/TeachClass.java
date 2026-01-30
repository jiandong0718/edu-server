package com.edu.teaching.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 班级实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tch_class")
public class TeachClass extends BaseEntity {

    /**
     * 班级名称
     */
    private String name;

    /**
     * 班级编码
     */
    private String code;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 主讲教师ID
     */
    private Long teacherId;

    /**
     * 助教ID
     */
    private Long assistantId;

    /**
     * 教室ID
     */
    private Long classroomId;

    /**
     * 校区ID
     */
    private Long campusId;

    /**
     * 班级容量
     */
    private Integer capacity;

    /**
     * 当前人数
     */
    private Integer currentCount;

    /**
     * 开班日期
     */
    private LocalDate startDate;

    /**
     * 结班日期
     */
    private LocalDate endDate;

    /**
     * 状态：pending-待开班，ongoing-进行中，finished-已结班，cancelled-已取消
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 课程名称（非数据库字段）
     */
    @TableField(exist = false)
    private String courseName;

    /**
     * 教师姓名（非数据库字段）
     */
    @TableField(exist = false)
    private String teacherName;

    /**
     * 校区名称（非数据库字段）
     */
    @TableField(exist = false)
    private String campusName;
}
