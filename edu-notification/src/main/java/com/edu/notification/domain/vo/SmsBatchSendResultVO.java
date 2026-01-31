package com.edu.notification.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 批量短信发送结果VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsBatchSendResultVO {

    /**
     * 总数
     */
    private Integer total;

    /**
     * 成功数
     */
    private Integer successCount;

    /**
     * 失败数
     */
    private Integer failCount;

    /**
     * 详细结果列表
     */
    private List<SmsSendResultVO> details;
}
