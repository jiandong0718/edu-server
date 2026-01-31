package com.edu.finance.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课时记录VO
 */
@Data
@Schema(description = "课时记录VO")
public class ClassHourRecordVO {

    /**
     * 记录ID
     */
    @Schema(description = "记录ID")
    private Long id;

    /**
     * 账户ID
     */
    @Schema(description = "账户ID")
    private Long accountId;

    /**
     * 学员ID
     */
    @Schema(description = "学员ID")
    private Long studentId;

    /**
     * 学员姓名
     */
    @Schema(description = "学员姓名")
    private String studentName;

    /**
     * 课程名称
     */
    @Schema(description = "课程名称")
    private String courseName;

    /**
     * 排课ID
     */
    @Schema(description = "排课ID")
    private Long scheduleId;

    /**
     * 类型：consume-消耗，gift-赠送，adjust-调整，refund-退费，revoke-撤销
     */
    @Schema(description = "类型")
    private String type;

    /**
     * 类型描述
     */
    @Schema(description = "类型描述")
    private String typeDesc;

    /**
     * 课时数（正数增加，负数减少）
     */
    @Schema(description = "课时数")
    private BigDecimal hours;

    /**
     * 变动后余额
     */
    @Schema(description = "变动后余额")
    private BigDecimal balance;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

    /**
     * 操作人ID
     */
    @Schema(description = "操作人ID")
    private Long createBy;

    /**
     * 操作人姓名
     */
    @Schema(description = "操作人姓名")
    private String createByName;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
