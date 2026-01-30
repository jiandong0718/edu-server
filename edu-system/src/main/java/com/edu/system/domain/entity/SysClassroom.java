package com.edu.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 教室实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_classroom")
public class SysClassroom extends BaseEntity {

    /**
     * 教室名称
     */
    private String name;

    /**
     * 校区ID
     */
    private Long campusId;

    /**
     * 容纳人数
     */
    private Integer capacity;

    /**
     * 设备配置
     */
    private String equipment;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 备注
     */
    private String remark;
}
