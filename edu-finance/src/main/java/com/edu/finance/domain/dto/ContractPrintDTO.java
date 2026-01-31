package com.edu.finance.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 合同打印DTO
 */
@Data
public class ContractPrintDTO {

    /**
     * 合同ID
     */
    @NotNull(message = "合同ID不能为空")
    private Long contractId;

    /**
     * 打印模板ID
     */
    private Long templateId;

    /**
     * 打印类型：pdf-PDF打印，paper-纸质打印
     */
    private String printType = "pdf";

    /**
     * 打印份数
     */
    private Integer printCount = 1;

    /**
     * 备注
     */
    private String remark;
}
