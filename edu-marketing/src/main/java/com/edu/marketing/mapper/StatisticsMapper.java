package com.edu.marketing.mapper;

import com.edu.marketing.domain.dto.AdvisorPerformanceQueryDTO;
import com.edu.marketing.domain.dto.ConversionFunnelQueryDTO;
import com.edu.marketing.domain.vo.AdvisorPerformanceVO;
import com.edu.marketing.domain.vo.ConversionFunnelVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 统计分析 Mapper
 *
 * @author edu
 * @since 2024-01-31
 */
public interface StatisticsMapper {

    /**
     * 查询招生转化漏斗统计
     *
     * @param query 查询条件
     * @return 转化漏斗统计数据
     */
    ConversionFunnelVO selectConversionFunnel(@Param("query") ConversionFunnelQueryDTO query);

    /**
     * 查询顾问业绩统计列表
     *
     * @param query 查询条件
     * @return 顾问业绩统计列表
     */
    List<AdvisorPerformanceVO> selectAdvisorPerformanceList(@Param("query") AdvisorPerformanceQueryDTO query);
}
