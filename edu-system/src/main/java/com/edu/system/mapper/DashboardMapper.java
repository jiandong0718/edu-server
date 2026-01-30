package com.edu.system.mapper;

import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 数据看板统计 Mapper
 */
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
     * 统计本月收入
     */
    BigDecimal sumIncomeThisMonth(@Param("campusId") Long campusId);

    /**
     * 统计本月退费
     */
    BigDecimal sumRefundThisMonth(@Param("campusId") Long campusId);

    /**
     * 统计待收款金额
     */
    BigDecimal sumPendingAmount(@Param("campusId") Long campusId);

    /**
     * 统计合同数
     */
    int countContracts(@Param("campusId") Long campusId);

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
     * 统计本月新增线索数
     */
    int countNewLeadsThisMonth(@Param("campusId") Long campusId);

    /**
     * 统计本月转化数
     */
    int countConvertedThisMonth(@Param("campusId") Long campusId);

    /**
     * 线索来源分布
     */
    List<Map<String, Object>> getLeadSourceDistribution(@Param("campusId") Long campusId);
}
