package com.edu.system.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 节假日 VO
 */
@Data
@Schema(description = "节假日视图对象")
public class SysHolidayVO {

    @Schema(description = "节假日ID")
    private Long id;

    @Schema(description = "节假日名称")
    private String name;

    @Schema(description = "节假日类型：1-法定节假日，2-调休，3-公司假期")
    private Integer type;

    @Schema(description = "节假日类型名称")
    private String typeName;

    @Schema(description = "开始日期")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    private LocalDate endDate;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "校区ID")
    private Long campusId;

    @Schema(description = "校区名称")
    private String campusName;

    @Schema(description = "是否工作日：0-否（休息），1-是（调休上班）")
    private Integer isWorkday;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
