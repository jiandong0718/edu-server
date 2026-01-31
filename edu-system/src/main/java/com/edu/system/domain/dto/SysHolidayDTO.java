package com.edu.system.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 节假日 DTO
 */
@Data
@Schema(description = "节假日数据传输对象")
public class SysHolidayDTO {

    @Schema(description = "节假日ID")
    private Long id;

    @Schema(description = "节假日名称", required = true)
    @NotBlank(message = "节假日名称不能为空")
    private String name;

    @Schema(description = "节假日类型：1-法定节假日，2-调休，3-公司假期", required = true)
    @NotNull(message = "节假日类型不能为空")
    private Integer type;

    @Schema(description = "开始日期", required = true)
    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;

    @Schema(description = "结束日期", required = true)
    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "校区ID（NULL表示全局）")
    private Long campusId;

    @Schema(description = "是否工作日：0-否（休息），1-是（调休上班）")
    private Integer isWorkday;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;

    @Schema(description = "备注")
    private String remark;
}
