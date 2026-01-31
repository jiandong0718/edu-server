package com.edu.finance.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 退费申请DTO
 */
@Data
@Schema(description = "退费申请DTO")
public class RefundApplyDTO {

    /**
     * 合同ID
     */
    @NotNull(message = "合同ID不能为空")
    @Schema(description = "合同ID", required = true)
    private Long contractId;

    /**
     * 退费原因
     */
    @NotBlank(message = "退费原因不能为空")
    @Schema(description = "退费原因", required = true, example = "学员搬家，无法继续上课")
    private String reason;

    /**
     * 退费说明
     */
    @Schema(description = "退费说明", example = "学员因工作调动需要搬迁至外地，无法继续在本校区上课")
    private String description;
}
