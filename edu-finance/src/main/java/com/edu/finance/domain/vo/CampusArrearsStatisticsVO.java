package com.edu.finance.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 校区欠费统计VO
 */
@Data
@Schema(description = "校区欠费统计VO")
public class CampusArrearsStatisticsVO {

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
     * 欠费学员数
     */
    @Schema(description = "欠费学员数")
    private Integer arrearsStudentCount;

    /**
     * 欠费合同数
     */
    @Schema(description = "欠费合同数")
    private Integer arrearsContractCount;

    /**
     * 平均欠费金额
     */
    @Schema(description = "平均欠费金额")
    private BigDecimal avgArrearsAmount;
}
