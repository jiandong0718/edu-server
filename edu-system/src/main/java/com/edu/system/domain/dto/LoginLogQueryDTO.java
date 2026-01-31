package com.edu.system.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 登录日志查询 DTO
 */
@Data
@Schema(description = "登录日志查询条件")
public class LoginLogQueryDTO {

    @Schema(description = "用户名（模糊查询）")
    private String username;

    @Schema(description = "登录状态：0-失败，1-成功")
    private Integer status;

    @Schema(description = "IP地址（模糊查询）")
    private String ip;

    @Schema(description = "登录地点（模糊查询）")
    private String location;

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
