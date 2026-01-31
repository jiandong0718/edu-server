package com.edu.finance.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.edu.finance.domain.dto.ProfitAnalysisQueryDTO;
import com.edu.finance.domain.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 利润分析报表 Mapper
 */
@Mapper
public interface ProfitAnalysisReportMapper {

    /**
     * 查询利润分析概览
     */
    ProfitAnalysisOverviewVO selectProfitAnalysisOverview(@Param("query") ProfitAnalysisQueryDTO query);

    /**
     * 按校区分析利润
     */
    List<CampusProfitAnalysisVO> selectCampusProfitAnalysis(@Param("query") ProfitAnalysisQueryDTO query);

    /**
     * 按课程类型分析利润
     */
    List<CourseTypeProfitAnalysisVO> selectCourseTypeProfitAnalysis(@Param("query") ProfitAnalysisQueryDTO query);

    /**
     * 查询利润趋势（按月）
     */
    List<ProfitTrendVO> selectProfitTrendByMonth(@Param("query") ProfitAnalysisQueryDTO query);

    /**
     * 查询利润趋势（按日）
     */
    List<ProfitTrendVO> selectProfitTrendByDay(@Param("query") ProfitAnalysisQueryDTO query);
}
