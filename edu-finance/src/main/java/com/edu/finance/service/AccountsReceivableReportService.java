package com.edu.finance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.finance.domain.dto.AccountsReceivableQueryDTO;
import com.edu.finance.domain.dto.CollectionReminderDTO;
import com.edu.finance.domain.vo.AccountsReceivableVO;
import com.edu.finance.domain.vo.AgingAnalysisVO;
import com.edu.finance.domain.vo.CampusArrearsStatisticsVO;

import java.util.List;

/**
 * 应收账款报表服务接口
 */
public interface AccountsReceivableReportService {

    /**
     * 分页查询应收账款列表
     *
     * @param query 查询条件
     * @return 应收账款分页数据
     */
    Page<AccountsReceivableVO> getAccountsReceivablePage(AccountsReceivableQueryDTO query);

    /**
     * 获取账龄分析
     *
     * @param campusId 校区ID（可选）
     * @return 账龄分析数据
     */
    AgingAnalysisVO getAgingAnalysis(Long campusId);

    /**
     * 按校区统计欠费情况
     *
     * @return 校区欠费统计列表
     */
    List<CampusArrearsStatisticsVO> getCampusArrearsStatistics();

    /**
     * 发送催缴提醒
     *
     * @param dto 催缴提醒DTO
     * @return 发送成功数量
     */
    Integer sendCollectionReminder(CollectionReminderDTO dto);

    /**
     * 导出应收账款报表
     *
     * @param query 查询条件
     * @return Excel文件字节数组
     */
    byte[] exportAccountsReceivable(AccountsReceivableQueryDTO query);
}
