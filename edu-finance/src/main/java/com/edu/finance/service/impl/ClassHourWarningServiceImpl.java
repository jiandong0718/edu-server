package com.edu.finance.service.impl;

import com.edu.finance.domain.dto.ClassHourWarningQueryDTO;
import com.edu.finance.domain.vo.ClassHourWarningVO;
import com.edu.finance.event.ClassHourWarningEvent;
import com.edu.finance.mapper.ClassHourAccountMapper;
import com.edu.finance.service.ClassHourWarningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 课时预警服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClassHourWarningServiceImpl implements ClassHourWarningService {

    private final ClassHourAccountMapper classHourAccountMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public List<ClassHourWarningVO> getWarningList(ClassHourWarningQueryDTO query) {
        // 设置默认阈值
        if (query.getLowBalanceThreshold() == null) {
            query.setLowBalanceThreshold(new BigDecimal("5"));
        }
        if (query.getExpiringDaysThreshold() == null) {
            query.setExpiringDaysThreshold(30);
        }

        return classHourAccountMapper.getWarningList(query);
    }

    @Override
    public int checkAndSendWarnings() {
        log.info("开始检查课时预警...");

        // 查询所有预警账户
        ClassHourWarningQueryDTO query = new ClassHourWarningQueryDTO();
        query.setLowBalanceThreshold(new BigDecimal("5"));
        query.setExpiringDaysThreshold(30);

        List<ClassHourWarningVO> warnings = classHourAccountMapper.getWarningList(query);

        // 发送预警事件
        for (ClassHourWarningVO warning : warnings) {
            try {
                ClassHourWarningEvent event = new ClassHourWarningEvent(
                    this,
                    warning.getWarningType(),
                    warning.getAccountId(),
                    warning.getStudentId(),
                    warning.getCourseId(),
                    warning.getRemainingHours(),
                    query.getLowBalanceThreshold(),
                    warning.getWarningMessage(),
                    true
                );
                eventPublisher.publishEvent(event);
                log.info("发送课时预警事件: 学员={}, 课程={}, 剩余课时={}",
                    warning.getStudentName(), warning.getCourseName(), warning.getRemainingHours());
            } catch (Exception e) {
                log.error("发送课时预警事件失败: accountId={}", warning.getAccountId(), e);
            }
        }

        log.info("课时预警检查完成，共发现{}个预警", warnings.size());
        return warnings.size();
    }
}
