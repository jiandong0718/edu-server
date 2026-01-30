package com.edu.teaching.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 教师可用时间配置实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tch_teacher_available_time")
public class TeacherAvailableTime extends BaseEntity {

    /**
     * 教师ID
     */
    private Long teacherId;

    /**
     * 星期几：1-7（周一到周日）
     */
    private Integer dayOfWeek;

    /**
     * 开始时间（HH:mm）
     */
    private String startTime;

    /**
     * 结束时间（HH:mm）
     */
    private String endTime;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
