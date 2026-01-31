package com.edu.finance.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.finance.domain.dto.AccountsReceivableQueryDTO;
import com.edu.finance.domain.vo.AccountsReceivableVO;
import com.edu.finance.domain.vo.AgingAnalysisVO;
import com.edu.finance.domain.vo.CampusArrearsStatisticsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 应收账款报表 Mapper
 */
@Mapper
public interface AccountsReceivableReportMapper {

    /**
     * 分页查询应收账款列表
     */
    Page<AccountsReceivableVO> selectAccountsReceivablePage(
            Page<AccountsReceivableVO> page,
            @Param("query") AccountsReceivableQueryDTO query
    );

    /**
     * 查询账龄分析
     */
    AgingAnalysisVO selectAgingAnalysis(@Param("campusId") Long campusId);

    /**
     * 按校区统计欠费情况
     */
    List<CampusArrearsStatisticsVO> selectCampusArrearsStatistics();

    /**
     * 查询应收账款列表（用于导出）
     */
    List<AccountsReceivableVO> selectAccountsReceivableList(@Param("query") AccountsReceivableQueryDTO query);
}
