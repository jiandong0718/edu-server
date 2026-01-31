package com.edu.teaching.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 班级结业结果VO
 */
@Data
@Schema(description = "班级结业结果VO")
public class ClassGraduationResultVO {

    @Schema(description = "班级ID")
    private Long classId;

    @Schema(description = "班级名称")
    private String className;

    @Schema(description = "结业日期")
    private LocalDate graduationDate;

    @Schema(description = "结业学员数量")
    private Integer graduatedStudentCount;

    @Schema(description = "结业学员ID列表")
    private List<Long> graduatedStudentIds;

    @Schema(description = "总课时数")
    private Integer totalLessons;

    @Schema(description = "已完成课时数")
    private Integer completedLessons;

    @Schema(description = "出勤率（百分比）")
    private Double attendanceRate;

    @Schema(description = "是否生成了结业证书")
    private Boolean certificateGenerated;

    @Schema(description = "结业证书ID列表")
    private List<Long> certificateIds;

    @Schema(description = "结业时间")
    private LocalDateTime graduationTime;

    @Schema(description = "是否成功")
    private Boolean success;

    @Schema(description = "失败原因")
    private String failureReason;
}
