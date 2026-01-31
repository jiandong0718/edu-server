package com.edu.finance.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课时消课规则VO
 */
@Data
@Schema(description = "课时消课规则VO")
public class ClassHourRuleVO {

    /**
     * 规则ID
     */
    @Schema(description = "规则ID", example = "1")
    private Long id;

    /**
     * 规则名称
     */
    @Schema(description = "规则名称", example = "一对一课程规则")
    private String name;

    /**
     * 课程ID
     */
    @Schema(description = "课程ID", example = "1")
    private Long courseId;

    /**
     * 课程名称
     */
    @Schema(description = "课程名称", example = "数学一对一")
    private String courseName;

    /**
     * 班级类型
     */
    @Schema(description = "班级类型：one_on_one-一对一，small_class-小班课，large_class-大班课", example = "one_on_one")
    private String classType;

    /**
     * 班级类型描述
     */
    @Schema(description = "班级类型描述", example = "一对一")
    private String classTypeDesc;

    /**
     * 扣减类型
     */
    @Schema(description = "扣减类型：per_hour-按课时，per_class-按课次，custom-自定义", example = "per_class")
    private String deductType;

    /**
     * 扣减类型描述
     */
    @Schema(description = "扣减类型描述", example = "按课次")
    private String deductTypeDesc;

    /**
     * 扣减数量
     */
    @Schema(description = "扣减数量", example = "1.0")
    private BigDecimal deductAmount;

    /**
     * 状态
     */
    @Schema(description = "状态：active-启用，inactive-停用", example = "active")
    private String status;

    /**
     * 状态描述
     */
    @Schema(description = "状态描述", example = "启用")
    private String statusDesc;

    /**
     * 校区ID
     */
    @Schema(description = "校区ID", example = "1")
    private Long campusId;

    /**
     * 校区名称
     */
    @Schema(description = "校区名称", example = "总部校区")
    private String campusName;

    /**
     * 备注
     */
    @Schema(description = "备注", example = "一对一课程按课次扣减")
    private String remark;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    @Schema(description = "创建人", example = "1")
    private Long createBy;

    /**
     * 创建人姓名
     */
    @Schema(description = "创建人姓名", example = "管理员")
    private String createByName;
}
