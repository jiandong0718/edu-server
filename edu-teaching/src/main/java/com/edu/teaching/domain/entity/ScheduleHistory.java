package com.edu.teaching.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 调课历史记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tch_schedule_history")
public class ScheduleHistory extends BaseEntity {

    /**
     * 排课ID
     */
    private Long scheduleId;

    /**
     * 操作类型：reschedule-调课，substitute-代课，cancel-取消
     */
    private String operationType;

    /**
     * 原上课日期
     */
    private LocalDate oldScheduleDate;

    /**
     * 新上课日期
     */
    private LocalDate newScheduleDate;

    /**
     * 原开始时间
     */
    private LocalTime oldStartTime;

    /**
     * 新开始时间
     */
    private LocalTime newStartTime;

    /**
     * 原结束时间
     */
    private LocalTime oldEndTime;

    /**
     * 新结束时间
     */
    private LocalTime newEndTime;

    /**
     * 原教师ID
     */
    private Long oldTeacherId;

    /**
     * 新教师ID
     */
    private Long newTeacherId;

    /**
     * 原教室ID
     */
    private Long oldClassroomId;

    /**
     * 新教室ID
     */
    private Long newClassroomId;

    /**
     * 调课原因
     */
    private String reason;

    /**
     * 备注
     */
    private String remark;
}
