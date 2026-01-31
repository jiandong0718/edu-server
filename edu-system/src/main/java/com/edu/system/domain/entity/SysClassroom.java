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
     * 教室编码
     */
    private String code;

    /**
     * 校区ID
     */
    private Long campusId;

    /**
     * 所属楼栋
     */
    private String building;

    /**
     * 楼层
     */
    private Integer floor;

    /**
     * 房间号
     */
    private String roomNo;

    /**
     * 容纳人数
     */
    private Integer capacity;

    /**
     * 面积(平方米)
     */
    private java.math.BigDecimal area;

    /**
     * 设施配置(JSON数组)
     */
    private String facilities;

    /**
     * 设备配置(已废弃，使用facilities)
     */
    @Deprecated
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
