package com.edu.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 预警配置实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_warning_config")
@Schema(description = "预警配置实体")
public class WarningConfig extends BaseEntity {

    /**
     * 预警类型
     */
    @Schema(description = "预警类型：course_hour_low-课时不足，course_hour_expire-课时即将到期，overdue-欠费，contract_expire-合同即将到期，student_loss-学员流失，class_full-班级满员，schedule_conflict-排课冲突，classroom_conflict-教室冲突，trial_conversion_low-试听转化率低，income_abnormal-收入异常，refund_rate_high-退费率高")
    private String warningType;

    /**
     * 预警名称
     */
    @Schema(description = "预警名称")
    private String warningName;

    /**
     * 预警级别
     */
    @Schema(description = "预警级别：normal-正常，warning-警告，urgent-紧急")
    private String warningLevel;

    /**
     * 阈值配置（JSON格式）
     */
    @Schema(description = "阈值配置（JSON格式），例如：{\"courseHourThreshold\":5,\"daysThreshold\":30}")
    private String thresholdConfig;

    /**
     * 是否启用：0-禁用，1-启用
     */
    @Schema(description = "是否启用：0-禁用，1-启用")
    private Integer enabled;

    /**
     * 校区ID（为空表示全局配置）
     */
    @Schema(description = "校区ID（为空表示全局配置）")
    private Long campusId;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
}
