package com.edu.marketing.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 优惠券使用规则实体
 */
@Data
@TableName("mkt_coupon_rule")
public class CouponRule implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 优惠券ID
     */
    private Long couponId;

    /**
     * 规则类型：course-适用课程，student_tag-适用学员标签，contract_type-适用合同类型
     */
    private String ruleType;

    /**
     * 规则值（课程ID、标签ID、合同类型等，多个用逗号分隔）
     */
    private String ruleValue;

    /**
     * 规则名称（用于显示）
     */
    private String ruleName;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 创建人ID
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;
}
