package com.edu.system.mapper;

import com.edu.system.domain.vo.TeachingDashboardVO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 教学数据看板 Mapper
 */
public interface TeachingDashboardMapper {

    /**
     * 统计在读学员数
     */
    int countActiveStudents(@Param("campusId") Long campusId);

    /**
     * 统计试听学员数
     */
    int countTrialStudents(@Param("campusId") Long campusId);

    /**
     * 统计潜在学员数
     */
    int countPotentialStudents(@Param("campusId") Long campusId);

    /**
     * 统计班级总数
     */
    int countTotalClasses(@Param("campusId") Long campusId);

    /**
     * 统计进行中班级数
     */
    int countOngoingClasses(@Param("campusId") Long campusId);

    /**
     * 统计已满员班级数
     */
    int countFullClasses(@Param("campusId") Long campusId);

    /**
     * 统计教师总数
     */
    int countTotalTeachers(@Param("campusId") Long campusId);

    /**
     * 统计全职教师数
     */
    int countFullTimeTeachers(@Param("campusId") Long campusId);

    /**
     * 统计兼职教师数
     */
    int countPartTimeTeachers(@Param("campusId") Long campusId);

    /**
     * 计算平均出勤率
     */
    TeachingDashboardVO.Overview getOverviewStats(@Param("campusId") Long campusId);

    /**
     * 获取考勤率趋势
     */
    List<TeachingDashboardVO.AttendanceRateItem> getAttendanceRateTrend(
            @Param("campusId") Long campusId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * 获取班级统计
     */
    List<TeachingDashboardVO.ClassStatsItem> getClassStats(
            @Param("campusId") Long campusId,
            @Param("status") String status
    );

    /**
     * 获取教师统计
     */
    List<TeachingDashboardVO.TeacherStatsItem> getTeacherStats(@Param("campusId") Long campusId);

    /**
     * 获取课程消耗统计
     */
    List<TeachingDashboardVO.CourseConsumptionItem> getCourseConsumption(
            @Param("campusId") Long campusId,
            @Param("limit") Integer limit
    );

    /**
     * 获取班级状态分布
     */
    List<TeachingDashboardVO.ClassStatusDistribution> getClassStatusDistribution(@Param("campusId") Long campusId);
}
