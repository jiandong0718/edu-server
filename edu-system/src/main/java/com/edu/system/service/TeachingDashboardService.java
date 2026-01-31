package com.edu.system.service;

import com.edu.system.domain.vo.TeachingDashboardVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 教学数据看板服务接口
 */
public interface TeachingDashboardService {

    /**
     * 获取教学数据概览
     *
     * @param campusId 校区ID，为null时查询所有校区
     * @return 教学数据概览
     */
    TeachingDashboardVO.Overview getOverview(Long campusId);

    /**
     * 获取考勤率趋势
     *
     * @param campusId  校区ID，为null时查询所有校区
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 考勤率趋势列表
     */
    List<TeachingDashboardVO.AttendanceRateItem> getAttendanceRateTrend(Long campusId, LocalDate startDate, LocalDate endDate);

    /**
     * 获取班级统计
     *
     * @param campusId 校区ID，为null时查询所有校区
     * @param status   班级状态，为null时查询所有状态
     * @return 班级统计列表
     */
    List<TeachingDashboardVO.ClassStatsItem> getClassStats(Long campusId, String status);

    /**
     * 获取教师统计
     *
     * @param campusId 校区ID，为null时查询所有校区
     * @return 教师统计列表
     */
    List<TeachingDashboardVO.TeacherStatsItem> getTeacherStats(Long campusId);

    /**
     * 获取课程消耗统计
     *
     * @param campusId 校区ID，为null时查询所有校区
     * @param limit    返回数量限制，默认10
     * @return 课程消耗统计列表（按消耗率排序）
     */
    List<TeachingDashboardVO.CourseConsumptionItem> getCourseConsumption(Long campusId, Integer limit);

    /**
     * 获取班级状态分布
     *
     * @param campusId 校区ID，为null时查询所有校区
     * @return 班级状态分布列表
     */
    List<TeachingDashboardVO.ClassStatusDistribution> getClassStatusDistribution(Long campusId);
}
