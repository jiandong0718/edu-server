package com.edu.system.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 节假日查询 DTO
 */
@Data
@Schema(description = "节假日查询条件")
public class SysHolidayQueryDTO {

    @Schema(description = "节假日名称（模糊查询）")
    private String name;

    @Schema(description = "节假日类型：1-法定节假日，2-调休，3-公司假期")
    private Integer type;

    @Schema(description = "校区ID")
    private Long campusId;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;

    @Schema(description = "查询开始日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Schema(description = "查询结束日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页数量", example = "10")
    private Integer pageSize = 10;
}
