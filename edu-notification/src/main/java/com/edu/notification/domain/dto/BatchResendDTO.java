package com.edu.notification.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * 批量重发DTO
 */
@Data
public class BatchResendDTO {

    /**
     * 通知记录ID列表
     */
    private List<Long> ids;
}
