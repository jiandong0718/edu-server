package com.edu.finance.service.impl;

import com.edu.finance.domain.vo.ClassHourConsumptionVO;
import com.edu.finance.domain.vo.ClassHourStatisticsVO;
import com.edu.finance.domain.vo.ClassHourSummaryVO;
import com.edu.finance.mapper.ClassHourAccountMapper;
import com.edu.finance.service.ClassHourStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

/**
 * 课时统计服务实现
 */
@Service
@RequiredArgsConstructor
public class ClassHourStatisticsServiceImpl implements ClassHourStatisticsService {

    private final ClassHourAccountMapper classHourAccountMapper;

    @Override
    public ClassHourSummaryVO getSummary(Long campusId) {
        ClassHourSummaryVO summary = classHourAccountMapper.getSummary(campusId);

        // 计算使用率
        if (summary != null && summary.getTotalHours() != null &&
            summary.getTotalHours().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal usageRate = summary.getUsedHours()
                .divide(summary.getTotalHours(), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);
            summary.setUsageRate(usageRate);
        }

        return summary;
    }

    @Override
    public List<ClassHourStatisticsVO> statisticsByCourse(Long campusId) {
        List<ClassHourStatisticsVO> statistics = classHourAccountMapper.statisticsListByCourse(campusId);

        // 计算使用率
        statistics.forEach(stat -> {
            if (stat.getTotalHours() != null && stat.getTotalHours().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal usageRate = stat.getUsedHours()
                    .divide(stat.getTotalHours(), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .setScale(2, RoundingMode.HALF_UP);
                stat.setUsageRate(usageRate);
            }
        });

        return statistics;
    }

    @Override
    public List<ClassHourStatisticsVO> statisticsByStudent(Long campusId) {
        List<ClassHourStatisticsVO> statistics = classHourAccountMapper.statisticsListByStudent(campusId);

        // 计算使用率
        statistics.forEach(stat -> {
            if (stat.getTotalHours() != null && stat.getTotalHours().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal usageRate = stat.getUsedHours()
                    .divide(stat.getTotalHours(), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .setScale(2, RoundingMode.HALF_UP);
                stat.setUsageRate(usageRate);
            }
        });

        return statistics;
    }

    @Override
    public List<ClassHourConsumptionVO> statisticsConsumption(LocalDate startDate, LocalDate endDate, Long campusId) {
        return classHourAccountMapper.statisticsConsumption(startDate, endDate, campusId);
    }
}
