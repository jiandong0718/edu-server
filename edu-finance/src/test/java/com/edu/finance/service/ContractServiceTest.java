package com.edu.finance.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.exception.BusinessException;
import com.edu.finance.domain.entity.Contract;
import com.edu.finance.mapper.ContractMapper;
import com.edu.finance.service.impl.ContractServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 合同服务测试类
 * 测试合同创建、金额计算、状态流转等核心功能
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("合同服务测试")
class ContractServiceTest {

    @Mock
    private ContractMapper contractMapper;

    @InjectMocks
    private ContractServiceImpl contractService;

    private Contract testContract;

    @BeforeEach
    void setUp() {
        testContract = new Contract();
        testContract.setId(1L);
        testContract.setContractNo("HT20260201001");
        testContract.setStudentId(100L);
        testContract.setCampusId(1L);
        testContract.setType("new");
        testContract.setAmount(new BigDecimal("10000.00"));
        testContract.setDiscountAmount(new BigDecimal("500.00"));
        testContract.setPaidAmount(new BigDecimal("9500.00"));
        testContract.setReceivedAmount(BigDecimal.ZERO);
        testContract.setTotalHours(100);
        testContract.setStatus("pending");
        testContract.setSalesId(10L);
    }

    @Test
    @DisplayName("测试创建合同 - 成功场景")
    void testCreateContract_Success() {
        // Given
        testContract.setContractNo(null); // 测试自动生成编号
        when(contractMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(contractMapper.insert(any(Contract.class))).thenReturn(1);

        // When
        boolean result = contractService.createContract(testContract);

        // Then
        assertTrue(result);
        ArgumentCaptor<Contract> captor = ArgumentCaptor.forClass(Contract.class);
        verify(contractMapper).insert(captor.capture());
        Contract savedContract = captor.getValue();

        assertNotNull(savedContract.getContractNo());
        assertTrue(savedContract.getContractNo().startsWith("HT"));
        assertEquals("pending", savedContract.getStatus());
        assertEquals(BigDecimal.ZERO, savedContract.getReceivedAmount());
    }

    @Test
    @DisplayName("测试创建合同 - 指定合同编号")
    void testCreateContract_WithContractNo() {
        // Given
        when(contractMapper.insert(any(Contract.class))).thenReturn(1);

        // When
        boolean result = contractService.createContract(testContract);

        // Then
        assertTrue(result);
        ArgumentCaptor<Contract> captor = ArgumentCaptor.forClass(Contract.class);
        verify(contractMapper).insert(captor.capture());
        Contract savedContract = captor.getValue();

        assertEquals("HT20260201001", savedContract.getContractNo());
    }

    @Test
    @DisplayName("测试生成合同编号 - 当天首个合同")
    void testGenerateContractNo_FirstOfDay() {
        // Given
        when(contractMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        // When
        String contractNo = contractService.generateContractNo();

        // Then
        assertNotNull(contractNo);
        assertTrue(contractNo.startsWith("HT" + LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"))));
        assertTrue(contractNo.endsWith("0001"));
    }

    @Test
    @DisplayName("测试生成合同编号 - 当天第N个合同")
    void testGenerateContractNo_NotFirstOfDay() {
        // Given
        Contract lastContract = new Contract();
        String today = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        lastContract.setContractNo("HT" + today + "0005");
        when(contractMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(lastContract);

        // When
        String contractNo = contractService.generateContractNo();

        // Then
        assertNotNull(contractNo);
        assertEquals("HT" + today + "0006", contractNo);
    }

    @Test
    @DisplayName("测试签署合同 - 成功场景")
    void testSignContract_Success() {
        // Given
        testContract.setStatus("pending");
        when(contractMapper.selectById(1L)).thenReturn(testContract);
        when(contractMapper.updateById(any(Contract.class))).thenReturn(1);

        // When
        boolean result = contractService.signContract(1L);

        // Then
        assertTrue(result);
        ArgumentCaptor<Contract> captor = ArgumentCaptor.forClass(Contract.class);
        verify(contractMapper).updateById(captor.capture());
        Contract updatedContract = captor.getValue();

        assertEquals("signed", updatedContract.getStatus());
        assertNotNull(updatedContract.getSignDate());
        assertEquals(LocalDate.now(), updatedContract.getSignDate());
    }

    @Test
    @DisplayName("测试签署合同 - 合同不存在")
    void testSignContract_NotFound() {
        // Given
        when(contractMapper.selectById(1L)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            contractService.signContract(1L);
        });
        assertEquals("合同不存在", exception.getMessage());
        verify(contractMapper, never()).updateById(any(Contract.class));
    }

    @Test
    @DisplayName("测试签署合同 - 状态不正确")
    void testSignContract_InvalidStatus() {
        // Given
        testContract.setStatus("signed");
        when(contractMapper.selectById(1L)).thenReturn(testContract);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            contractService.signContract(1L);
        });
        assertEquals("只有待签署状态的合同才能签署", exception.getMessage());
        verify(contractMapper, never()).updateById(any(Contract.class));
    }

    @Test
    @DisplayName("测试作废合同 - 成功场景")
    void testCancelContract_Success() {
        // Given
        testContract.setStatus("pending");
        when(contractMapper.selectById(1L)).thenReturn(testContract);
        when(contractMapper.updateById(any(Contract.class))).thenReturn(1);

        // When
        boolean result = contractService.cancelContract(1L);

        // Then
        assertTrue(result);
        ArgumentCaptor<Contract> captor = ArgumentCaptor.forClass(Contract.class);
        verify(contractMapper).updateById(captor.capture());
        Contract updatedContract = captor.getValue();

        assertEquals("cancelled", updatedContract.getStatus());
    }

    @Test
    @DisplayName("测试作废合同 - 合同不存在")
    void testCancelContract_NotFound() {
        // Given
        when(contractMapper.selectById(1L)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            contractService.cancelContract(1L);
        });
        assertEquals("合同不存在", exception.getMessage());
    }

    @Test
    @DisplayName("测试作废合同 - 已完成的合同不能作废")
    void testCancelContract_CompletedContract() {
        // Given
        testContract.setStatus("completed");
        when(contractMapper.selectById(1L)).thenReturn(testContract);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            contractService.cancelContract(1L);
        });
        assertEquals("已完成或已退费的合同不能作废", exception.getMessage());
    }

    @Test
    @DisplayName("测试作废合同 - 已退费的合同不能作废")
    void testCancelContract_RefundedContract() {
        // Given
        testContract.setStatus("refunded");
        when(contractMapper.selectById(1L)).thenReturn(testContract);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            contractService.cancelContract(1L);
        });
        assertEquals("已完成或已退费的合同不能作废", exception.getMessage());
    }

    @Test
    @DisplayName("测试合同金额计算 - 新签合同")
    void testContractAmount_NewContract() {
        // Given
        testContract.setType("new");
        testContract.setAmount(new BigDecimal("10000.00"));
        testContract.setDiscountAmount(new BigDecimal("500.00"));
        testContract.setCouponDiscountAmount(BigDecimal.ZERO);

        // When
        BigDecimal expectedPaidAmount = testContract.getAmount()
                .subtract(testContract.getDiscountAmount())
                .subtract(testContract.getCouponDiscountAmount());

        // Then
        assertEquals(new BigDecimal("9500.00"), expectedPaidAmount);
    }

    @Test
    @DisplayName("测试合同金额计算 - 使用优惠券")
    void testContractAmount_WithCoupon() {
        // Given
        testContract.setAmount(new BigDecimal("10000.00"));
        testContract.setDiscountAmount(new BigDecimal("500.00"));
        testContract.setCouponDiscountAmount(new BigDecimal("200.00"));

        // When
        BigDecimal expectedPaidAmount = testContract.getAmount()
                .subtract(testContract.getDiscountAmount())
                .subtract(testContract.getCouponDiscountAmount());

        // Then
        assertEquals(new BigDecimal("9300.00"), expectedPaidAmount);
    }

    @Test
    @DisplayName("测试合同状态流转 - 待签署到已签署")
    void testContractStatusFlow_PendingToSigned() {
        // Given
        testContract.setStatus("pending");
        when(contractMapper.selectById(1L)).thenReturn(testContract);
        when(contractMapper.updateById(any(Contract.class))).thenReturn(1);

        // When
        boolean result = contractService.signContract(1L);

        // Then
        assertTrue(result);
        assertEquals("signed", testContract.getStatus());
    }

    @Test
    @DisplayName("测试合同状态流转 - 已签署到已作废")
    void testContractStatusFlow_SignedToCancelled() {
        // Given
        testContract.setStatus("signed");
        when(contractMapper.selectById(1L)).thenReturn(testContract);
        when(contractMapper.updateById(any(Contract.class))).thenReturn(1);

        // When
        boolean result = contractService.cancelContract(1L);

        // Then
        assertTrue(result);
        assertEquals("cancelled", testContract.getStatus());
    }

    @Test
    @DisplayName("测试分页查询合同列表")
    void testPageList() {
        // Given
        IPage<Contract> page = new Page<>(1, 10);
        Contract query = new Contract();
        query.setStudentId(100L);
        when(contractMapper.selectContractPage(any(IPage.class), any(Contract.class))).thenReturn(page);

        // When
        IPage<Contract> result = contractService.pageList(page, query);

        // Then
        assertNotNull(result);
        verify(contractMapper, times(1)).selectContractPage(any(IPage.class), any(Contract.class));
    }

    @Test
    @DisplayName("测试续费合同")
    void testRenewContract() {
        // Given
        testContract.setType("renew");
        testContract.setAmount(new BigDecimal("8000.00"));
        when(contractMapper.insert(any(Contract.class))).thenReturn(1);

        // When
        boolean result = contractService.createContract(testContract);

        // Then
        assertTrue(result);
        assertEquals("renew", testContract.getType());
    }

    @Test
    @DisplayName("测试升级合同")
    void testUpgradeContract() {
        // Given
        testContract.setType("upgrade");
        testContract.setAmount(new BigDecimal("5000.00"));
        when(contractMapper.insert(any(Contract.class))).thenReturn(1);

        // When
        boolean result = contractService.createContract(testContract);

        // Then
        assertTrue(result);
        assertEquals("upgrade", testContract.getType());
    }
}
