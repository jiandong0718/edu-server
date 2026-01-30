package com.edu.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 节假日实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_holiday")
public class SysHoliday extends BaseEntity {

    /**
     * 节假日名称
     */
    private String name;

    /**
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;

    /**
     * 节假日类型：1-法定节假日，2-调休，3-公司假期
     */
    private Integer type;

    /**
     * 是否工作日：0-否（休息），1-是（调休上班）
     */
    private Integer isWorkday;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
