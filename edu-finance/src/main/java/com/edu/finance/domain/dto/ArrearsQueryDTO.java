package com.edu.finance.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 欠费查询DTO
 */
@Data
@Schema(description = "欠费查询DTO")
public class ArrearsQueryDTO {

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
     * 合同ID
     */
    @Schema(description = "合同ID")
    private Long contractId;

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
     * 最小欠费天数
     */
    @Schema(description = "最小欠费天数")
    private Integer minArrearsDays;

    /**
     * 最大欠费天数
     */
    @Schema(description = "最大欠费天数")
    private Integer maxArrearsDays;

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
