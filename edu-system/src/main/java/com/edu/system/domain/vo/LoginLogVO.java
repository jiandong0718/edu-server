package com.edu.system.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录日志 VO
 */
@Data
@Schema(description = "登录日志视图对象")
public class LoginLogVO {

    @Schema(description = "日志ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "登录IP")
    private String ip;

    @Schema(description = "登录地点")
    private String location;

    @Schema(description = "浏览器")
    private String browser;

    @Schema(description = "操作系统")
    private String os;

    @Schema(description = "状态：0-失败，1-成功")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "提示消息")
    private String msg;

    @Schema(description = "登录时间")
    private LocalDateTime loginTime;
}
