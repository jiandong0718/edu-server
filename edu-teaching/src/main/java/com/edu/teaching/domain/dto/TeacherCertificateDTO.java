package com.edu.teaching.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 教师资质证书DTO
 */
@Data
@Schema(description = "教师资质证书DTO")
public class TeacherCertificateDTO {

    @Schema(description = "证书ID（修改时必填）")
    private Long id;

    @NotNull(message = "教师ID不能为空")
    @Schema(description = "教师ID")
    private Long teacherId;

    @NotBlank(message = "证书名称不能为空")
    @Schema(description = "证书名称")
    private String certName;

    @Schema(description = "证书编号")
    private String certNo;

    @NotBlank(message = "证书类型不能为空")
    @Schema(description = "证书类型：teacher_qualification-教师资格证，degree-学历证书，skill-技能证书，other-其他")
    private String certType;

    @Schema(description = "颁发机构")
    private String issueOrg;

    @Schema(description = "颁发日期")
    private LocalDate issueDate;

    @Schema(description = "有效期至（永久有效可为空）")
    private LocalDate expireDate;

    @NotBlank(message = "证书文件URL不能为空")
    @Schema(description = "证书文件URL")
    private String fileUrl;

    @Schema(description = "状态：0-禁用，1-启用", defaultValue = "1")
    private Integer status;

    @Schema(description = "校区ID")
    private Long campusId;

    @Schema(description = "备注")
    private String remark;
}
