package com.edu.teaching.domain.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 教师考勤统计查询DTO
 */
@Data
public class TeacherAttendanceStatsQueryDTO {

    /**
     * 教师ID（可选）
     */
    private Long teacherId;

    /**
     * 班级ID（可选）
     */
    private Long classId;

    /**
     * 课程ID（可选）
     */
    private Long courseId;

    /**
     * 校区ID（可选）
     */
    private Long campusId;

    /**
     * 开始日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
}
