package com.edu.system.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 预警信息VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "预警信息VO")
public class WarningVO {

    /**
     * 预警ID（用于标识唯一预警记录）
     */
    @Schema(description = "预警ID")
    private String warningId;

    /**
     * 预警类型
     */
    @Schema(description = "预警类型")
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
     * 预警描述
     */
    @Schema(description = "预警描述")
    private String description;

    /**
     * 关联业务ID
     */
    @Schema(description = "关联业务ID")
    private Long businessId;

    /**
     * 关联业务名称
     */
    @Schema(description = "关联业务名称")
    private String businessName;

    /**
     * 校区ID
     */
    @Schema(description = "校区ID")
    private Long campusId;

    /**
     * 校区名称
     */
    @Schema(description = "校区名称")
    private String campusName;

    /**
     * 当前值
     */
    @Schema(description = "当前值")
    private String currentValue;

    /**
     * 阈值
     */
    @Schema(description = "阈值")
    private String thresholdValue;

    /**
     * 预警时间
     */
    @Schema(description = "预警时间")
    private LocalDateTime warningTime;

    /**
     * 预警汇总统计
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "预警汇总统计")
    public static class WarningSummary {

        /**
         * 总预警数
         */
        @Schema(description = "总预警数")
        private Integer totalCount;

        /**
         * 紧急预警数
         */
        @Schema(description = "紧急预警数")
        private Integer urgentCount;

        /**
         * 警告预警数
         */
        @Schema(description = "警告预警数")
        private Integer warningCount;

        /**
         * 正常预警数
         */
        @Schema(description = "正常预警数")
        private Integer normalCount;

        /**
         * 业务预警数
         */
        @Schema(description = "业务预警数")
        private Integer businessWarningCount;

        /**
         * 运营预警数
         */
        @Schema(description = "运营预警数")
        private Integer operationWarningCount;

        /**
         * 财务预警数
         */
        @Schema(description = "财务预警数")
        private Integer financeWarningCount;

        /**
         * 预警类型分布
         */
        @Schema(description = "预警类型分布")
        private List<WarningTypeDistribution> typeDistribution;
    }

    /**
     * 预警类型分布
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "预警类型分布")
    public static class WarningTypeDistribution {

        /**
         * 预警类型
         */
        @Schema(description = "预警类型")
        private String warningType;

        /**
         * 预警名称
         */
        @Schema(description = "预警名称")
        private String warningName;

        /**
         * 预警数量
         */
        @Schema(description = "预警数量")
        private Integer count;

        /**
         * 预警级别
         */
        @Schema(description = "预警级别")
        private String warningLevel;
    }

    /**
     * 课时不足预警详情
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "课时不足预警详情")
    public static class CourseHourLowWarning {

        /**
         * 学员ID
         */
        @Schema(description = "学员ID")
        private Long studentId;

        /**
         * 学员姓名
         */
        @Schema(description = "学员姓名")
        private String studentName;

        /**
         * 课程ID
         */
        @Schema(description = "课程ID")
        private Long courseId;

        /**
         * 课程名称
         */
        @Schema(description = "课程名称")
        private String courseName;

        /**
         * 剩余课时
         */
        @Schema(description = "剩余课时")
        private BigDecimal remainingHours;

        /**
         * 阈值
         */
        @Schema(description = "阈值")
        private BigDecimal threshold;

        /**
         * 校区名称
         */
        @Schema(description = "校区名称")
        private String campusName;
    }

    /**
     * 课时即将到期预警详情
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "课时即将到期预警详情")
    public static class CourseHourExpireWarning {

        /**
         * 学员ID
         */
        @Schema(description = "学员ID")
        private Long studentId;

        /**
         * 学员姓名
         */
        @Schema(description = "学员姓名")
        private String studentName;

        /**
         * 课程ID
         */
        @Schema(description = "课程ID")
        private Long courseId;

        /**
         * 课程名称
         */
        @Schema(description = "课程名称")
        private String courseName;

        /**
         * 到期日期
         */
        @Schema(description = "到期日期")
        private LocalDate expireDate;

        /**
         * 剩余天数
         */
        @Schema(description = "剩余天数")
        private Integer remainingDays;

        /**
         * 剩余课时
         */
        @Schema(description = "剩余课时")
        private BigDecimal remainingHours;

        /**
         * 校区名称
         */
        @Schema(description = "校区名称")
        private String campusName;
    }

    /**
     * 欠费预警详情
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "欠费预警详情")
    public static class OverdueWarning {

        /**
         * 合同ID
         */
        @Schema(description = "合同ID")
        private Long contractId;

        /**
         * 合同编号
         */
        @Schema(description = "合同编号")
        private String contractNo;

        /**
         * 学员ID
         */
        @Schema(description = "学员ID")
        private Long studentId;

        /**
         * 学员姓名
         */
        @Schema(description = "学员姓名")
        private String studentName;

        /**
         * 欠费金额
         */
        @Schema(description = "欠费金额")
        private BigDecimal overdueAmount;

        /**
         * 欠费天数
         */
        @Schema(description = "欠费天数")
        private Integer overdueDays;

        /**
         * 应付日期
         */
        @Schema(description = "应付日期")
        private LocalDate dueDate;

        /**
         * 校区名称
         */
        @Schema(description = "校区名称")
        private String campusName;
    }

    /**
     * 合同即将到期预警详情
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "合同即将到期预警详情")
    public static class ContractExpireWarning {

        /**
         * 合同ID
         */
        @Schema(description = "合同ID")
        private Long contractId;

        /**
         * 合同编号
         */
        @Schema(description = "合同编号")
        private String contractNo;

        /**
         * 学员ID
         */
        @Schema(description = "学员ID")
        private Long studentId;

        /**
         * 学员姓名
         */
        @Schema(description = "学员姓名")
        private String studentName;

        /**
         * 到期日期
         */
        @Schema(description = "到期日期")
        private LocalDate expireDate;

        /**
         * 剩余天数
         */
        @Schema(description = "剩余天数")
        private Integer remainingDays;

        /**
         * 校区名称
         */
        @Schema(description = "校区名称")
        private String campusName;
    }

    /**
     * 学员流失预警详情
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "学员流失预警详情")
    public static class StudentLossWarning {

        /**
         * 学员ID
         */
        @Schema(description = "学员ID")
        private Long studentId;

        /**
         * 学员姓名
         */
        @Schema(description = "学员姓名")
        private String studentName;

        /**
         * 最后上课日期
         */
        @Schema(description = "最后上课日期")
        private LocalDate lastAttendanceDate;

        /**
         * 未上课天数
         */
        @Schema(description = "未上课天数")
        private Integer noAttendanceDays;

        /**
         * 校区名称
         */
        @Schema(description = "校区名称")
        private String campusName;
    }
}
