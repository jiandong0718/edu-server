package com.edu.marketing.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 跟进记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mkt_follow_up")
public class FollowUp extends BaseEntity {

    /**
     * 线索ID
     */
    private Long leadId;

    /**
     * 跟进方式：phone-电话，wechat-微信，visit-到访，other-其他
     */
    private String method;

    /**
     * 跟进内容
     */
    private String content;

    /**
     * 跟进结果
     */
    private String result;

    /**
     * 下次跟进时间
     */
    private LocalDateTime nextFollowTime;

    /**
     * 跟进人ID
     */
    private Long followerId;
}
