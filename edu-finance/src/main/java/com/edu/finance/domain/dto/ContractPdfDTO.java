package com.edu.finance.domain.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 合同PDF数据传输对象
 */
@Data
public class ContractPdfDTO {

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 签订日期
     */
    private LocalDate signDate;

    /**
     * 生效日期
     */
    private LocalDate effectiveDate;

    /**
     * 到期日期
     */
    private LocalDate expireDate;

    /**
     * 甲方（机构）信息
     */
    private PartyInfo partyA;

    /**
     * 乙方（学员/家长）信息
     */
    private PartyInfo partyB;

    /**
     * 课程明细列表
     */
    private List<CourseItem> courseItems;

    /**
     * 合同总金额
     */
    private BigDecimal totalAmount;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 实付金额
     */
    private BigDecimal paidAmount;

    /**
     * 付款方式
     */
    private String paymentMethod;

    /**
     * 合同条款
     */
    private String terms;

    /**
     * 备注
     */
    private String remark;

    /**
     * 甲方签字（可选）
     */
    private String partyASignature;

    /**
     * 乙方签字（可选）
     */
    private String partyBSignature;

    /**
     * 当事方信息
     */
    @Data
    public static class PartyInfo {
        /**
         * 名称
         */
        private String name;

        /**
         * 联系人
         */
        private String contact;

        /**
         * 电话
         */
        private String phone;

        /**
         * 地址
         */
        private String address;

        /**
         * 身份证号（乙方）
         */
        private String idCard;
    }

    /**
     * 课程明细
     */
    @Data
    public static class CourseItem {
        /**
         * 课程名称
         */
        private String courseName;

        /**
         * 课时数
         */
        private Integer hours;

        /**
         * 单价
         */
        private BigDecimal unitPrice;

        /**
         * 金额
         */
        private BigDecimal amount;
    }
}
