package com.edu.teaching.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * 教师考勤统计VO
 */
@Data
@Schema(description = "教师考勤统计VO")
public class TeacherAttendanceStatsVO {

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
     * 统计开始日期
     */
    @Schema(description = "统计开始日期")
    private LocalDate startDate;

    /**
     * 统计结束日期
     */
    @Schema(description = "统计结束日期")
    private LocalDate endDate;

    /**
     * 总课节数
     */
    @Schema(description = "总课节数")
    private Integer totalCount;

    /**
     * 出勤次数
     */
    @Schema(description = "出勤次数")
    private Integer presentCount;

    /**
     * 缺勤次数
     */
    @Schema(description = "缺勤次数")
    private Integer absentCount;

    /**
     * 迟到次数
     */
    @Schema(description = "迟到次数")
    private Integer lateCount;

    /**
     * 早退次数
     */
    @Schema(description = "早退次数")
    private Integer earlyLeaveCount;

    /**
     * 请假次数
     */
    @Schema(description = "请假次数")
    private Integer leaveCount;

    /**
     * 出勤率（百分比）
     */
    @Schema(description = "出勤率（百分比）")
    private Double attendanceRate;

    /**
     * 迟到率（百分比）
     */
    @Schema(description = "迟到率（百分比）")
    private Double lateRate;

    /**
     * 早退率（百分比）
     */
    @Schema(description = "早退率（百分比）")
    private Double earlyLeaveRate;

    /**
     * 总迟到分钟数
     */
    @Schema(description = "总迟到分钟数")
    private Integer totalLateMinutes;

    /**
     * 总早退分钟数
     */
    @Schema(description = "总早退分钟数")
    private Integer totalEarlyLeaveMinutes;
}
