package com.edu.finance.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 合同打印模板实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fin_contract_print_template")
public class ContractPrintTemplate extends BaseEntity {

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 模板编码
     */
    private String templateCode;

    /**
     * 模板类型：default-默认模板，custom-自定义模板
     */
    private String templateType;

    /**
     * 模板内容（HTML）
     */
    private String templateContent;

    /**
     * 纸张大小：A4，A5，Letter
     */
    private String pageSize;

    /**
     * 页面方向：portrait-纵向，landscape-横向
     */
    private String pageOrientation;

    /**
     * 上边距（mm）
     */
    private Integer marginTop;

    /**
     * 下边距（mm）
     */
    private Integer marginBottom;

    /**
     * 左边距（mm）
     */
    private Integer marginLeft;

    /**
     * 右边距（mm）
     */
    private Integer marginRight;

    /**
     * 是否默认模板
     */
    private Boolean isDefault;

    /**
     * 状态：active-启用，inactive-停用
     */
    private String status;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 备注
     */
    private String remark;
}
