package com.edu.student.api.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 学员联系人DTO（供其他模块调用）
 */
@Data
public class StudentContactDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 联系人ID
     */
    private Long id;

    /**
     * 学员ID
     */
    private Long studentId;

    /**
     * 联系人姓名
     */
    private String name;

    /**
     * 关系：father-父亲，mother-母亲，grandpa-爷爷，grandma-奶奶，other-其他
     */
    private String relation;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 是否主要联系人
     */
    private Boolean isPrimary;

    /**
     * 是否接收通知
     */
    private Boolean receiveNotify;

    /**
     * 备注
     */
    private String remark;
}
