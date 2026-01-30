package com.edu.teaching.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.edu.teaching.domain.entity.LeaveRequest;
import org.apache.ibatis.annotations.Param;

/**
 * 请假申请 Mapper
 */
public interface LeaveRequestMapper extends BaseMapper<LeaveRequest> {

    /**
     * 分页查询请假申请列表
     */
    IPage<LeaveRequest> selectLeaveRequestPage(IPage<LeaveRequest> page, @Param("query") LeaveRequest query);
}
