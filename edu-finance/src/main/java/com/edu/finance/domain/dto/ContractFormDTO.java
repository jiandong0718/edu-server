package com.edu.finance.domain.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 前端合同表单兼容 DTO
 */
@Data
public class ContractFormDTO {

    private Long id;

    private Long studentId;

    private Long campusId;

    private String type;

    private LocalDate signDate;

    private LocalDate startDate;

    private LocalDate endDate;

    private Long salesPersonId;

    private List<ContractFormItemDTO> items;

    private String remark;
}
