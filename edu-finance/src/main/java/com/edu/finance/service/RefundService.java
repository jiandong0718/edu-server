package com.edu.finance.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.finance.domain.dto.RefundApplyDTO;
import com.edu.finance.domain.dto.RefundApproveDTO;
import com.edu.finance.domain.dto.RefundCalculationDTO;
import com.edu.finance.domain.entity.Refund;

/**
 * 退费申请服务接口
 */
public interface RefundService extends IService<Refund> {

    /**
     * 分页查询退费申请列表
     */
    IPage<Refund> pageList(IPage<Refund> page, Refund query);

    /**
     * 计算退费金额
     *
     * @param contractId 合同ID
     * @return 退费金额计算结果
     */
    RefundCalculationDTO calculateRefundAmount(Long contractId);

    /**
     * 提交退费申请
     *
     * @param applyDTO 退费申请DTO
     * @return 退费申请ID
     */
    Long applyRefund(RefundApplyDTO applyDTO);

    /**
     * 审批退费申请
     *
     * @param approveDTO 审批DTO
     * @return 是否成功
     */
    boolean approveRefund(RefundApproveDTO approveDTO);

    /**
     * 执行退款
     *
     * @param refundId 退费申请ID
     * @return 是否成功
     */
    boolean executeRefund(Long refundId);

    /**
     * 生成退费单号
     */
    String generateRefundNo();
}
