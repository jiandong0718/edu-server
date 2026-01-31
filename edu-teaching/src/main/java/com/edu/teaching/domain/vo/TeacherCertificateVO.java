package com.edu.teaching.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 教师资质证书VO
 */
@Data
@Schema(description = "教师资质证书VO")
public class TeacherCertificateVO {

    @Schema(description = "证书ID")
    private Long id;

    @Schema(description = "教师ID")
    private Long teacherId;

    @Schema(description = "教师姓名")
    private String teacherName;

    @Schema(description = "证书名称")
    private String certName;

    @Schema(description = "证书编号")
    private String certNo;

    @Schema(description = "证书类型")
    private String certType;

    @Schema(description = "证书类型名称")
    private String certTypeName;

    @Schema(description = "颁发机构")
    private String issueOrg;

    @Schema(description = "颁发日期")
    private LocalDate issueDate;

    @Schema(description = "有效期至")
    private LocalDate expireDate;

    @Schema(description = "证书文件URL")
    private String fileUrl;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;

    @Schema(description = "是否已过期")
    private Boolean expired;

    @Schema(description = "校区ID")
    private Long campusId;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
