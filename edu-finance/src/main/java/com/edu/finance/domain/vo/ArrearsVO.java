package com.edu.finance.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 欠费记录VO
 */
@Data
@Schema(description = "欠费记录VO")
public class ArrearsVO {

    /**
     * 合同ID
     */
    @Schema(description = "合同ID")
    private Long contractId;

    /**
     * 合同编号
     */
    @Schema(description = "合同编号")
    private String contractNo;

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
     * 学员手机号
     */
    @Schema(description = "学员手机号")
    private String studentPhone;

    /**
     * 校区ID
     */
    @Schema(description = "校区ID")
    private Long campusId;

    /**
     * 校区名称
     */
    @Schema(description = "校区名称")
    private String campusName;

    /**
     * 合同金额
     */
    @Schema(description = "合同金额")
    private BigDecimal contractAmount;

    /**
     * 实付金额（应收金额）
     */
    @Schema(description = "实付金额")
    private BigDecimal paidAmount;

    /**
     * 已收金额
     */
    @Schema(description = "已收金额")
    private BigDecimal receivedAmount;

    /**
     * 欠费金额
     */
    @Schema(description = "欠费金额")
    private BigDecimal arrearsAmount;

    /**
     * 签约日期
     */
    @Schema(description = "签约日期")
    private LocalDate signDate;

    /**
     * 生效日期
     */
    @Schema(description = "生效日期")
    private LocalDate effectiveDate;

    /**
     * 欠费天数
     */
    @Schema(description = "欠费天数")
    private Integer arrearsDays;

    /**
     * 合同状态
     */
    @Schema(description = "合同状态")
    private String contractStatus;

    /**
     * 合同状态描述
     */
    @Schema(description = "合同状态描述")
    private String contractStatusDesc;

    /**
     * 销售顾问ID
     */
    @Schema(description = "销售顾问ID")
    private Long salesId;

    /**
     * 销售顾问姓名
     */
    @Schema(description = "销售顾问姓名")
    private String salesName;

    /**
     * 最后收款时间
     */
    @Schema(description = "最后收款时间")
    private LocalDateTime lastPaymentTime;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
