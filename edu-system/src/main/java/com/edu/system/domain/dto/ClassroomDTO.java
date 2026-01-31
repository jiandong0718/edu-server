package com.edu.system.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * 教室DTO
 */
@Data
@Schema(description = "教室数据传输对象")
public class ClassroomDTO {

    @Schema(description = "教室ID")
    private Long id;

    @NotBlank(message = "教室名称不能为空")
    @Size(max = 50, message = "教室名称不能超过50个字符")
    @Schema(description = "教室名称", required = true)
    private String name;

    @NotBlank(message = "教室编码不能为空")
    @Size(max = 50, message = "教室编码不能超过50个字符")
    @Schema(description = "教室编码", required = true)
    private String code;

    @NotNull(message = "校区ID不能为空")
    @Schema(description = "校区ID", required = true)
    private Long campusId;

    @NotBlank(message = "所属楼栋不能为空")
    @Size(max = 50, message = "楼栋名称不能超过50个字符")
    @Schema(description = "所属楼栋", required = true)
    private String building;

    @NotNull(message = "楼层不能为空")
    @Min(value = 1, message = "楼层必须大于0")
    @Max(value = 99, message = "楼层不能超过99")
    @Schema(description = "楼层", required = true)
    private Integer floor;

    @Size(max = 20, message = "房间号不能超过20个字符")
    @Schema(description = "房间号")
    private String roomNo;

    @NotNull(message = "容量不能为空")
    @Min(value = 1, message = "容量必须大于0")
    @Max(value = 999, message = "容量不能超过999")
    @Schema(description = "容纳人数", required = true)
    private Integer capacity;

    @NotNull(message = "面积不能为空")
    @DecimalMin(value = "0.01", message = "面积必须大于0")
    @DecimalMax(value = "9999.99", message = "面积不能超过9999.99")
    @Schema(description = "面积(平方米)", required = true)
    private BigDecimal area;

    @Schema(description = "设施配置列表")
    private List<String> facilities;

    @NotNull(message = "状态不能为空")
    @Schema(description = "状态：0-禁用，1-启用", required = true)
    private Integer status;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Size(max = 500, message = "备注不能超过500个字符")
    @Schema(description = "备注")
    private String remark;
}
