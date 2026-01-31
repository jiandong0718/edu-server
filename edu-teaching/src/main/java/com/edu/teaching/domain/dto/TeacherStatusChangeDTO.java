package com.edu.teaching.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 教师状态变更DTO
 */
@Data
@Schema(description = "教师状态变更DTO")
public class TeacherStatusChangeDTO {

    @NotNull(message = "教师ID不能为空")
    @Schema(description = "教师ID")
    private Long teacherId;

    @NotBlank(message = "新状态不能为空")
    @Schema(description = "新状态：active-在职，on_leave-休假，resigned-离职")
    private String toStatus;

    @NotBlank(message = "变更原因不能为空")
    @Schema(description = "变更原因")
    private String reason;

    @Schema(description = "生效日期（不填则为当前日期）")
    private LocalDate effectiveDate;

    @Schema(description = "预计返回日期（休假时填写）")
    private LocalDate expectedReturnDate;

    @Schema(description = "备注")
    private String remark;
}
