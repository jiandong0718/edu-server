package com.edu.teaching.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.teaching.domain.entity.LeaveRequest;

/**
 * 请假申请服务接口
 */
public interface LeaveRequestService extends IService<LeaveRequest> {

    /**
     * 分页查询请假申请列表
     */
    IPage<LeaveRequest> getLeaveRequestPage(IPage<LeaveRequest> page, LeaveRequest query);

    /**
     * 提交请假申请
     */
    boolean submitLeaveRequest(LeaveRequest leaveRequest);

    /**
     * 审批请假申请
     */
    boolean approve(Long id, boolean approved, String remark);

    /**
     * 取消请假申请
     */
    boolean cancel(Long id);

    /**
     * 安排补课
     */
    boolean arrangeMakeup(Long id, Long makeupScheduleId);

    /**
     * 生成请假单号
     */
    String generateLeaveNo();
}
