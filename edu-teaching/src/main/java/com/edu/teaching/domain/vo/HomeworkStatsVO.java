package com.edu.teaching.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 作业统计VO
 */
@Data
@Schema(description = "作业统计VO")
public class HomeworkStatsVO {

    /**
     * 作业ID
     */
    @Schema(description = "作业ID")
    private Long homeworkId;

    /**
     * 作业标题
     */
    @Schema(description = "作业标题")
    private String homeworkTitle;

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
     * 班级学员总数
     */
    @Schema(description = "班级学员总数")
    private Integer totalStudents;

    /**
     * 已提交数量
     */
    @Schema(description = "已提交数量")
    private Integer submittedCount;

    /**
     * 待批改数量
     */
    @Schema(description = "待批改数量")
    private Integer pendingCount;

    /**
     * 已批改数量
     */
    @Schema(description = "已批改数量")
    private Integer reviewedCount;

    /**
     * 已退回数量
     */
    @Schema(description = "已退回数量")
    private Integer returnedCount;

    /**
     * 未提交数量
     */
    @Schema(description = "未提交数量")
    private Integer notSubmittedCount;

    /**
     * 提交率（百分比）
     */
    @Schema(description = "提交率（百分比）")
    private Double submitRate;

    /**
     * 平均分
     */
    @Schema(description = "平均分")
    private Double averageScore;

    /**
     * 最高分
     */
    @Schema(description = "最高分")
    private Integer maxScore;

    /**
     * 最低分
     */
    @Schema(description = "最低分")
    private Integer minScore;

    /**
     * 优秀率（A等级，百分比）
     */
    @Schema(description = "优秀率（A等级，百分比）")
    private Double excellentRate;

    /**
     * 及格率（C等级及以上，百分比）
     */
    @Schema(description = "及格率（C等级及以上，百分比）")
    private Double passRate;
}
