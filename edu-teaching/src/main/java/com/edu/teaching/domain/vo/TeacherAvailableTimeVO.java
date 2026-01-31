package com.edu.teaching.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 教师可用时间VO
 */
@Data
@Schema(description = "教师可用时间VO")
public class TeacherAvailableTimeVO {

    @Schema(description = "可用时间ID")
    private Long id;

    @Schema(description = "教师ID")
    private Long teacherId;

    @Schema(description = "教师姓名")
    private String teacherName;

    @Schema(description = "星期几：1-7（1表示周一，7表示周日）")
    private Integer dayOfWeek;

    @Schema(description = "星期几名称")
    private String dayOfWeekName;

    @Schema(description = "开始时间（HH:mm）")
    private String startTime;

    @Schema(description = "结束时间（HH:mm）")
    private String endTime;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
