package com.edu.teaching.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 排课冲突检测结果VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "排课冲突检测结果")
public class ScheduleConflictVO {

    @Schema(description = "是否有冲突")
    private Boolean hasConflict;

    @Schema(description = "教师冲突列表")
    private List<ConflictDetail> teacherConflicts;

    @Schema(description = "教室冲突列表")
    private List<ConflictDetail> classroomConflicts;

    @Schema(description = "学员冲突列表")
    private List<StudentConflictDetail> studentConflicts;

    @Schema(description = "教师可用时间冲突")
    private TeacherAvailabilityConflict teacherAvailabilityConflict;

    @Schema(description = "教室状态冲突")
    private ClassroomStatusConflict classroomStatusConflict;

    /**
     * 冲突详情
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "冲突详情")
    public static class ConflictDetail {
        @Schema(description = "冲突的排课ID")
        private Long scheduleId;

        @Schema(description = "班级ID")
        private Long classId;

        @Schema(description = "班级名称")
        private String className;

        @Schema(description = "课程名称")
        private String courseName;

        @Schema(description = "上课日期")
        private LocalDate scheduleDate;

        @Schema(description = "开始时间")
        private LocalTime startTime;

        @Schema(description = "结束时间")
        private LocalTime endTime;

        @Schema(description = "教师ID")
        private Long teacherId;

        @Schema(description = "教师姓名")
        private String teacherName;

        @Schema(description = "教室ID")
        private Long classroomId;

        @Schema(description = "教室名称")
        private String classroomName;
    }

    /**
     * 学员冲突详情
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "学员冲突详情")
    public static class StudentConflictDetail {
        @Schema(description = "学员ID")
        private Long studentId;

        @Schema(description = "学员姓名")
        private String studentName;

        @Schema(description = "学员编号")
        private String studentNo;

        @Schema(description = "冲突的排课信息")
        private ConflictDetail conflictSchedule;
    }

    /**
     * 教师可用时间冲突
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "教师可用时间冲突")
    public static class TeacherAvailabilityConflict {
        @Schema(description = "是否有冲突")
        private Boolean hasConflict;

        @Schema(description = "教师ID")
        private Long teacherId;

        @Schema(description = "教师姓名")
        private String teacherName;

        @Schema(description = "冲突原因")
        private String reason;
    }

    /**
     * 教室状态冲突
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "教室状态冲突")
    public static class ClassroomStatusConflict {
        @Schema(description = "是否有冲突")
        private Boolean hasConflict;

        @Schema(description = "教室ID")
        private Long classroomId;

        @Schema(description = "教室名称")
        private String classroomName;

        @Schema(description = "冲突原因")
        private String reason;
    }
}
