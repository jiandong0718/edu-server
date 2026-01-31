package com.edu.system.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 教学数据看板VO
 */
@Data
@Schema(description = "教学数据看板")
public class TeachingDashboardVO {

    /**
     * 教学数据概览
     */
    @Data
    @Schema(description = "教学数据概览")
    public static class Overview {
        @Schema(description = "在读学员数")
        private Integer activeStudentCount;

        @Schema(description = "试听学员数")
        private Integer trialStudentCount;

        @Schema(description = "潜在学员数")
        private Integer potentialStudentCount;

        @Schema(description = "班级总数")
        private Integer totalClassCount;

        @Schema(description = "进行中班级数")
        private Integer ongoingClassCount;

        @Schema(description = "已满员班级数")
        private Integer fullClassCount;

        @Schema(description = "教师总数")
        private Integer totalTeacherCount;

        @Schema(description = "全职教师数")
        private Integer fullTimeTeacherCount;

        @Schema(description = "兼职教师数")
        private Integer partTimeTeacherCount;

        @Schema(description = "平均出勤率（百分比）")
        private BigDecimal averageAttendanceRate;

        @Schema(description = "平均完课率（百分比）")
        private BigDecimal averageCompletionRate;

        @Schema(description = "续费率（百分比）")
        private BigDecimal renewalRate;
    }

    /**
     * 考勤率趋势项
     */
    @Data
    @Schema(description = "考勤率趋势项")
    public static class AttendanceRateItem {
        @Schema(description = "日期", example = "2026-01-31")
        private String date;

        @Schema(description = "出勤率（百分比）", example = "85.5")
        private BigDecimal rate;

        @Schema(description = "应到人数")
        private Integer expectedCount;

        @Schema(description = "实到人数")
        private Integer actualCount;
    }

    /**
     * 班级统计项
     */
    @Data
    @Schema(description = "班级统计项")
    public static class ClassStatsItem {
        @Schema(description = "班级ID")
        private Long classId;

        @Schema(description = "班级名称")
        private String className;

        @Schema(description = "课程名称")
        private String courseName;

        @Schema(description = "班级状态", example = "ongoing")
        private String status;

        @Schema(description = "班级状态名称", example = "进行中")
        private String statusName;

        @Schema(description = "学员数量")
        private Integer studentCount;

        @Schema(description = "班级容量")
        private Integer capacity;

        @Schema(description = "出勤率（百分比）")
        private BigDecimal attendanceRate;

        @Schema(description = "已上课节数")
        private Integer completedLessons;

        @Schema(description = "总课节数")
        private Integer totalLessons;
    }

    /**
     * 教师统计项
     */
    @Data
    @Schema(description = "教师统计项")
    public static class TeacherStatsItem {
        @Schema(description = "教师ID")
        private Long teacherId;

        @Schema(description = "教师姓名")
        private String teacherName;

        @Schema(description = "教师类型", example = "full_time")
        private String teacherType;

        @Schema(description = "教师类型名称", example = "全职")
        private String teacherTypeName;

        @Schema(description = "负责班级数")
        private Integer classCount;

        @Schema(description = "本周课节数")
        private Integer weekScheduleCount;

        @Schema(description = "本月课节数")
        private Integer monthScheduleCount;

        @Schema(description = "学员数量")
        private Integer studentCount;

        @Schema(description = "平均出勤率（百分比）")
        private BigDecimal averageAttendanceRate;
    }

    /**
     * 课程消耗统计项
     */
    @Data
    @Schema(description = "课程消耗统计项")
    public static class CourseConsumptionItem {
        @Schema(description = "课程ID")
        private Long courseId;

        @Schema(description = "课程名称")
        private String courseName;

        @Schema(description = "总课时数")
        private Integer totalHours;

        @Schema(description = "已消耗课时数")
        private Integer consumedHours;

        @Schema(description = "剩余课时数")
        private Integer remainingHours;

        @Schema(description = "消耗率（百分比）")
        private BigDecimal consumptionRate;

        @Schema(description = "学员数量")
        private Integer studentCount;

        @Schema(description = "班级数量")
        private Integer classCount;
    }

    /**
     * 班级状态分布项
     */
    @Data
    @Schema(description = "班级状态分布项")
    public static class ClassStatusDistribution {
        @Schema(description = "状态", example = "ongoing")
        private String status;

        @Schema(description = "状态名称", example = "进行中")
        private String statusName;

        @Schema(description = "数量")
        private Integer count;

        @Schema(description = "占比（百分比）")
        private BigDecimal percentage;
    }
}
