package com.edu.teaching.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 教师考勤VO
 */
@Data
@Schema(description = "教师考勤VO")
public class TeacherAttendanceVO {

    /**
     * ID
     */
    @Schema(description = "ID")
    private Long id;

    /**
     * 排课ID
     */
    @Schema(description = "排课ID")
    private Long scheduleId;

    /**
     * 教师ID
     */
    @Schema(description = "教师ID")
    private Long teacherId;

    /**
     * 教师姓名
     */
    @Schema(description = "教师姓名")
    private String teacherName;

    /**
     * 教师编号
     */
    @Schema(description = "教师编号")
    private String teacherNo;

    /**
     * 班级ID
     */
    @Schema(description = "班级ID")
    private Long classId;

    /**
     * 班级名称
     */
    @Schema(description = "班级名称")
    private String className;

    /**
     * 课程名称
     */
    @Schema(description = "课程名称")
    private String courseName;

    /**
     * 上课日期
     */
    @Schema(description = "上课日期")
    private LocalDate scheduleDate;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private LocalTime startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private LocalTime endTime;

    /**
     * 签到时间
     */
    @Schema(description = "签到时间")
    private LocalDateTime signInTime;

    /**
     * 签退时间
     */
    @Schema(description = "签退时间")
    private LocalDateTime signOutTime;

    /**
     * 状态：present-出勤，absent-缺勤，late-迟到，early_leave-早退，leave-请假
     */
    @Schema(description = "状态：present-出勤，absent-缺勤，late-迟到，early_leave-早退，leave-请假")
    private String status;

    /**
     * 是否迟到
     */
    @Schema(description = "是否迟到")
    private Boolean isLate;

    /**
     * 是否早退
     */
    @Schema(description = "是否早退")
    private Boolean isEarlyLeave;

    /**
     * 迟到分钟数
     */
    @Schema(description = "迟到分钟数")
    private Integer lateMinutes;

    /**
     * 早退分钟数
     */
    @Schema(description = "早退分钟数")
    private Integer earlyLeaveMinutes;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
