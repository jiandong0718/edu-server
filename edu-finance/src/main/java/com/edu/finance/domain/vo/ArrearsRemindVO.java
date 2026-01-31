package com.edu.finance.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 欠费提醒VO
 */
@Data
@Schema(description = "欠费提醒VO")
public class ArrearsRemindVO {

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
     * 欠费金额
     */
    @Schema(description = "欠费金额")
    private BigDecimal arrearsAmount;

    /**
     * 欠费天数
     */
    @Schema(description = "欠费天数")
    private Integer arrearsDays;

    /**
     * 提醒级别：normal-正常，warning-警告，urgent-紧急
     */
    @Schema(description = "提醒级别")
    private String remindLevel;

    /**
     * 提醒级别描述
     */
    @Schema(description = "提醒级别描述")
    private String remindLevelDesc;

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
     * 签约日期
     */
    @Schema(description = "签约日期")
    private LocalDate signDate;

    /**
     * 生效日期
     */
    @Schema(description = "生效日期")
    private LocalDate effectiveDate;
}
