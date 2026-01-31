package com.edu.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.finance.domain.dto.ArrearsQueryDTO;
import com.edu.finance.domain.entity.Payment;
import com.edu.finance.domain.vo.ArrearsRemindVO;
import com.edu.finance.domain.vo.ArrearsStatisticsVO;
import com.edu.finance.domain.vo.ArrearsVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 收款记录 Mapper
 */
public interface PaymentMapper extends BaseMapper<Payment> {

    /**
     * 分页查询欠费记录
     */
    Page<ArrearsVO> selectArrearsPage(Page<ArrearsVO> page, @Param("query") ArrearsQueryDTO query);

    /**
     * 查询欠费统计
     */
    ArrearsStatisticsVO selectArrearsStatistics(@Param("query") ArrearsQueryDTO query);

    /**
     * 查询需要提醒的欠费记录
     */
    List<ArrearsRemindVO> selectArrearsRemind(@Param("minDays") Integer minDays);
}
