package com.edu.finance.service;

import com.edu.finance.domain.dto.ProfitAnalysisQueryDTO;
import com.edu.finance.domain.vo.*;

import java.util.List;

/**
 * 利润分析报表服务接口
 */
public interface ProfitAnalysisReportService {

    /**
     * 获取利润分析概览
     *
     * @param query 查询条件
     * @return 利润分析概览
     */
    ProfitAnalysisOverviewVO getProfitAnalysisOverview(ProfitAnalysisQueryDTO query);

    /**
     * 按校区分析利润
     *
     * @param query 查询条件
     * @return 校区利润分析列表
     */
    List<CampusProfitAnalysisVO> getCampusProfitAnalysis(ProfitAnalysisQueryDTO query);

    /**
     * 按课程类型分析利润
     *
     * @param query 查询条件
     * @return 课程类型利润分析列表
     */
    List<CourseTypeProfitAnalysisVO> getCourseTypeProfitAnalysis(ProfitAnalysisQueryDTO query);

    /**
     * 获取利润趋势分析
     *
     * @param query 查询条件
     * @return 利润趋势列表
     */
    List<ProfitTrendVO> getProfitTrend(ProfitAnalysisQueryDTO query);

    /**
     * 导出利润分析报表
     *
     * @param query 查询条件
     * @return Excel文件字节数组
     */
    byte[] exportProfitAnalysis(ProfitAnalysisQueryDTO query);
}
