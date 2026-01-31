package com.edu.system.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 教室VO
 */
@Data
@Schema(description = "教室视图对象")
public class ClassroomVO {

    @Schema(description = "教室ID")
    private Long id;

    @Schema(description = "教室名称")
    private String name;

    @Schema(description = "教室编码")
    private String code;

    @Schema(description = "校区ID")
    private Long campusId;

    @Schema(description = "校区名称")
    private String campusName;

    @Schema(description = "所属楼栋")
    private String building;

    @Schema(description = "楼层")
    private Integer floor;

    @Schema(description = "房间号")
    private String roomNo;

    @Schema(description = "容纳人数")
    private Integer capacity;

    @Schema(description = "面积(平方米)")
    private BigDecimal area;

    @Schema(description = "设施配置列表")
    private List<String> facilities;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;

    @Schema(description = "状态文本")
    private String statusText;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
