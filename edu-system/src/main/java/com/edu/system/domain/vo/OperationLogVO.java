package com.edu.system.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志 VO
 */
@Data
@Schema(description = "操作日志视图对象")
public class OperationLogVO {

    @Schema(description = "日志ID")
    private Long id;

    @Schema(description = "操作标题")
    private String title;

    @Schema(description = "业务类型：0-其他，1-新增，2-修改，3-删除，4-导出，5-导入")
    private Integer businessType;

    @Schema(description = "业务类型名称")
    private String businessTypeName;

    @Schema(description = "请求方法")
    private String method;

    @Schema(description = "请求方式")
    private String requestMethod;

    @Schema(description = "操作类别：0-其他，1-后台用户，2-手机端用户")
    private Integer operatorType;

    @Schema(description = "操作人员")
    private String operatorName;

    @Schema(description = "操作人员ID")
    private Long operatorId;

    @Schema(description = "校区ID")
    private Long campusId;

    @Schema(description = "校区名称")
    private String campusName;

    @Schema(description = "请求URL")
    private String url;

    @Schema(description = "操作IP")
    private String ip;

    @Schema(description = "操作地点")
    private String location;

    @Schema(description = "请求参数")
    private String param;

    @Schema(description = "返回结果")
    private String result;

    @Schema(description = "状态：0-失败，1-成功")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "错误消息")
    private String errorMsg;

    @Schema(description = "耗时（毫秒）")
    private Long costTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
