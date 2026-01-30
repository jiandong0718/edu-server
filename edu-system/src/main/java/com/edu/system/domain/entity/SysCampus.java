package com.edu.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 校区实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_campus")
public class SysCampus extends BaseEntity {

    /**
     * 校区名称
     */
    private String name;

    /**
     * 校区编码
     */
    private String code;

    /**
     * 校区地址
     */
    private String address;

    /**
     * 联系人
     */
    private String contactName;

    /**
     * 联系电话
     */
    private String contactPhone;

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
