package com.edu.notification.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * 批量短信发送DTO
 */
@Data
public class SmsBatchSendDTO {

    /**
     * 接收人手机号列表
     */
    @NotEmpty(message = "手机号列表不能为空")
    @Size(max = 1000, message = "单次最多发送1000条")
    private List<String> phones;

    /**
     * 短信内容
     */
    @NotBlank(message = "短信内容不能为空")
    private String content;

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
