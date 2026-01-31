package com.edu.finance.service.impl;

import com.edu.finance.domain.dto.ClassHourReportQueryDTO;
import com.edu.finance.domain.dto.RevenueReportQueryDTO;
import com.edu.finance.domain.vo.*;
import com.edu.finance.mapper.FinanceReportMapper;
import com.edu.finance.service.FinanceReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 财务报表服务实现
 */
@Service
@RequiredArgsConstructor
public class FinanceReportServiceImpl implements FinanceReportService {

    private final FinanceReportMapper financeReportMapper;

    @Override
    public RevenueSummaryVO getRevenueSummary(RevenueReportQueryDTO query) {
        RevenueSummaryVO summary = financeReportMapper.selectRevenueSummary(query);
        if (summary == null) {
            summary = new RevenueSummaryVO();
            summary.setTotalRevenue(BigDecimal.ZERO);
            summary.setTotalContracts(0);
            summary.setTotalStudents(0);
            summary.setAvgOrderAmount(BigDecimal.ZERO);
            summary.setCashRevenue(BigDecimal.ZERO);
            summary.setTransferRevenue(BigDecimal.ZERO);
            summary.setOnlineRevenue(BigDecimal.ZERO);
            summary.setGrowthRate(BigDecimal.ZERO);
        }
        return summary;
    }

    @Override
    public List<RevenueReportVO> getRevenueByTime(RevenueReportQueryDTO query) {
        List<RevenueReportVO> list = financeReportMapper.selectRevenueByTime(query);
        calculatePercentage(list);
        return list;
    }

    @Override
    public List<RevenueReportVO> getRevenueByCampus(RevenueReportQueryDTO query) {
        List<RevenueReportVO> list = financeReportMapper.selectRevenueByCampus(query);
        calculatePercentage(list);
        return list;
    }

    @Override
    public List<RevenueReportVO> getRevenueByCourseType(RevenueReportQueryDTO query) {
        List<RevenueReportVO> list = financeReportMapper.selectRevenueByCourseType(query);
        calculatePercentage(list);
        return list;
    }

    @Override
    public List<RevenueReportVO> getRevenueByPaymentMethod(RevenueReportQueryDTO query) {
        List<RevenueReportVO> list = financeReportMapper.selectRevenueByPaymentMethod(query);
        calculatePercentage(list);
        return list;
    }

    @Override
    public List<ClassHourTrendVO> getClassHourTrend(ClassHourReportQueryDTO query) {
        return financeReportMapper.selectClassHourTrend(query);
    }

    @Override
    public List<ClassHourReportVO> getClassHourByClass(ClassHourReportQueryDTO query) {
        List<ClassHourReportVO> list = financeReportMapper.selectClassHourByClass(query);
        calculateConsumptionRate(list);
        return list;
    }

    @Override
    public List<ClassHourReportVO> getClassHourByCourse(ClassHourReportQueryDTO query) {
        List<ClassHourReportVO> list = financeReportMapper.selectClassHourByCourse(query);
        calculateConsumptionRate(list);
        return list;
    }

    @Override
    public List<ClassHourReportVO> getClassHourByTeacher(ClassHourReportQueryDTO query) {
        List<ClassHourReportVO> list = financeReportMapper.selectClassHourByTeacher(query);
        calculateConsumptionRate(list);
        return list;
    }

    @Override
    public ClassHourSummaryVO getClassHourSummary(ClassHourReportQueryDTO query) {
        ClassHourSummaryVO summary = financeReportMapper.selectClassHourSummary(query);
        if (summary == null) {
            summary = new ClassHourSummaryVO();
            summary.setTotalHours(BigDecimal.ZERO);
            summary.setUsedHours(BigDecimal.ZERO);
            summary.setRemainingHours(BigDecimal.ZERO);
            summary.setUsageRate(BigDecimal.ZERO);
            summary.setWarningAccounts(0);
        }
        return summary;
    }

    /**
     * 计算占比
     */
    private void calculatePercentage(List<RevenueReportVO> list) {
        if (list == null || list.isEmpty()) {
            return;
        }

        BigDecimal total = list.stream()
                .map(RevenueReportVO::getRevenueAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (total.compareTo(BigDecimal.ZERO) > 0) {
            list.forEach(item -> {
                BigDecimal percentage = item.getRevenueAmount()
                        .multiply(BigDecimal.valueOf(100))
                        .divide(total, 2, RoundingMode.HALF_UP);
                item.setPercentage(percentage);
            });
        }
    }

    /**
     * 计算课消率
     */
    private void calculateConsumptionRate(List<ClassHourReportVO> list) {
        if (list == null || list.isEmpty()) {
            return;
        }

        list.forEach(item -> {
            if (item.getTotalHours() != null && item.getTotalHours().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal rate = item.getConsumedHours()
                        .multiply(BigDecimal.valueOf(100))
                        .divide(item.getTotalHours(), 2, RoundingMode.HALF_UP);
                item.setConsumptionRate(rate);
            } else {
                item.setConsumptionRate(BigDecimal.ZERO);
            }
        });
    }
}
