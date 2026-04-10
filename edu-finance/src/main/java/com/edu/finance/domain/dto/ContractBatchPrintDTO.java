package com.edu.finance.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * 批量打印合同请求 DTO（兼容 JSON body）
 */
@Data
public class ContractBatchPrintDTO {

    /**
     * 合同ID列表
     */
    private List<Long> contractIds;

    /**
     * 打印模板ID
     */
    private Long templateId;
}
