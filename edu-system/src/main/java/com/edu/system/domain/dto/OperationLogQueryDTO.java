package com.edu.system.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 操作日志查询 DTO
 */
@Data
@Schema(description = "操作日志查询条件")
public class OperationLogQueryDTO {

    @Schema(description = "操作标题（模糊查询）")
    private String title;

    @Schema(description = "操作人员（模糊查询）")
    private String operatorName;

    @Schema(description = "业务类型：0-其他，1-新增，2-修改，3-删除，4-导出，5-导入")
    private Integer businessType;

    @Schema(description = "状态：0-失败，1-成功")
    private Integer status;

    @Schema(description = "校区ID")
    private Long campusId;

    @Schema(description = "IP地址（模糊查询）")
    private String ip;

    @Schema(description = "查询开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(description = "查询结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页数量", example = "10")
    private Integer pageSize = 10;
}
