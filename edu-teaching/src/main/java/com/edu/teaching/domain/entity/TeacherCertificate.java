package com.edu.teaching.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
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
    private String certName;

    /**
     * 证书编号
     */
    private String certNo;

    /**
     * 证书类型：teacher_qualification-教师资格证，degree-学历证书，skill-技能证书，other-其他
     */
    private String certType;

    /**
     * 颁发机构
     */
    private String issueOrg;

    /**
     * 颁发日期
     */
    private LocalDate issueDate;

    /**
     * 有效期至（永久有效可为空）
     */
    private LocalDate expireDate;

    /**
     * 证书文件URL
     */
    private String fileUrl;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 校区ID（用于数据隔离）
     */
    private Long campusId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否已过期（非数据库字段）
     */
    @TableField(exist = false)
    private Boolean expired;
}
