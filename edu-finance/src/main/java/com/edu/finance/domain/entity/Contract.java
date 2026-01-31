package com.edu.finance.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 合同实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fin_contract")
public class Contract extends BaseEntity {

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 学员ID
     */
    private Long studentId;

    /**
     * 校区ID
     */
    private Long campusId;

    /**
     * 合同类型：new-新签，renew-续费，upgrade-升级
     */
    private String type;

    /**
     * 合同金额
     */
    private BigDecimal amount;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 使用的优惠券记录ID
     */
    private Long couponRecordId;

    /**
     * 优惠券优惠金额
     */
    private BigDecimal couponDiscountAmount;

    /**
     * 实付金额
     */
    private BigDecimal paidAmount;

    /**
     * 已付金额
     */
    private BigDecimal receivedAmount;

    /**
     * 总课时数
     */
    private Integer totalHours;

    /**
     * 签约日期
     */
    private LocalDate signDate;

    /**
     * 生效日期
     */
    private LocalDate effectiveDate;

    /**
     * 到期日期
     */
    private LocalDate expireDate;

    /**
     * 状态：pending-待签署，signed-已签署，completed-已完成，refunded-已退费，cancelled-已作废
     */
    private String status;

    /**
     * 销售顾问ID
     */
    private Long salesId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 学员姓名（非数据库字段）
     */
    @TableField(exist = false)
    private String studentName;

    /**
     * 校区名称（非数据库字段）
     */
    @TableField(exist = false)
    private String campusName;

    /**
     * 销售姓名（非数据库字段）
     */
    @TableField(exist = false)
    private String salesName;
}
