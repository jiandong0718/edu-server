package com.edu.finance.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.excel.EasyExcel;
import com.edu.common.exception.BusinessException;
import com.edu.finance.domain.dto.ProfitAnalysisQueryDTO;
import com.edu.finance.domain.vo.*;
import com.edu.finance.mapper.ProfitAnalysisReportMapper;
import com.edu.finance.service.ProfitAnalysisReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 利润分析报表服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProfitAnalysisReportServiceImpl implements ProfitAnalysisReportService {

    private final ProfitAnalysisReportMapper profitAnalysisReportMapper;

    @Override
    public ProfitAnalysisOverviewVO getProfitAnalysisOverview(ProfitAnalysisQueryDTO query) {
        validateQuery(query);
        ProfitAnalysisOverviewVO result = profitAnalysisReportMapper.selectProfitAnalysisOverview(query);
        if (result == null) {
            result = new ProfitAnalysisOverviewVO();
            result.setTotalRevenue(java.math.BigDecimal.ZERO);
            result.setTotalCost(java.math.BigDecimal.ZERO);
            result.setGrossProfit(java.math.BigDecimal.ZERO);
            result.setGrossProfitMargin(java.math.BigDecimal.ZERO);
            result.setContractCount(0);
            result.setStudentCount(0);
            result.setAvgContractAmount(java.math.BigDecimal.ZERO);
        }
        return result;
    }

    @Override
    public List<CampusProfitAnalysisVO> getCampusProfitAnalysis(ProfitAnalysisQueryDTO query) {
        validateQuery(query);
        return profitAnalysisReportMapper.selectCampusProfitAnalysis(query);
    }

    @Override
    public List<CourseTypeProfitAnalysisVO> getCourseTypeProfitAnalysis(ProfitAnalysisQueryDTO query) {
        validateQuery(query);
        return profitAnalysisReportMapper.selectCourseTypeProfitAnalysis(query);
    }

    @Override
    public List<ProfitTrendVO> getProfitTrend(ProfitAnalysisQueryDTO query) {
        validateQuery(query);

        // 根据日期范围决定按月还是按日统计
        long daysBetween = ChronoUnit.DAYS.between(query.getStartDate(), query.getEndDate());

        if (daysBetween > 90) {
            // 超过90天，按月统计
            return profitAnalysisReportMapper.selectProfitTrendByMonth(query);
        } else {
            // 90天以内，按日统计
            return profitAnalysisReportMapper.selectProfitTrendByDay(query);
        }
    }

    @Override
    public byte[] exportProfitAnalysis(ProfitAnalysisQueryDTO query) {
        try {
            validateQuery(query);

            // 查询数据
            ProfitAnalysisOverviewVO overview = getProfitAnalysisOverview(query);
            List<CampusProfitAnalysisVO> campusData = getCampusProfitAnalysis(query);
            List<CourseTypeProfitAnalysisVO> courseTypeData = getCourseTypeProfitAnalysis(query);
            List<ProfitTrendVO> trendData = getProfitTrend(query);

            // 使用EasyExcel导出多个sheet
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            EasyExcel.write(outputStream)
                    .sheet("概览")
                    .head(ProfitAnalysisOverviewVO.class)
                    .doWrite(java.util.Collections.singletonList(overview));

            EasyExcel.write(outputStream)
                    .sheet("按校区分析")
                    .head(CampusProfitAnalysisVO.class)
                    .doWrite(campusData);

            EasyExcel.write(outputStream)
                    .sheet("按课程类型分析")
                    .head(CourseTypeProfitAnalysisVO.class)
                    .doWrite(courseTypeData);

            EasyExcel.write(outputStream)
                    .sheet("趋势分析")
                    .head(ProfitTrendVO.class)
                    .doWrite(trendData);

            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("导出利润分析报表失败", e);
            throw new BusinessException("导出失败: " + e.getMessage());
        }
    }

    /**
     * 验证查询参数
     */
    private void validateQuery(ProfitAnalysisQueryDTO query) {
        if (query.getStartDate() == null || query.getEndDate() == null) {
            throw new BusinessException("开始日期和结束日期不能为空");
        }
        if (query.getStartDate().isAfter(query.getEndDate())) {
            throw new BusinessException("开始日期不能晚于结束日期");
        }
    }
}
