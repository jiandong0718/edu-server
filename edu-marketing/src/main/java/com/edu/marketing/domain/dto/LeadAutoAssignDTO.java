package com.edu.marketing.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * 线索自动分配DTO
 */
@Data
public class LeadAutoAssignDTO {

    /**
     * 线索ID列表
     */
    private List<Long> leadIds;

    /**
     * 校区ID
     */
    private Long campusId;
}
