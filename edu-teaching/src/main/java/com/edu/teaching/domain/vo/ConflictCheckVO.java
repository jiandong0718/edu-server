package com.edu.teaching.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 冲突检测结果VO
 */
@Data
@Schema(description = "冲突检测结果VO")
public class ConflictCheckVO {

    @Schema(description = "是否有冲突")
    private Boolean hasConflict;

    @Schema(description = "冲突类型：teacher-教师冲突，classroom-教室冲突")
    private String conflictType;

    @Schema(description = "冲突的排课ID")
    private Long conflictScheduleId;

    @Schema(description = "冲突的课程名称")
    private String conflictCourseName;

    @Schema(description = "冲突的班级名称")
    private String conflictClassName;

    @Schema(description = "冲突的日期")
    private LocalDate conflictDate;

    @Schema(description = "冲突的开始时间")
    private LocalTime conflictStartTime;

    @Schema(description = "冲突的结束时间")
    private LocalTime conflictEndTime;

    @Schema(description = "冲突描述")
    private String conflictMessage;

    public static ConflictCheckVO noConflict() {
        ConflictCheckVO vo = new ConflictCheckVO();
        vo.setHasConflict(false);
        return vo;
    }

    public static ConflictCheckVO conflict(String type, String message) {
        ConflictCheckVO vo = new ConflictCheckVO();
        vo.setHasConflict(true);
        vo.setConflictType(type);
        vo.setConflictMessage(message);
        return vo;
    }
}
