package com.edu.finance.domain.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 收据DTO
 */
@Data
public class ReceiptDTO {

    /**
     * 收据编号
     */
    private String receiptNo;

    /**
     * 收款日期
     */
    private LocalDateTime paymentDate;

    /**
     * 学员信息
     */
    private StudentInfo studentInfo;

    /**
     * 收款项目
     */
    private PaymentItem paymentItem;

    /**
     * 收款金额（小写）
     */
    private BigDecimal amount;

    /**
     * 收款金额（大写）
     */
    private String amountInWords;

    /**
     * 收款方式
     */
    private String paymentMethod;

    /**
     * 收款人
     */
    private String receiver;

    /**
     * 机构信息
     */
    private InstitutionInfo institutionInfo;

    /**
     * 备注
     */
    private String remark;

    /**
     * 学员信息
     */
    @Data
    public static class StudentInfo {
        /**
         * 学员姓名
         */
        private String name;

        /**
         * 手机号
         */
        private String phone;

        /**
         * 学员编号
         */
        private String studentNo;
    }

    /**
     * 收款项目
     */
    @Data
    public static class PaymentItem {
        /**
         * 项目名称（课程名称）
         */
        private String name;

        /**
         * 数量（课时数）
         */
        private Integer quantity;

        /**
         * 单价
         */
        private BigDecimal unitPrice;

        /**
         * 合同编号
         */
        private String contractNo;
    }

    /**
     * 机构信息
     */
    @Data
    public static class InstitutionInfo {
        /**
         * 机构名称
         */
        private String name;

        /**
         * 机构地址
         */
        private String address;

        /**
         * 联系电话
         */
        private String phone;

        /**
         * 校区名称
         */
        private String campusName;
    }
}
