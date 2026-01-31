package com.edu.finance.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.edu.common.exception.BusinessException;
import com.edu.finance.domain.entity.Contract;
import com.edu.finance.domain.entity.Payment;
import com.edu.finance.mapper.PaymentMapper;
import com.edu.finance.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 收款服务测试类
 * 测试收款登记、退费计算等核心功能
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("收款服务测试")
class PaymentServiceTest {

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private ContractService contractService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Payment testPayment;
    private Contract testContract;

    @BeforeEach
    void setUp() {
        testPayment = new Payment();
        testPayment.setId(1L);
        testPayment.setPaymentNo("SK20260201001");
        testPayment.setContractId(100L);
        testPayment.setStudentId(200L);
        testPayment.setCampusId(1L);
        testPayment.setAmount(new BigDecimal("5000.00"));
        testPayment.setPaymentMethod("wechat");
        testPayment.setStatus("pending");

        testContract = new Contract();
        testContract.setId(100L);
        testContract.setStudentId(200L);
        testContract.setCampusId(1L);
        testContract.setPaidAmount(new BigDecimal("10000.00"));
        testContract.setReceivedAmount(new BigDecimal("5000.00"));
    }

    @Test
    @DisplayName("测试创建收款记录 - 成功场景")
    void testCreatePayment_Success() {
        // Given
        testPayment.setPaymentNo(null); // 测试自动生成编号
        when(paymentMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(paymentMapper.insert(any(Payment.class))).thenReturn(1);

        // When
        boolean result = paymentService.createPayment(testPayment);

        // Then
        assertTrue(result);
        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentMapper).insert(captor.capture());
        Payment savedPayment = captor.getValue();

        assertNotNull(savedPayment.getPaymentNo());
        assertTrue(savedPayment.getPaymentNo().startsWith("SK"));
        assertEquals("pending", savedPayment.getStatus());
    }

    @Test
    @DisplayName("测试创建收款记录 - 指定收款单号")
    void testCreatePayment_WithPaymentNo() {
        // Given
        when(paymentMapper.insert(any(Payment.class))).thenReturn(1);

        // When
        boolean result = paymentService.createPayment(testPayment);

        // Then
        assertTrue(result);
        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentMapper).insert(captor.capture());
        Payment savedPayment = captor.getValue();

        assertEquals("SK20260201001", savedPayment.getPaymentNo());
    }

    @Test
    @DisplayName("测试生成收款单号 - 当天首个收款")
    void testGeneratePaymentNo_FirstOfDay() {
        // Given
        when(paymentMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        // When
        String paymentNo = paymentService.generatePaymentNo();

        // Then
        assertNotNull(paymentNo);
        assertTrue(paymentNo.startsWith("SK" + LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"))));
        assertTrue(paymentNo.endsWith("0001"));
    }

    @Test
    @DisplayName("测试生成收款单号 - 当天第N个收款")
    void testGeneratePaymentNo_NotFirstOfDay() {
        // Given
        Payment lastPayment = new Payment();
        String today = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        lastPayment.setPaymentNo("SK" + today + "0010");
        when(paymentMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(lastPayment);

        // When
        String paymentNo = paymentService.generatePaymentNo();

        // Then
        assertNotNull(paymentNo);
        assertEquals("SK" + today + "0011", paymentNo);
    }

    @Test
    @DisplayName("测试确认收款 - 成功场景")
    void testConfirmPayment_Success() {
        // Given
        testPayment.setStatus("pending");
        when(paymentMapper.selectById(1L)).thenReturn(testPayment);
        when(paymentMapper.updateById(any(Payment.class))).thenReturn(1);
        when(contractService.getById(100L)).thenReturn(testContract);
        when(contractService.updateById(any(Contract.class))).thenReturn(true);

        // When
        boolean result = paymentService.confirmPayment(1L, "WX20260201123456");

        // Then
        assertTrue(result);
        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentMapper).updateById(paymentCaptor.capture());
        Payment updatedPayment = paymentCaptor.getValue();

        assertEquals("paid", updatedPayment.getStatus());
        assertEquals("WX20260201123456", updatedPayment.getTransactionNo());
        assertNotNull(updatedPayment.getPayTime());

        // Verify contract received amount is updated
        ArgumentCaptor<Contract> contractCaptor = ArgumentCaptor.forClass(Contract.class);
        verify(contractService).updateById(contractCaptor.capture());
        Contract updatedContract = contractCaptor.getValue();
        assertEquals(new BigDecimal("10000.00"), updatedContract.getReceivedAmount());
    }

    @Test
    @DisplayName("测试确认收款 - 收款记录不存在")
    void testConfirmPayment_NotFound() {
        // Given
        when(paymentMapper.selectById(1L)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            paymentService.confirmPayment(1L, "WX20260201123456");
        });
        assertEquals("收款记录不存在", exception.getMessage());
        verify(paymentMapper, never()).updateById(any(Payment.class));
    }

    @Test
    @DisplayName("测试确认收款 - 状态不正确")
    void testConfirmPayment_InvalidStatus() {
        // Given
        testPayment.setStatus("paid");
        when(paymentMapper.selectById(1L)).thenReturn(testPayment);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            paymentService.confirmPayment(1L, "WX20260201123456");
        });
        assertEquals("只有待支付状态的记录才能确认收款", exception.getMessage());
        verify(paymentMapper, never()).updateById(any(Payment.class));
    }

    @Test
    @DisplayName("测试确认收款 - 合同全额支付触发事件")
    void testConfirmPayment_FullyPaid_PublishEvent() {
        // Given
        testPayment.setStatus("pending");
        testPayment.setAmount(new BigDecimal("5000.00"));
        testContract.setReceivedAmount(new BigDecimal("5000.00")); // 已收5000
        testContract.setPaidAmount(new BigDecimal("10000.00")); // 应付10000

        when(paymentMapper.selectById(1L)).thenReturn(testPayment);
        when(paymentMapper.updateById(any(Payment.class))).thenReturn(1);
        when(contractService.getById(100L)).thenReturn(testContract);
        when(contractService.updateById(any(Contract.class))).thenReturn(true);

        // When
        boolean result = paymentService.confirmPayment(1L, "WX20260201123456");

        // Then
        assertTrue(result);
        verify(eventPublisher, times(1)).publishEvent(any());
    }

    @Test
    @DisplayName("测试确认收款 - 部分支付不触发事件")
    void testConfirmPayment_PartialPaid_NoEvent() {
        // Given
        testPayment.setStatus("pending");
        testPayment.setAmount(new BigDecimal("3000.00"));
        testContract.setReceivedAmount(new BigDecimal("2000.00")); // 已收2000
        testContract.setPaidAmount(new BigDecimal("10000.00")); // 应付10000

        when(paymentMapper.selectById(1L)).thenReturn(testPayment);
        when(paymentMapper.updateById(any(Payment.class))).thenReturn(1);
        when(contractService.getById(100L)).thenReturn(testContract);
        when(contractService.updateById(any(Contract.class))).thenReturn(true);

        // When
        boolean result = paymentService.confirmPayment(1L, "WX20260201123456");

        // Then
        assertTrue(result);
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("测试不同支付方式 - 微信支付")
    void testPaymentMethod_Wechat() {
        // Given
        testPayment.setPaymentMethod("wechat");
        when(paymentMapper.insert(any(Payment.class))).thenReturn(1);

        // When
        boolean result = paymentService.createPayment(testPayment);

        // Then
        assertTrue(result);
        assertEquals("wechat", testPayment.getPaymentMethod());
    }

    @Test
    @DisplayName("测试不同支付方式 - 支付宝")
    void testPaymentMethod_Alipay() {
        // Given
        testPayment.setPaymentMethod("alipay");
        when(paymentMapper.insert(any(Payment.class))).thenReturn(1);

        // When
        boolean result = paymentService.createPayment(testPayment);

        // Then
        assertTrue(result);
        assertEquals("alipay", testPayment.getPaymentMethod());
    }

    @Test
    @DisplayName("测试不同支付方式 - 现金")
    void testPaymentMethod_Cash() {
        // Given
        testPayment.setPaymentMethod("cash");
        when(paymentMapper.insert(any(Payment.class))).thenReturn(1);

        // When
        boolean result = paymentService.createPayment(testPayment);

        // Then
        assertTrue(result);
        assertEquals("cash", testPayment.getPaymentMethod());
    }

    @Test
    @DisplayName("测试不同支付方式 - POS机")
    void testPaymentMethod_POS() {
        // Given
        testPayment.setPaymentMethod("pos");
        when(paymentMapper.insert(any(Payment.class))).thenReturn(1);

        // When
        boolean result = paymentService.createPayment(testPayment);

        // Then
        assertTrue(result);
        assertEquals("pos", testPayment.getPaymentMethod());
    }

    @Test
    @DisplayName("测试收款金额计算 - 无优惠券")
    void testPaymentAmount_NoCoupon() {
        // Given
        testPayment.setAmount(new BigDecimal("5000.00"));
        testPayment.setCouponDiscountAmount(BigDecimal.ZERO);

        // When
        BigDecimal actualAmount = testPayment.getAmount();

        // Then
        assertEquals(new BigDecimal("5000.00"), actualAmount);
    }

    @Test
    @DisplayName("测试收款金额计算 - 使用优惠券")
    void testPaymentAmount_WithCoupon() {
        // Given
        testPayment.setAmount(new BigDecimal("5000.00"));
        testPayment.setCouponDiscountAmount(new BigDecimal("200.00"));

        // When
        BigDecimal actualAmount = testPayment.getAmount()
                .subtract(testPayment.getCouponDiscountAmount());

        // Then
        assertEquals(new BigDecimal("4800.00"), actualAmount);
    }

    @Test
    @DisplayName("测试收款状态流转 - 待支付到已支付")
    void testPaymentStatusFlow_PendingToPaid() {
        // Given
        testPayment.setStatus("pending");
        when(paymentMapper.selectById(1L)).thenReturn(testPayment);
        when(paymentMapper.updateById(any(Payment.class))).thenReturn(1);
        when(contractService.getById(100L)).thenReturn(testContract);
        when(contractService.updateById(any(Contract.class))).thenReturn(true);

        // When
        boolean result = paymentService.confirmPayment(1L, "TX123456");

        // Then
        assertTrue(result);
        assertEquals("paid", testPayment.getStatus());
    }

    @Test
    @DisplayName("测试分期付款场景 - 首付")
    void testInstallmentPayment_FirstPayment() {
        // Given
        testPayment.setAmount(new BigDecimal("3000.00")); // 首付3000
        testContract.setPaidAmount(new BigDecimal("10000.00")); // 总价10000
        testContract.setReceivedAmount(BigDecimal.ZERO);

        when(paymentMapper.selectById(1L)).thenReturn(testPayment);
        when(paymentMapper.updateById(any(Payment.class))).thenReturn(1);
        when(contractService.getById(100L)).thenReturn(testContract);
        when(contractService.updateById(any(Contract.class))).thenReturn(true);

        // When
        boolean result = paymentService.confirmPayment(1L, "TX123456");

        // Then
        assertTrue(result);
        ArgumentCaptor<Contract> captor = ArgumentCaptor.forClass(Contract.class);
        verify(contractService).updateById(captor.capture());
        Contract updatedContract = captor.getValue();
        assertEquals(new BigDecimal("3000.00"), updatedContract.getReceivedAmount());
    }

    @Test
    @DisplayName("测试分期付款场景 - 尾款")
    void testInstallmentPayment_FinalPayment() {
        // Given
        testPayment.setAmount(new BigDecimal("7000.00")); // 尾款7000
        testContract.setPaidAmount(new BigDecimal("10000.00")); // 总价10000
        testContract.setReceivedAmount(new BigDecimal("3000.00")); // 已付3000

        when(paymentMapper.selectById(1L)).thenReturn(testPayment);
        when(paymentMapper.updateById(any(Payment.class))).thenReturn(1);
        when(contractService.getById(100L)).thenReturn(testContract);
        when(contractService.updateById(any(Contract.class))).thenReturn(true);

        // When
        boolean result = paymentService.confirmPayment(1L, "TX123456");

        // Then
        assertTrue(result);
        ArgumentCaptor<Contract> captor = ArgumentCaptor.forClass(Contract.class);
        verify(contractService).updateById(captor.capture());
        Contract updatedContract = captor.getValue();
        assertEquals(new BigDecimal("10000.00"), updatedContract.getReceivedAmount());
        verify(eventPublisher, times(1)).publishEvent(any()); // 全额支付触发事件
    }
}
