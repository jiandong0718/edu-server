package com.edu.finance.service;

import com.edu.finance.domain.vo.ClassHourConsumptionVO;
import com.edu.finance.domain.vo.ClassHourStatisticsVO;
import com.edu.finance.domain.vo.ClassHourSummaryVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 课时统计服务接口
 */
public interface ClassHourStatisticsService {

    /**
     * 课时汇总统计
     *
     * @param campusId 校区ID（可选）
     * @return 汇总统计
     */
    ClassHourSummaryVO getSummary(Long campusId);

    /**
     * 按课程统计
     *
     * @param campusId 校区ID（可选）
     * @return 课程统计列表
     */
    List<ClassHourStatisticsVO> statisticsByCourse(Long campusId);

    /**
     * 按学员统计
     *
     * @param campusId 校区ID（可选）
     * @return 学员统计列表
     */
    List<ClassHourStatisticsVO> statisticsByStudent(Long campusId);

    /**
     * 消课统计
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param campusId  校区ID（可选）
     * @return 消课统计列表
     */
    List<ClassHourConsumptionVO> statisticsConsumption(LocalDate startDate, LocalDate endDate, Long campusId);
}
