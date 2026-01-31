package com.edu.notification.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import java.util.Map;

/**
 * 模板短信发送DTO
 */
@Data
public class SmsTemplateSendDTO {

    /**
     * 接收人手机号（单个）
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 接收人手机号列表（批量）
     */
    private List<String> phones;

    /**
     * 模板编码
     */
    @NotBlank(message = "模板编码不能为空")
    private String templateCode;

    /**
     * 模板参数
     * 例如：{"name": "张三", "code": "123456", "time": "5分钟"}
     */
    private Map<String, String> params;

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
