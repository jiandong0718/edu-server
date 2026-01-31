package com.edu.marketing.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.edu.marketing.domain.entity.Lead;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 线索 Mapper
 */
@DS("marketing")
public interface LeadMapper extends BaseMapper<Lead> {

    /**
     * 分页查询线索列表
     */
    IPage<Lead> selectLeadPage(IPage<Lead> page, @Param("query") Lead query);

    /**
     * 查询校区内各顾问的线索数量
     *
     * @param campusId 校区ID
     * @return 顾问ID和线索数量列表
     */
    List<Map<String, Object>> selectAdvisorLeadCounts(@Param("campusId") Long campusId);
}
