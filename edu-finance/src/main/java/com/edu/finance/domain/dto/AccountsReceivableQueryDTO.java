package com.edu.finance.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 应收账款查询DTO
 */
@Data
@Schema(description = "应收账款查询DTO")
public class AccountsReceivableQueryDTO {

    /**
     * 校区ID
     */
    @Schema(description = "校区ID")
    private Long campusId;

    /**
     * 学员ID
     */
    @Schema(description = "学员ID")
    private Long studentId;

    /**
     * 学员姓名（模糊查询）
     */
    @Schema(description = "学员姓名")
    private String studentName;

    /**
     * 学员手机号
     */
    @Schema(description = "学员手机号")
    private String studentPhone;

    /**
     * 合同编号（模糊查询）
     */
    @Schema(description = "合同编号")
    private String contractNo;

    /**
     * 最小欠费金额
     */
    @Schema(description = "最小欠费金额")
    private BigDecimal minArrearsAmount;

    /**
     * 最大欠费金额
     */
    @Schema(description = "最大欠费金额")
    private BigDecimal maxArrearsAmount;

    /**
     * 账龄范围：30-30天内，30_60-30-60天，60_90-60-90天，90_plus-90天以上
     */
    @Schema(description = "账龄范围", example = "30")
    private String agingRange;

    /**
     * 开始日期
     */
    @Schema(description = "开始日期")
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @Schema(description = "结束日期")
    private LocalDate endDate;

    /**
     * 页码
     */
    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;
}
