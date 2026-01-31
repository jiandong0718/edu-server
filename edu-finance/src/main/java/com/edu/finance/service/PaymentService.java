package com.edu.finance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.finance.domain.dto.ArrearsQueryDTO;
import com.edu.finance.domain.entity.Payment;
import com.edu.finance.domain.vo.ArrearsRemindVO;
import com.edu.finance.domain.vo.ArrearsStatisticsVO;
import com.edu.finance.domain.vo.ArrearsVO;

import java.util.List;

/**
 * 收款服务接口
 */
public interface PaymentService extends IService<Payment> {

    /**
     * 创建收款记录
     */
    boolean createPayment(Payment payment);

    /**
     * 确认收款
     */
    boolean confirmPayment(Long id, String transactionNo);

    /**
     * 生成收款单号
     */
    String generatePaymentNo();

    /**
     * 分页查询欠费记录
     */
    Page<ArrearsVO> getArrearsPage(ArrearsQueryDTO query);

    /**
     * 查询欠费统计
     */
    ArrearsStatisticsVO getArrearsStatistics(ArrearsQueryDTO query);

    /**
     * 查询需要提醒的欠费记录
     */
    List<ArrearsRemindVO> getArrearsRemind(Integer minDays);
}
