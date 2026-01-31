package com.edu.marketing.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.marketing.domain.entity.FollowUp;

/**
 * 跟进记录 Mapper
 */
@DS("marketing")
public interface FollowUpMapper extends BaseMapper<FollowUp> {
}
