package com.edu.teaching.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 班级学员关联实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tch_class_student")
public class ClassStudent extends BaseEntity {

    /**
     * 班级ID
     */
    private Long classId;

    /**
     * 学员ID
     */
    private Long studentId;

    /**
     * 加入日期
     */
    private LocalDate joinDate;

    /**
     * 退出日期
     */
    private LocalDate leaveDate;

    /**
     * 状态：active-在读，left-已退出，graduated-已结业
     */
    private String status;
}
