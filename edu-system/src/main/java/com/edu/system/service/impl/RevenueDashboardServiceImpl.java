package com.edu.system.service.impl;

import com.edu.system.domain.vo.RevenueDashboardVO;
import com.edu.system.mapper.RevenueDashboardMapper;
import com.edu.system.service.RevenueDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 营收数据看板服务实现
 */
@Service
@RequiredArgsConstructor
public class RevenueDashboardServiceImpl implements RevenueDashboardService {

    private final RevenueDashboardMapper revenueDashboardMapper;

    @Override
    public RevenueDashboardVO.RevenueOverview getRevenueOverview(Long campusId) {
        RevenueDashboardVO.RevenueOverview overview = new RevenueDashboardVO.RevenueOverview();

        // 收入统计
        overview.setIncomeToday(revenueDashboardMapper.sumIncomeToday(campusId));
        overview.setIncomeThisWeek(revenueDashboardMapper.sumIncomeThisWeek(campusId));
        overview.setIncomeThisMonth(revenueDashboardMapper.sumIncomeThisMonth(campusId));
        overview.setIncomeThisYear(revenueDashboardMapper.sumIncomeThisYear(campusId));

        // 欠费统计
        overview.setTotalArrears(revenueDashboardMapper.sumTotalArrears(campusId));
        overview.setArrearsStudentCount(revenueDashboardMapper.countArrearsStudents(campusId));
        overview.setArrearsContractCount(revenueDashboardMapper.countArrearsContracts(campusId));

        // 退费统计
        BigDecimal refundThisMonth = revenueDashboardMapper.sumRefundThisMonth(campusId);
        overview.setRefundThisMonth(refundThisMonth);

        // 计算退费率
        BigDecimal incomeThisMonth = overview.getIncomeThisMonth();
        if (incomeThisMonth != null && incomeThisMonth.compareTo(BigDecimal.ZERO) > 0) {
            double refundRate = refundThisMonth.divide(incomeThisMonth, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
            overview.setRefundRate(refundRate);
        } else {
            overview.setRefundRate(0.0);
        }

        return overview;
    }

    @Override
    public List<RevenueDashboardVO.RevenueTrendItem> getRevenueTrend(Long campusId, Integer days) {
        if (days == null || days <= 0) {
            days = 30;
        }
        return revenueDashboardMapper.getRevenueTrend(campusId, days);
    }

    @Override
    public List<RevenueDashboardVO.PaymentMethodItem> getPaymentMethodDistribution(
            Long campusId, String startDate, String endDate) {
        List<RevenueDashboardVO.PaymentMethodItem> items =
                revenueDashboardMapper.getPaymentMethodDistribution(campusId, startDate, endDate);

        // 计算总金额
        BigDecimal totalAmount = items.stream()
                .map(RevenueDashboardVO.PaymentMethodItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 计算占比
        if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {
            items.forEach(item -> {
                double percentage = item.getAmount()
                        .divide(totalAmount, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .doubleValue();
                item.setPercentage(percentage);
            });
        }

        return items;
    }

    @Override
    public List<RevenueDashboardVO.ArrearsItem> getArrearsList(Long campusId, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 20;
        }
        return revenueDashboardMapper.getArrearsList(campusId, limit);
    }

    @Override
    public List<RevenueDashboardVO.CourseRevenueItem> getCourseRevenueRanking(
            Long campusId, String startDate, String endDate, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        return revenueDashboardMapper.getCourseRevenueRanking(campusId, startDate, endDate, limit);
    }
}
