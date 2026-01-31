package com.edu.notification.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 短信发送结果VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsSendResultVO {

    /**
     * 发送记录ID
     */
    private Long logId;

    /**
     * 接收人手机号
     */
    private String phone;

    /**
     * 发送状态：success-成功，failed-失败
     */
    private String status;

    /**
     * 第三方平台消息ID
     */
    private String thirdPartyId;

    /**
     * 失败原因
     */
    private String failReason;

    /**
     * 发送时间
     */
    private LocalDateTime sendTime;

    /**
     * 发送成本（元）
     */
    private String cost;
}
