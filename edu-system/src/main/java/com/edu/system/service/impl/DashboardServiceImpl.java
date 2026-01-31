package com.edu.system.service.impl;

import com.edu.system.domain.vo.DashboardVO;
import com.edu.system.mapper.DashboardMapper;
import com.edu.system.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
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
    @Cacheable(value = "dashboard:student", key = "#campusId != null ? #campusId : 'all'", unless = "#result == null")
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
    @Cacheable(value = "dashboard:finance", key = "#campusId != null ? #campusId : 'all'", unless = "#result == null")
    public DashboardVO.FinanceStats getFinanceStats(Long campusId) {
        DashboardVO.FinanceStats stats = new DashboardVO.FinanceStats();

        BigDecimal incomeToday = dashboardMapper.sumIncomeToday(campusId);
        BigDecimal incomeWeek = dashboardMapper.sumIncomeThisWeek(campusId);
        BigDecimal incomeMonth = dashboardMapper.sumIncomeThisMonth(campusId);
        BigDecimal incomeYear = dashboardMapper.sumIncomeThisYear(campusId);
        BigDecimal refund = dashboardMapper.sumRefundThisMonth(campusId);
        BigDecimal pending = dashboardMapper.sumPendingAmount(campusId);
        BigDecimal overdue = dashboardMapper.sumOverdueAmount(campusId);

        stats.setIncomeToday(incomeToday != null ? incomeToday : BigDecimal.ZERO);
        stats.setIncomeThisWeek(incomeWeek != null ? incomeWeek : BigDecimal.ZERO);
        stats.setIncomeThisMonth(incomeMonth != null ? incomeMonth : BigDecimal.ZERO);
        stats.setIncomeThisYear(incomeYear != null ? incomeYear : BigDecimal.ZERO);
        stats.setRefundThisMonth(refund != null ? refund : BigDecimal.ZERO);
        stats.setPendingAmount(pending != null ? pending : BigDecimal.ZERO);
        stats.setOverdueAmount(overdue != null ? overdue : BigDecimal.ZERO);
        stats.setContractCount(dashboardMapper.countContracts(campusId));
        stats.setPaymentMethodDistribution(dashboardMapper.getPaymentMethodDistribution(campusId));
        stats.setIncomeTrend(dashboardMapper.getIncomeTrend(campusId, 30));

        return stats;
    }

    @Override
    @Cacheable(value = "dashboard:teaching", key = "#campusId != null ? #campusId : 'all'", unless = "#result == null")
    public DashboardVO.TeachingStats getTeachingStats(Long campusId) {
        DashboardVO.TeachingStats stats = new DashboardVO.TeachingStats();

        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        stats.setTodayScheduleCount(dashboardMapper.countSchedulesByDate(campusId, today));
        stats.setWeekScheduleCount(dashboardMapper.countSchedulesByDateRange(campusId, weekStart, weekEnd));
        stats.setClassCount(dashboardMapper.countClasses(campusId));
        stats.setOngoingClassCount(dashboardMapper.countClassesByStatus(campusId, "ongoing"));
        stats.setCompletedClassCount(dashboardMapper.countClassesByStatus(campusId, "completed"));
        stats.setTeacherCount(dashboardMapper.countTeachers(campusId));
        stats.setActiveTeacherCount(dashboardMapper.countTeachersByStatus(campusId, "active"));
        stats.setOnLeaveTeacherCount(dashboardMapper.countTeachersByStatus(campusId, "on_leave"));
        stats.setCourseCount(dashboardMapper.countCourses(campusId));

        // 学员统计
        stats.setStudentCount(dashboardMapper.countStudents(campusId));
        stats.setEnrolledStudentCount(dashboardMapper.countStudentsByStatus(campusId, "enrolled"));
        stats.setTrialStudentCount(dashboardMapper.countStudentsByStatus(campusId, "trial"));
        stats.setPotentialStudentCount(dashboardMapper.countStudentsByStatus(campusId, "potential"));

        Double attendanceRate = dashboardMapper.calculateAttendanceRate(campusId, weekStart, weekEnd);
        stats.setAttendanceRate(attendanceRate != null ? attendanceRate : 0.0);

        return stats;
    }

    @Override
    @Cacheable(value = "dashboard:marketing", key = "#campusId != null ? #campusId : 'all'", unless = "#result == null")
    public DashboardVO.MarketingStats getMarketingStats(Long campusId) {
        DashboardVO.MarketingStats stats = new DashboardVO.MarketingStats();

        stats.setLeadCount(dashboardMapper.countLeads(campusId));
        stats.setPendingLeadCount(dashboardMapper.countLeadsByStatus(campusId, "following"));
        stats.setConvertedLeadCount(dashboardMapper.countLeadsByStatus(campusId, "converted"));
        stats.setNewLeadThisMonth(dashboardMapper.countNewLeadsThisMonth(campusId));
        stats.setConvertedThisMonth(dashboardMapper.countConvertedThisMonth(campusId));

        // 计算转化率
        int newLeads = stats.getNewLeadThisMonth();
        int converted = stats.getConvertedThisMonth();
        stats.setConversionRate(newLeads > 0 ? (converted * 100.0 / newLeads) : 0.0);

        // 试听统计
        stats.setTrialCount(dashboardMapper.countTrials(campusId));
        stats.setTrialThisMonth(dashboardMapper.countTrialsThisMonth(campusId));
        stats.setTrialConvertedCount(dashboardMapper.countTrialConverted(campusId));

        // 试听转化率
        int totalTrials = stats.getTrialCount();
        int trialConverted = stats.getTrialConvertedCount();
        stats.setTrialConversionRate(totalTrials > 0 ? (trialConverted * 100.0 / totalTrials) : 0.0);

        stats.setSourceDistribution(dashboardMapper.getLeadSourceDistribution(campusId));
        stats.setLeadTrend(dashboardMapper.getLeadTrend(campusId, 30));
        stats.setConversionTrend(dashboardMapper.getConversionTrend(campusId, 30));

        return stats;
    }
}
