package com.edu.teaching.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 请假申请VO
 */
@Data
@Schema(description = "请假申请VO")
public class LeaveRequestVO {

    @Schema(description = "请假申请ID")
    private Long id;

    @Schema(description = "请假单号")
    private String leaveNo;

    @Schema(description = "学员ID")
    private Long studentId;

    @Schema(description = "学员姓名")
    private String studentName;

    @Schema(description = "学员编号")
    private String studentNo;

    @Schema(description = "排课ID")
    private Long scheduleId;

    @Schema(description = "班级ID")
    private Long classId;

    @Schema(description = "班级名称")
    private String className;

    @Schema(description = "校区ID")
    private Long campusId;

    @Schema(description = "请假类型：single-单次请假，period-时段请假")
    private String type;

    @Schema(description = "请假类型名称")
    private String typeName;

    @Schema(description = "开始日期")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    private LocalDate endDate;

    @Schema(description = "请假原因")
    private String reason;

    @Schema(description = "状态：pending-待审批，approved-已批准，rejected-已拒绝，cancelled-已取消")
    private String status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "审批人ID")
    private Long approverId;

    @Schema(description = "审批人姓名")
    private String approverName;

    @Schema(description = "审批时间")
    private LocalDateTime approveTime;

    @Schema(description = "审批意见")
    private String approveRemark;

    @Schema(description = "是否需要补课：0-不需要，1-需要")
    private Integer needMakeup;

    @Schema(description = "补课排课ID")
    private Long makeupScheduleId;

    @Schema(description = "是否已安排补课")
    private Boolean makeupArranged;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
