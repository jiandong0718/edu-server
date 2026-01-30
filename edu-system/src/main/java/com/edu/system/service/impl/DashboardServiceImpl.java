package com.edu.system.service.impl;

import com.edu.system.domain.vo.DashboardVO;
import com.edu.system.mapper.DashboardMapper;
import com.edu.system.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

/**
 * 数据看板服务实现
 */
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final DashboardMapper dashboardMapper;

    @Override
    public DashboardVO getDashboard(Long campusId) {
        DashboardVO dashboard = new DashboardVO();
        dashboard.setStudentStats(getStudentStats(campusId));
        dashboard.setFinanceStats(getFinanceStats(campusId));
        dashboard.setTeachingStats(getTeachingStats(campusId));
        dashboard.setMarketingStats(getMarketingStats(campusId));
        return dashboard;
    }

    @Override
    public DashboardVO.StudentStats getStudentStats(Long campusId) {
        DashboardVO.StudentStats stats = new DashboardVO.StudentStats();

        stats.setTotalCount(dashboardMapper.countStudents(campusId));
        stats.setEnrolledCount(dashboardMapper.countStudentsByStatus(campusId, "enrolled"));
        stats.setPotentialCount(dashboardMapper.countStudentsByStatus(campusId, "potential"));
        stats.setNewCountThisMonth(dashboardMapper.countNewStudentsThisMonth(campusId));
        stats.setStatusDistribution(dashboardMapper.getStudentStatusDistribution(campusId));

        return stats;
    }

    @Override
    public DashboardVO.FinanceStats getFinanceStats(Long campusId) {
        DashboardVO.FinanceStats stats = new DashboardVO.FinanceStats();

        BigDecimal income = dashboardMapper.sumIncomeThisMonth(campusId);
        BigDecimal refund = dashboardMapper.sumRefundThisMonth(campusId);
        BigDecimal pending = dashboardMapper.sumPendingAmount(campusId);

        stats.setIncomeThisMonth(income != null ? income : BigDecimal.ZERO);
        stats.setRefundThisMonth(refund != null ? refund : BigDecimal.ZERO);
        stats.setPendingAmount(pending != null ? pending : BigDecimal.ZERO);
        stats.setContractCount(dashboardMapper.countContracts(campusId));
        stats.setIncomeTrend(dashboardMapper.getIncomeTrend(campusId, 7));

        return stats;
    }

    @Override
    public DashboardVO.TeachingStats getTeachingStats(Long campusId) {
        DashboardVO.TeachingStats stats = new DashboardVO.TeachingStats();

        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        stats.setTodayScheduleCount(dashboardMapper.countSchedulesByDate(campusId, today));
        stats.setWeekScheduleCount(dashboardMapper.countSchedulesByDateRange(campusId, weekStart, weekEnd));
        stats.setClassCount(dashboardMapper.countClasses(campusId));
        stats.setOngoingClassCount(dashboardMapper.countClassesByStatus(campusId, "ongoing"));
        stats.setTeacherCount(dashboardMapper.countTeachers(campusId));

        Double attendanceRate = dashboardMapper.calculateAttendanceRate(campusId, weekStart, weekEnd);
        stats.setAttendanceRate(attendanceRate != null ? attendanceRate : 0.0);

        return stats;
    }

    @Override
    public DashboardVO.MarketingStats getMarketingStats(Long campusId) {
        DashboardVO.MarketingStats stats = new DashboardVO.MarketingStats();

        stats.setLeadCount(dashboardMapper.countLeads(campusId));
        stats.setNewLeadThisMonth(dashboardMapper.countNewLeadsThisMonth(campusId));
        stats.setConvertedThisMonth(dashboardMapper.countConvertedThisMonth(campusId));

        // 计算转化率
        int newLeads = stats.getNewLeadThisMonth();
        int converted = stats.getConvertedThisMonth();
        stats.setConversionRate(newLeads > 0 ? (converted * 100.0 / newLeads) : 0.0);

        stats.setSourceDistribution(dashboardMapper.getLeadSourceDistribution(campusId));

        return stats;
    }
}
