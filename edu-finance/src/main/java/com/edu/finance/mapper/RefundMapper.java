package com.edu.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.edu.finance.domain.entity.Refund;
import org.apache.ibatis.annotations.Param;

/**
 * 退费申请 Mapper
 */
public interface RefundMapper extends BaseMapper<Refund> {

    /**
     * 分页查询退费申请列表
     */
    IPage<Refund> selectRefundPage(IPage<Refund> page, @Param("query") Refund query);
}
