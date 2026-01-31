package com.edu.teaching.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 班级结业请求DTO
 */
@Data
@Schema(description = "班级结业请求DTO")
public class ClassGraduationDTO {

    @NotNull(message = "班级ID不能为空")
    @Schema(description = "班级ID")
    private Long classId;

    @Schema(description = "结业日期（默认为当前日期）")
    private LocalDate graduationDate;

    @Schema(description = "是否生成结业证书", defaultValue = "false")
    private Boolean generateCertificate = false;

    @Schema(description = "结业评语")
    private String graduationComment;

    @Schema(description = "备注")
    private String remark;
}
