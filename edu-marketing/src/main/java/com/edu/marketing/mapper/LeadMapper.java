package com.edu.marketing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.edu.marketing.domain.entity.Lead;
import org.apache.ibatis.annotations.Param;

/**
 * 线索 Mapper
 */
public interface LeadMapper extends BaseMapper<Lead> {

    /**
     * 分页查询线索列表
     */
    IPage<Lead> selectLeadPage(IPage<Lead> page, @Param("query") Lead query);
}
