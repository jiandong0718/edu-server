package com.edu.finance.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 合同打印记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fin_contract_print_record")
public class ContractPrintRecord extends BaseEntity {

    /**
     * 合同ID
     */
    private Long contractId;

    /**
     * 打印单号
     */
    private String printNo;

    /**
     * 打印模板ID
     */
    private Long templateId;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 打印类型：pdf-PDF打印，paper-纸质打印
     */
    private String printType;

    /**
     * 打印份数
     */
    private Integer printCount;

    /**
     * 文件URL（PDF打印）
     */
    private String fileUrl;

    /**
     * 打印人ID
     */
    private Long printerId;

    /**
     * 打印人姓名
     */
    private String printerName;

    /**
     * 打印时间
     */
    private LocalDateTime printTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 合同编号（非数据库字段）
     */
    @TableField(exist = false)
    private String contractNo;
}
