package com.edu.notification.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Map;

/**
 * 短信发送DTO
 */
@Data
public class SmsSendDTO {

    /**
     * 接收人手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 短信内容
     */
    @NotBlank(message = "短信内容不能为空")
    private String content;

    /**
     * 接收人姓名
     */
    private String receiverName;

    /**
     * 接收人ID
     */
    private Long receiverId;

    /**
     * 校区ID
     */
    private Long campusId;

    /**
     * 业务类型
     */
    private String bizType;

    /**
     * 业务ID
     */
    private Long bizId;

    /**
     * 备注
     */
    private String remark;
}
