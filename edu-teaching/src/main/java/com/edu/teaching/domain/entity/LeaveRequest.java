package com.edu.teaching.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 请假申请实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tch_leave_request")
public class LeaveRequest extends BaseEntity {

    /**
     * 请假单号
     */
    private String leaveNo;

    /**
     * 学员ID
     */
    private Long studentId;

    /**
     * 排课ID（针对特定课节请假）
     */
    private Long scheduleId;

    /**
     * 班级ID
     */
    private Long classId;

    /**
     * 校区ID
     */
    private Long campusId;

    /**
     * 请假类型：single-单次请假，period-时段请假
     */
    private String type;

    /**
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;

    /**
     * 请假原因
     */
    private String reason;

    /**
     * 状态：pending-待审批，approved-已批准，rejected-已拒绝，cancelled-已取消
     */
    private String status;

    /**
     * 审批人ID
     */
    private Long approverId;

    /**
     * 审批时间
     */
    private LocalDateTime approveTime;

    /**
     * 审批意见
     */
    private String approveRemark;

    /**
     * 是否需要补课
     */
    private Integer needMakeup;

    /**
     * 补课排课ID
     */
    private Long makeupScheduleId;

    /**
     * 备注
     */
    private String remark;

    // ========== 非数据库字段 ==========

    /**
     * 学员姓名
     */
    @TableField(exist = false)
    private String studentName;

    /**
     * 学员编号
     */
    @TableField(exist = false)
    private String studentNo;

    /**
     * 班级名称
     */
    @TableField(exist = false)
    private String className;

    /**
     * 审批人姓名
     */
    @TableField(exist = false)
    private String approverName;
}
