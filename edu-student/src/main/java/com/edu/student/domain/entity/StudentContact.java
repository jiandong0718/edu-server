package com.edu.student.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 学员联系人实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("stu_contact")
public class StudentContact extends BaseEntity {

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
