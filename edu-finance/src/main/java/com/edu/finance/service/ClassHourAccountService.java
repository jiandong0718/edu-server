package com.edu.finance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.finance.domain.dto.ClassHourAccountCreateDTO;
import com.edu.finance.domain.dto.ClassHourAdjustDTO;
import com.edu.finance.domain.dto.ClassHourBalanceQueryDTO;
import com.edu.finance.domain.dto.ClassHourBatchAdjustDTO;
import com.edu.finance.domain.dto.ClassHourDeductDTO;
import com.edu.finance.domain.entity.ClassHourAccount;
import com.edu.finance.domain.vo.ClassHourAccountVO;
import com.edu.finance.domain.vo.ClassHourBalanceVO;
import com.edu.finance.domain.vo.ClassHourStatisticsVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 课时账户服务接口
 */
public interface ClassHourAccountService extends IService<ClassHourAccount> {

    /**
     * 创建课时账户
     *
     * @param dto 创建DTO
     * @return 是否成功
     */
    boolean createAccount(ClassHourAccountCreateDTO dto);

    /**
     * 根据合同创建课时账户
     * 合同支付后自动调用
     *
     * @param contractId 合同ID
     * @return 是否成功
     */
    boolean createAccountByContract(Long contractId);

    /**
     * 扣减课时
     *
     * @param dto 扣减DTO
     * @return 是否成功
     */
    boolean deductHours(ClassHourDeductDTO dto);

    /**
     * 查询学员的课时账户列表
     *
     * @param studentId 学员ID
     * @return 课时账户列表
     */
    List<ClassHourAccountVO> getByStudentId(Long studentId);

    /**
     * 查询学员指定课程的课时账户
     *
     * @param studentId 学员ID
     * @param courseId  课程ID
     * @return 课时账户
     */
    ClassHourAccount getByStudentAndCourse(Long studentId, Long courseId);

    /**
     * 冻结课时账户
     *
     * @param id 账户ID
     * @return 是否成功
     */
    boolean freezeAccount(Long id);

    /**
     * 解冻课时账户
     *
     * @param id 账户ID
     * @return 是否成功
     */
    boolean unfreezeAccount(Long id);

    /**
     * 调整课时余额
     *
     * @param id     账户ID
     * @param hours  调整数量（正数增加，负数减少）
     * @param remark 备注
     * @return 是否成功
     */
    boolean adjustHours(Long id, BigDecimal hours, String remark);

    /**
     * 课时调整（赠送/扣减/撤销）
     *
     * @param dto 调整DTO
     * @return 是否成功
     */
    boolean adjustClassHour(ClassHourAdjustDTO dto);

    /**
     * 批量课时调整
     *
     * @param dto 批量调整DTO
     * @return 调整结果（账户ID -> 是否成功）
     */
    Map<Long, Boolean> batchAdjustClassHour(ClassHourBatchAdjustDTO dto);

    /**
     * 赠送课时
     *
     * @param accountId 账户ID
     * @param hours     赠送课时数
     * @param reason    赠送原因
     * @return 是否成功
     */
    boolean giftHours(Long accountId, BigDecimal hours, String reason);

    /**
     * 撤销课时记录
     *
     * @param recordId 记录ID
     * @param reason   撤销原因
     * @return 是否成功
     */
    boolean revokeRecord(Long recordId, String reason);

    /**
     * 查询课时余额（支持多条件查询）
     *
     * @param query 查询条件
     * @return 课时余额列表
     */
    List<ClassHourBalanceVO> queryBalance(ClassHourBalanceQueryDTO query);

    /**
     * 查询预警账户列表
     *
     * @param warningType 预警类型：low_balance-余额不足，expiring-即将过期
     * @param threshold   阈值
     * @return 预警账户列表
     */
    List<ClassHourBalanceVO> getWarningAccounts(String warningType, BigDecimal threshold);

    /**
     * 统计学员课时使用情况
     *
     * @param studentId 学员ID
     * @return 统计结果
     */
    ClassHourStatisticsVO statisticsByStudent(Long studentId);

    /**
     * 统计课程消课情况
     *
     * @param courseId 课程ID
     * @return 统计结果
     */
    ClassHourStatisticsVO statisticsByCourse(Long courseId);

    /**
     * 统计校区课时数据
     *
     * @param campusId 校区ID
     * @return 统计结果
     */
    ClassHourStatisticsVO statisticsByCampus(Long campusId);
}
