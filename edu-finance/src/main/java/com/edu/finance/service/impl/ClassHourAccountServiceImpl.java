package com.edu.finance.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.exception.BusinessException;
import com.edu.finance.domain.dto.ClassHourAccountCreateDTO;
import com.edu.finance.domain.dto.ClassHourAdjustDTO;
import com.edu.finance.domain.dto.ClassHourBalanceQueryDTO;
import com.edu.finance.domain.dto.ClassHourBatchAdjustDTO;
import com.edu.finance.domain.dto.ClassHourDeductDTO;
import com.edu.finance.domain.entity.ClassHourAccount;
import com.edu.finance.domain.entity.ClassHourRecord;
import com.edu.finance.domain.entity.Contract;
import com.edu.finance.domain.entity.ContractItem;
import com.edu.finance.domain.vo.ClassHourAccountVO;
import com.edu.finance.domain.vo.ClassHourBalanceVO;
import com.edu.finance.domain.vo.ClassHourStatisticsVO;
import com.edu.finance.event.ClassHourWarningEvent;
import com.edu.finance.mapper.ClassHourAccountMapper;
import com.edu.finance.mapper.ContractItemMapper;
import com.edu.finance.service.ClassHourAccountService;
import com.edu.finance.service.ClassHourRecordService;
import com.edu.finance.service.ContractService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 课时账户服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClassHourAccountServiceImpl extends ServiceImpl<ClassHourAccountMapper, ClassHourAccount> implements ClassHourAccountService {

    private final ClassHourRecordService classHourRecordService;
    private final ContractService contractService;
    private final ContractItemMapper contractItemMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createAccount(ClassHourAccountCreateDTO dto) {
        // 检查是否已存在相同的课时账户
        ClassHourAccount existingAccount = getOne(new LambdaQueryWrapper<ClassHourAccount>()
                .eq(ClassHourAccount::getStudentId, dto.getStudentId())
                .eq(ClassHourAccount::getContractId, dto.getContractId())
                .eq(ClassHourAccount::getCourseId, dto.getCourseId()));

        if (existingAccount != null) {
            log.warn("课时账户已存在: studentId={}, contractId={}, courseId={}",
                    dto.getStudentId(), dto.getContractId(), dto.getCourseId());
            return false;
        }

        // 创建课时账户
        ClassHourAccount account = new ClassHourAccount();
        account.setStudentId(dto.getStudentId());
        account.setContractId(dto.getContractId());
        account.setCourseId(dto.getCourseId());
        account.setCampusId(dto.getCampusId());
        account.setTotalHours(dto.getTotalHours());
        account.setUsedHours(BigDecimal.ZERO);
        account.setRemainingHours(dto.getTotalHours());
        account.setGiftHours(dto.getGiftHours() != null ? dto.getGiftHours() : BigDecimal.ZERO);
        account.setStatus("active");

        boolean result = save(account);

        if (result) {
            log.info("课时账户创建成功: studentId={}, contractId={}, courseId={}, totalHours={}",
                    dto.getStudentId(), dto.getContractId(), dto.getCourseId(), dto.getTotalHours());
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createAccountByContract(Long contractId) {
        // 获取合同信息
        Contract contract = contractService.getById(contractId);
        if (contract == null) {
            throw new BusinessException("合同不存在");
        }

        // 获取合同明细
        List<ContractItem> items = contractItemMapper.selectByContractId(contractId);
        if (items == null || items.isEmpty()) {
            log.warn("合同没有明细，无法创建课时账户: contractId={}", contractId);
            return false;
        }

        // 为每个课程创建课时账户
        int successCount = 0;
        for (ContractItem item : items) {
            if (item.getHours() != null && item.getHours() > 0) {
                ClassHourAccountCreateDTO dto = new ClassHourAccountCreateDTO();
                dto.setStudentId(contract.getStudentId());
                dto.setContractId(contractId);
                dto.setCourseId(item.getCourseId());
                dto.setCampusId(contract.getCampusId());
                dto.setTotalHours(BigDecimal.valueOf(item.getHours()));
                dto.setGiftHours(BigDecimal.ZERO);

                if (createAccount(dto)) {
                    successCount++;
                }
            }
        }

        log.info("根据合同创建课时账户完成: contractId={}, total={}, success={}",
                contractId, items.size(), successCount);

        return successCount > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deductHours(ClassHourDeductDTO dto) {
        // 查找学员的课时账户
        ClassHourAccount account = getByStudentAndCourse(dto.getStudentId(), dto.getCourseId());
        if (account == null) {
            throw new BusinessException("学员没有该课程的课时账户");
        }

        // 检查账户状态
        if ("frozen".equals(account.getStatus())) {
            throw new BusinessException("课时账户已冻结，无法扣减");
        }

        if ("exhausted".equals(account.getStatus())) {
            throw new BusinessException("课时账户已用完，无法扣减");
        }

        // 检查余额是否足够
        if (account.getRemainingHours().compareTo(dto.getHours()) < 0) {
            // 发送课时不足预警
            publishWarningEvent(account, "insufficient_balance",
                    "课时余额不足，剩余: " + account.getRemainingHours() + "，需要: " + dto.getHours());
            throw new BusinessException("课时余额不足，剩余: " + account.getRemainingHours() + "，需要: " + dto.getHours());
        }

        // 扣减课时
        BigDecimal newUsedHours = account.getUsedHours().add(dto.getHours());
        BigDecimal newRemainingHours = account.getRemainingHours().subtract(dto.getHours());

        account.setUsedHours(newUsedHours);
        account.setRemainingHours(newRemainingHours);

        // 如果余额为0，更新状态为已用完
        if (newRemainingHours.compareTo(BigDecimal.ZERO) == 0) {
            account.setStatus("exhausted");
        }

        boolean result = updateById(account);

        if (result) {
            // 记录课时消耗
            ClassHourRecord record = new ClassHourRecord();
            record.setAccountId(account.getId());
            record.setStudentId(dto.getStudentId());
            record.setScheduleId(dto.getScheduleId());
            record.setType("consume");
            record.setHours(dto.getHours().negate()); // 负数表示扣减
            record.setBalance(newRemainingHours);
            record.setRemark(dto.getRemark());
            classHourRecordService.save(record);

            log.info("课时扣减成功: studentId={}, courseId={}, hours={}, remaining={}",
                    dto.getStudentId(), dto.getCourseId(), dto.getHours(), newRemainingHours);

            // 检查是否需要发送低余额预警（剩余课时 <= 5）
            BigDecimal warningThreshold = BigDecimal.valueOf(5);
            if (newRemainingHours.compareTo(BigDecimal.ZERO) > 0
                    && newRemainingHours.compareTo(warningThreshold) <= 0) {
                publishWarningEvent(account, "low_balance",
                        "课时余额不足，剩余: " + newRemainingHours + " 课时");
            }
        }

        return result;
    }

    /**
     * 发布课时预警事件
     */
    private void publishWarningEvent(ClassHourAccount account, String warningType, String message) {
        try {
            ClassHourWarningEvent event = new ClassHourWarningEvent(
                    this,
                    warningType,
                    account.getId(),
                    account.getStudentId(),
                    account.getCourseId(),
                    account.getRemainingHours(),
                    BigDecimal.valueOf(5), // 默认阈值
                    message,
                    true
            );
            eventPublisher.publishEvent(event);
            log.info("发布课时预警事件: type={}, studentId={}, courseId={}, remaining={}",
                    warningType, account.getStudentId(), account.getCourseId(), account.getRemainingHours());
        } catch (Exception e) {
            log.error("发布课时预警事件失败: studentId={}, courseId={}",
                    account.getStudentId(), account.getCourseId(), e);
        }
    }

    @Override
    public List<ClassHourAccountVO> getByStudentId(Long studentId) {
        List<ClassHourAccount> accounts = list(new LambdaQueryWrapper<ClassHourAccount>()
                .eq(ClassHourAccount::getStudentId, studentId)
                .orderByDesc(ClassHourAccount::getCreateTime));

        List<ClassHourAccountVO> voList = new ArrayList<>();
        for (ClassHourAccount account : accounts) {
            ClassHourAccountVO vo = BeanUtil.copyProperties(account, ClassHourAccountVO.class);
            // TODO: 填充关联信息（学员姓名、课程名称、校区名称等）
            voList.add(vo);
        }

        return voList;
    }

    @Override
    public ClassHourAccount getByStudentAndCourse(Long studentId, Long courseId) {
        return getOne(new LambdaQueryWrapper<ClassHourAccount>()
                .eq(ClassHourAccount::getStudentId, studentId)
                .eq(ClassHourAccount::getCourseId, courseId)
                .eq(ClassHourAccount::getStatus, "active")
                .orderByDesc(ClassHourAccount::getCreateTime)
                .last("LIMIT 1"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean freezeAccount(Long id) {
        ClassHourAccount account = getById(id);
        if (account == null) {
            throw new BusinessException("课时账户不存在");
        }

        if ("frozen".equals(account.getStatus())) {
            throw new BusinessException("课时账户已经是冻结状态");
        }

        account.setStatus("frozen");
        return updateById(account);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unfreezeAccount(Long id) {
        ClassHourAccount account = getById(id);
        if (account == null) {
            throw new BusinessException("课时账户不存在");
        }

        if (!"frozen".equals(account.getStatus())) {
            throw new BusinessException("只有冻结状态的账户才能解冻");
        }

        // 检查余额，如果为0则设置为已用完，否则设置为正常
        if (account.getRemainingHours().compareTo(BigDecimal.ZERO) == 0) {
            account.setStatus("exhausted");
        } else {
            account.setStatus("active");
        }

        return updateById(account);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean adjustHours(Long id, BigDecimal hours, String remark) {
        ClassHourAccount account = getById(id);
        if (account == null) {
            throw new BusinessException("课时账户不存在");
        }

        // 计算调整后的余额
        BigDecimal newRemainingHours = account.getRemainingHours().add(hours);
        if (newRemainingHours.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("调整后余额不能为负数");
        }

        // 更新账户
        BigDecimal newTotalHours = account.getTotalHours().add(hours);
        account.setTotalHours(newTotalHours);
        account.setRemainingHours(newRemainingHours);

        // 更新状态
        if (newRemainingHours.compareTo(BigDecimal.ZERO) == 0) {
            account.setStatus("exhausted");
        } else if ("exhausted".equals(account.getStatus())) {
            account.setStatus("active");
        }

        boolean result = updateById(account);

        if (result) {
            // 记录调整
            ClassHourRecord record = new ClassHourRecord();
            record.setAccountId(account.getId());
            record.setStudentId(account.getStudentId());
            record.setScheduleId(null);
            record.setType("adjust");
            record.setHours(hours);
            record.setBalance(newRemainingHours);
            record.setRemark(remark);
            classHourRecordService.save(record);

            log.info("课时调整成功: accountId={}, hours={}, newRemaining={}", id, hours, newRemainingHours);
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean adjustClassHour(ClassHourAdjustDTO dto) {
        switch (dto.getAdjustType()) {
            case "gift":
                return giftHours(dto.getAccountId(), dto.getHours(), dto.getReason());
            case "deduct":
                return adjustHours(dto.getAccountId(), dto.getHours().negate(), dto.getReason());
            case "revoke":
                if (dto.getOriginalRecordId() == null) {
                    throw new BusinessException("撤销操作需要提供原记录ID");
                }
                return revokeRecord(dto.getOriginalRecordId(), dto.getReason());
            default:
                throw new BusinessException("不支持的调整类型: " + dto.getAdjustType());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<Long, Boolean> batchAdjustClassHour(ClassHourBatchAdjustDTO dto) {
        Map<Long, Boolean> resultMap = new HashMap<>();

        if (dto.getAdjustments() == null || dto.getAdjustments().isEmpty()) {
            throw new BusinessException("调整列表不能为空");
        }

        // 如果需要审批，这里可以添加审批逻辑
        if (Boolean.TRUE.equals(dto.getNeedApproval())) {
            if (dto.getApproverId() == null) {
                throw new BusinessException("需要审批时必须指定审批人");
            }
            // TODO: 实现审批流程
            log.info("批量课时调整需要审批，审批人ID: {}", dto.getApproverId());
        }

        // 批量执行调整
        for (ClassHourAdjustDTO adjustDTO : dto.getAdjustments()) {
            try {
                boolean success = adjustClassHour(adjustDTO);
                resultMap.put(adjustDTO.getAccountId(), success);
            } catch (Exception e) {
                log.error("课时调整失败: accountId={}, error={}", adjustDTO.getAccountId(), e.getMessage());
                resultMap.put(adjustDTO.getAccountId(), false);
            }
        }

        log.info("批量课时调整完成: total={}, success={}, failed={}",
                dto.getAdjustments().size(),
                resultMap.values().stream().filter(v -> v).count(),
                resultMap.values().stream().filter(v -> !v).count());

        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean giftHours(Long accountId, BigDecimal hours, String reason) {
        ClassHourAccount account = getById(accountId);
        if (account == null) {
            throw new BusinessException("课时账户不存在");
        }

        if (hours.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("赠送课时数必须大于0");
        }

        // 更新账户
        BigDecimal newTotalHours = account.getTotalHours().add(hours);
        BigDecimal newRemainingHours = account.getRemainingHours().add(hours);
        BigDecimal newGiftHours = account.getGiftHours().add(hours);

        account.setTotalHours(newTotalHours);
        account.setRemainingHours(newRemainingHours);
        account.setGiftHours(newGiftHours);

        // 如果账户是已用完状态，恢复为正常状态
        if ("exhausted".equals(account.getStatus())) {
            account.setStatus("active");
        }

        boolean result = updateById(account);

        if (result) {
            // 记录赠送
            ClassHourRecord record = new ClassHourRecord();
            record.setAccountId(account.getId());
            record.setStudentId(account.getStudentId());
            record.setScheduleId(null);
            record.setType("gift");
            record.setHours(hours);
            record.setBalance(newRemainingHours);
            record.setRemark(reason);
            classHourRecordService.save(record);

            log.info("课时赠送成功: accountId=, hours=, newRemaining={}", accountId, hours, newRemainingHours);
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean revokeRecord(Long recordId, String reason) {
        // 查询原记录
        ClassHourRecord originalRecord = classHourRecordService.getById(recordId);
        if (originalRecord == null) {
            throw new BusinessException("课时记录不存在");
        }

        // 只能撤销消耗类型的记录
        if (!"consume".equals(originalRecord.getType())) {
            throw new BusinessException("只能撤销消耗类型的记录");
        }

        // 查询账户
        ClassHourAccount account = getById(originalRecord.getAccountId());
        if (account == null) {
            throw new BusinessException("课时账户不存在");
        }

        // 撤销操作：将扣减的课时加回来
        BigDecimal revokeHours = originalRecord.getHours().abs(); // 原记录是负数，取绝对值
        BigDecimal newUsedHours = account.getUsedHours().subtract(revokeHours);
        BigDecimal newRemainingHours = account.getRemainingHours().add(revokeHours);

        // 确保不会出现负数
        if (newUsedHours.compareTo(BigDecimal.ZERO) < 0) {
            newUsedHours = BigDecimal.ZERO;
        }

        account.setUsedHours(newUsedHours);
        account.setRemainingHours(newRemainingHours);

        // 如果账户是已用完状态，恢复为正常状态
        if ("exhausted".equals(account.getStatus()) && newRemainingHours.compareTo(BigDecimal.ZERO) > 0) {
            account.setStatus("active");
        }

        boolean result = updateById(account);

        if (result) {
            // 记录撤销操作
            ClassHourRecord revokeRecord = new ClassHourRecord();
            revokeRecord.setAccountId(account.getId());
            revokeRecord.setStudentId(account.getStudentId());
            revokeRecord.setScheduleId(originalRecord.getScheduleId());
            revokeRecord.setType("revoke");
            revokeRecord.setHours(revokeHours);
            revokeRecord.setBalance(newRemainingHours);
            revokeRecord.setRemark("撤销记录ID:" + recordId + ", 原因:" + reason);
            classHourRecordService.save(revokeRecord);

            log.info("课时撤销成功: recordId={}, accountId={}, hours={}, newRemaining={}",
                    recordId, account.getId(), revokeHours, newRemainingHours);
        }

        return result;
    }

    @Override
    public List<ClassHourBalanceVO> queryBalance(ClassHourBalanceQueryDTO query) {
        LambdaQueryWrapper<ClassHourAccount> wrapper = new LambdaQueryWrapper<>();

        if (query.getStudentId() != null) {
            wrapper.eq(ClassHourAccount::getStudentId, query.getStudentId());
        }
        if (query.getCourseId() != null) {
            wrapper.eq(ClassHourAccount::getCourseId, query.getCourseId());
        }
        if (query.getCampusId() != null) {
            wrapper.eq(ClassHourAccount::getCampusId, query.getCampusId());
        }
        if (query.getStatus() != null) {
            wrapper.eq(ClassHourAccount::getStatus, query.getStatus());
        }

        wrapper.orderByDesc(ClassHourAccount::getCreateTime);

        List<ClassHourAccount> accounts = list(wrapper);
        List<ClassHourBalanceVO> voList = new ArrayList<>();

        for (ClassHourAccount account : accounts) {
            ClassHourBalanceVO vo = convertToBalanceVO(account);
            voList.add(vo);
        }

        return voList;
    }

    @Override
    public List<ClassHourBalanceVO> getWarningAccounts(String warningType, BigDecimal threshold) {
        LambdaQueryWrapper<ClassHourAccount> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassHourAccount::getStatus, "active");

        if ("low_balance".equals(warningType)) {
            // 余额不足预警
            wrapper.le(ClassHourAccount::getRemainingHours, threshold);
            wrapper.gt(ClassHourAccount::getRemainingHours, BigDecimal.ZERO);
        }

        List<ClassHourAccount> accounts = list(wrapper);
        List<ClassHourBalanceVO> voList = new ArrayList<>();

        for (ClassHourAccount account : accounts) {
            ClassHourBalanceVO vo = convertToBalanceVO(account);
            vo.setIsWarning(true);
            vo.setWarningReason("剩余课时不足" + threshold + "课时");
            voList.add(vo);
        }

        return voList;
    }

    @Override
    public ClassHourStatisticsVO statisticsByStudent(Long studentId) {
        ClassHourStatisticsVO statistics = baseMapper.statisticsByStudent(studentId);
        if (statistics == null) {
            statistics = new ClassHourStatisticsVO();
            statistics.setDimension("student");
            statistics.setDimensionId(studentId);
            statistics.setAccountCount(0);
            statistics.setTotalHours(BigDecimal.ZERO);
            statistics.setUsedHours(BigDecimal.ZERO);
            statistics.setRemainingHours(BigDecimal.ZERO);
            statistics.setGiftHours(BigDecimal.ZERO);
            statistics.setUsageRate(BigDecimal.ZERO);
            statistics.setWarningAccountCount(0);
        } else {
            // 计算使用率
            if (statistics.getTotalHours().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal usageRate = statistics.getUsedHours()
                        .divide(statistics.getTotalHours(), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                statistics.setUsageRate(usageRate);
            } else {
                statistics.setUsageRate(BigDecimal.ZERO);
            }
        }
        return statistics;
    }

    @Override
    public ClassHourStatisticsVO statisticsByCourse(Long courseId) {
        ClassHourStatisticsVO statistics = baseMapper.statisticsByCourse(courseId);
        if (statistics == null) {
            statistics = new ClassHourStatisticsVO();
            statistics.setDimension("course");
            statistics.setDimensionId(courseId);
            statistics.setAccountCount(0);
            statistics.setTotalHours(BigDecimal.ZERO);
            statistics.setUsedHours(BigDecimal.ZERO);
            statistics.setRemainingHours(BigDecimal.ZERO);
            statistics.setGiftHours(BigDecimal.ZERO);
            statistics.setUsageRate(BigDecimal.ZERO);
            statistics.setWarningAccountCount(0);
        } else {
            // 计算使用率
            if (statistics.getTotalHours().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal usageRate = statistics.getUsedHours()
                        .divide(statistics.getTotalHours(), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                statistics.setUsageRate(usageRate);
            } else {
                statistics.setUsageRate(BigDecimal.ZERO);
            }
        }
        return statistics;
    }

    @Override
    public ClassHourStatisticsVO statisticsByCampus(Long campusId) {
        ClassHourStatisticsVO statistics = baseMapper.statisticsByCampus(campusId);
        if (statistics == null) {
            statistics = new ClassHourStatisticsVO();
            statistics.setDimension("campus");
            statistics.setDimensionId(campusId);
            statistics.setAccountCount(0);
            statistics.setTotalHours(BigDecimal.ZERO);
            statistics.setUsedHours(BigDecimal.ZERO);
            statistics.setRemainingHours(BigDecimal.ZERO);
            statistics.setGiftHours(BigDecimal.ZERO);
            statistics.setUsageRate(BigDecimal.ZERO);
            statistics.setWarningAccountCount(0);
        } else {
            // 计算使用率
            if (statistics.getTotalHours().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal usageRate = statistics.getUsedHours()
                        .divide(statistics.getTotalHours(), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                statistics.setUsageRate(usageRate);
            } else {
                statistics.setUsageRate(BigDecimal.ZERO);
            }
        }
        return statistics;
    }

    /**
     * 转换为余额VO
     */
    private ClassHourBalanceVO convertToBalanceVO(ClassHourAccount account) {
        ClassHourBalanceVO vo = new ClassHourBalanceVO();
        vo.setAccountId(account.getId());
        vo.setStudentId(account.getStudentId());
        vo.setCourseId(account.getCourseId());
        vo.setTotalHours(account.getTotalHours());
        vo.setUsedHours(account.getUsedHours());
        vo.setRemainingHours(account.getRemainingHours());
        vo.setGiftHours(account.getGiftHours());
        vo.setFrozenHours(BigDecimal.ZERO); // TODO: 实现冻结课时逻辑
        vo.setAvailableHours(account.getRemainingHours());
        vo.setStatus(account.getStatus());
        vo.setStatusDesc(getStatusDesc(account.getStatus()));
        vo.setIsWarning(false);
        vo.setCreateTime(account.getCreateTime());
        vo.setUpdateTime(account.getUpdateTime());
        // TODO: 填充学员姓名、课程名称等关联信息
        return vo;
    }

    /**
     * 获取状态描述
     */
    private String getStatusDesc(String status) {
        switch (status) {
            case "active":
                return "正常";
            case "frozen":
                return "冻结";
            case "exhausted":
                return "已用完";
            default:
                return status;
        }
    }
}
