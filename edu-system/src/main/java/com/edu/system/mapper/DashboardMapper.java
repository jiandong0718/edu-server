package com.edu.system.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 数据看板统计 Mapper
 */
@DS("system")
public interface DashboardMapper {

    // ========== 学员统计 ==========

    /**
     * 统计学员总数
     */
    int countStudents(@Param("campusId") Long campusId);

    /**
     * 按状态统计学员数
     */
    int countStudentsByStatus(@Param("campusId") Long campusId, @Param("status") String status);

    /**
     * 统计本月新增学员数
     */
    int countNewStudentsThisMonth(@Param("campusId") Long campusId);

    /**
     * 学员状态分布
     */
    List<Map<String, Object>> getStudentStatusDistribution(@Param("campusId") Long campusId);

    // ========== 财务统计 ==========

    /**
     * 统计今日收入
     */
    BigDecimal sumIncomeToday(@Param("campusId") Long campusId);

    /**
     * 统计本周收入
     */
    BigDecimal sumIncomeThisWeek(@Param("campusId") Long campusId);

    /**
     * 统计本月收入
     */
    BigDecimal sumIncomeThisMonth(@Param("campusId") Long campusId);

    /**
     * 统计本年收入
     */
    BigDecimal sumIncomeThisYear(@Param("campusId") Long campusId);

    /**
     * 统计本月退费
     */
    BigDecimal sumRefundThisMonth(@Param("campusId") Long campusId);

    /**
     * 统计待收款金额
     */
    BigDecimal sumPendingAmount(@Param("campusId") Long campusId);

    /**
     * 统计逾期欠费金额
     */
    BigDecimal sumOverdueAmount(@Param("campusId") Long campusId);

    /**
     * 统计合同数
     */
    int countContracts(@Param("campusId") Long campusId);

    /**
     * 收款方式分布
     */
    List<Map<String, Object>> getPaymentMethodDistribution(@Param("campusId") Long campusId);

    /**
     * 近N天收入趋势
     */
    List<Map<String, Object>> getIncomeTrend(@Param("campusId") Long campusId, @Param("days") int days);

    // ========== 教学统计 ==========

    /**
     * 统计指定日期的课节数
     */
    int countSchedulesByDate(@Param("campusId") Long campusId, @Param("date") LocalDate date);

    /**
     * 统计日期范围内的课节数
     */
    int countSchedulesByDateRange(@Param("campusId") Long campusId,
                                   @Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate);

    /**
     * 统计班级数
     */
    int countClasses(@Param("campusId") Long campusId);

    /**
     * 按状态统计班级数
     */
    int countClassesByStatus(@Param("campusId") Long campusId, @Param("status") String status);

    /**
     * 统计教师数
     */
    int countTeachers(@Param("campusId") Long campusId);

    /**
     * 按状态统计教师数
     */
    int countTeachersByStatus(@Param("campusId") Long campusId, @Param("status") String status);

    /**
     * 统计课程数
     */
    int countCourses(@Param("campusId") Long campusId);

    /**
     * 计算出勤率
     */
    Double calculateAttendanceRate(@Param("campusId") Long campusId,
                                    @Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);

    // ========== 营销统计 ==========

    /**
     * 统计线索数
     */
    int countLeads(@Param("campusId") Long campusId);

    /**
     * 按状态统计线索数
     */
    int countLeadsByStatus(@Param("campusId") Long campusId, @Param("status") String status);

    /**
     * 统计本月新增线索数
     */
    int countNewLeadsThisMonth(@Param("campusId") Long campusId);

    /**
     * 统计本月转化数
     */
    int countConvertedThisMonth(@Param("campusId") Long campusId);

    /**
     * 统计试听总数
     */
    int countTrials(@Param("campusId") Long campusId);

    /**
     * 统计本月试听数
     */
    int countTrialsThisMonth(@Param("campusId") Long campusId);

    /**
     * 统计试听转化数
     */
    int countTrialConverted(@Param("campusId") Long campusId);

    /**
     * 线索来源分布
     */
    List<Map<String, Object>> getLeadSourceDistribution(@Param("campusId") Long campusId);

    /**
     * 线索趋势（近N天）
     */
    List<Map<String, Object>> getLeadTrend(@Param("campusId") Long campusId, @Param("days") int days);

    /**
     * 转化趋势（近N天）
     */
    List<Map<String, Object>> getConversionTrend(@Param("campusId") Long campusId, @Param("days") int days);
}
