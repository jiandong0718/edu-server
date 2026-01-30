package com.edu.marketing.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 线索实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mkt_lead")
public class Lead extends BaseEntity {

    /**
     * 线索编号
     */
    private String leadNo;

    /**
     * 姓名
     */
    private String name;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 性别：0-未知，1-男，2-女
     */
    private Integer gender;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 来源：offline-地推，referral-转介绍，online_ad-线上广告，walk_in-自然到访，phone-电话咨询
     */
    private String source;

    /**
     * 来源详情
     */
    private String sourceDetail;

    /**
     * 意向课程ID
     */
    private Long intentCourseId;

    /**
     * 意向程度：high-高，medium-中，low-低
     */
    private String intentLevel;

    /**
     * 状态：new-新线索，following-跟进中，appointed-已预约，trialed-已试听，converted-已成交，lost-已流失
     */
    private String status;

    /**
     * 跟进顾问ID
     */
    private Long advisorId;

    /**
     * 校区ID
     */
    private Long campusId;

    /**
     * 下次跟进时间
     */
    private LocalDateTime nextFollowTime;

    /**
     * 最后跟进时间
     */
    private LocalDateTime lastFollowTime;

    /**
     * 跟进次数
     */
    private Integer followCount;

    /**
     * 流失原因
     */
    private String lostReason;

    /**
     * 备注
     */
    private String remark;

    /**
     * 顾问姓名（非数据库字段）
     */
    @TableField(exist = false)
    private String advisorName;

    /**
     * 校区名称（非数据库字段）
     */
    @TableField(exist = false)
    private String campusName;

    /**
     * 意向课程名称（非数据库字段）
     */
    @TableField(exist = false)
    private String intentCourseName;
}
