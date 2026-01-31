package com.edu.teaching.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 补课记录VO
 */
@Data
@Schema(description = "补课记录VO")
public class MakeupLessonVO {

    @Schema(description = "补课记录ID")
    private Long id;

    @Schema(description = "请假申请ID")
    private Long leaveRequestId;

    @Schema(description = "请假单号")
    private String leaveNo;

    @Schema(description = "原排课ID")
    private Long originalScheduleId;

    @Schema(description = "原课程日期")
    private String originalScheduleDate;

    @Schema(description = "原课程教师")
    private String originalTeacherName;

    @Schema(description = "补课排课ID")
    private Long makeupScheduleId;

    @Schema(description = "补课日期")
    private String makeupScheduleDate;

    @Schema(description = "补课教师")
    private String makeupTeacherName;

    @Schema(description = "学员ID")
    private Long studentId;

    @Schema(description = "学员姓名")
    private String studentName;

    @Schema(description = "学员编号")
    private String studentNo;

    @Schema(description = "校区ID")
    private Long campusId;

    @Schema(description = "状态：pending-待补课，completed-已完成，cancelled-已取消")
    private String status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
