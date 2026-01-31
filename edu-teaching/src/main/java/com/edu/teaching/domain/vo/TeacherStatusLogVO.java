package com.edu.teaching.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 教师状态变更记录VO
 */
@Data
@Schema(description = "教师状态变更记录VO")
public class TeacherStatusLogVO {

    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "教师ID")
    private Long teacherId;

    @Schema(description = "教师姓名")
    private String teacherName;

    @Schema(description = "教师编号")
    private String teacherNo;

    @Schema(description = "原状态代码")
    private String fromStatus;

    @Schema(description = "原状态名称")
    private String fromStatusName;

    @Schema(description = "新状态代码")
    private String toStatus;

    @Schema(description = "新状态名称")
    private String toStatusName;

    @Schema(description = "变更原因")
    private String reason;

    @Schema(description = "生效日期")
    private LocalDate effectiveDate;

    @Schema(description = "预计返回日期")
    private LocalDate expectedReturnDate;

    @Schema(description = "操作人ID")
    private Long operatorId;

    @Schema(description = "操作人姓名")
    private String operatorName;

    @Schema(description = "校区ID")
    private Long campusId;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
