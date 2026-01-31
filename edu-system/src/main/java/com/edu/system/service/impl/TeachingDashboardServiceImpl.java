package com.edu.system.service.impl;

import com.edu.system.domain.vo.TeachingDashboardVO;
import com.edu.system.mapper.TeachingDashboardMapper;
import com.edu.system.service.TeachingDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * 教学数据看板服务实现
 */
@Service
@RequiredArgsConstructor
public class TeachingDashboardServiceImpl implements TeachingDashboardService {

    private final TeachingDashboardMapper teachingDashboardMapper;

    @Override
    @Cacheable(value = "teaching:dashboard:overview", key = "#campusId != null ? #campusId : 'all'", unless = "#result == null")
    public TeachingDashboardVO.Overview getOverview(Long campusId) {
        return teachingDashboardMapper.getOverviewStats(campusId);
    }

    @Override
    @Cacheable(value = "teaching:dashboard:attendance", key = "#campusId + ':' + #startDate + ':' + #endDate", unless = "#result == null || #result.isEmpty()")
    public List<TeachingDashboardVO.AttendanceRateItem> getAttendanceRateTrend(Long campusId, LocalDate startDate, LocalDate endDate) {
        // 如果没有指定日期范围，默认查询最近7天
        if (startDate == null || endDate == null) {
            endDate = LocalDate.now();
            startDate = endDate.minusDays(6);
        }
        return teachingDashboardMapper.getAttendanceRateTrend(campusId, startDate, endDate);
    }

    @Override
    @Cacheable(value = "teaching:dashboard:class", key = "#campusId + ':' + (#status != null ? #status : 'all')", unless = "#result == null || #result.isEmpty()")
    public List<TeachingDashboardVO.ClassStatsItem> getClassStats(Long campusId, String status) {
        return teachingDashboardMapper.getClassStats(campusId, status);
    }

    @Override
    @Cacheable(value = "teaching:dashboard:teacher", key = "#campusId != null ? #campusId : 'all'", unless = "#result == null || #result.isEmpty()")
    public List<TeachingDashboardVO.TeacherStatsItem> getTeacherStats(Long campusId) {
        return teachingDashboardMapper.getTeacherStats(campusId);
    }

    @Override
    @Cacheable(value = "teaching:dashboard:course", key = "#campusId + ':' + (#limit != null ? #limit : 10)", unless = "#result == null || #result.isEmpty()")
    public List<TeachingDashboardVO.CourseConsumptionItem> getCourseConsumption(Long campusId, Integer limit) {
        // 如果没有指定限制数量，默认返回前10条
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        return teachingDashboardMapper.getCourseConsumption(campusId, limit);
    }

    @Override
    @Cacheable(value = "teaching:dashboard:distribution", key = "#campusId != null ? #campusId : 'all'", unless = "#result == null || #result.isEmpty()")
    public List<TeachingDashboardVO.ClassStatusDistribution> getClassStatusDistribution(Long campusId) {
        return teachingDashboardMapper.getClassStatusDistribution(campusId);
    }
}
