package com.edu.finance.domain.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 前端合同明细兼容 DTO
 */
@Data
public class ContractFormItemDTO {

    private Long courseId;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal discountAmount;
}
