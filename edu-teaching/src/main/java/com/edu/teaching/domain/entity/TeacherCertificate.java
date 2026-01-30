package com.edu.teaching.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 教师资质证书实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tch_teacher_certificate")
public class TeacherCertificate extends BaseEntity {

    /**
     * 教师ID
     */
    private Long teacherId;

    /**
     * 证书名称
     */
    private String certificateName;

    /**
     * 证书编号
     */
    private String certificateNo;

    /**
     * 颁发机构
     */
    private String issueOrg;

    /**
     * 颁发日期
     */
    private LocalDate issueDate;

    /**
     * 有效期至
     */
    private LocalDate expiryDate;

    /**
     * 证书图片URL
     */
    private String certificateUrl;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
