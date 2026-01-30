package com.edu.teaching.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.Result;
import com.edu.teaching.domain.entity.LeaveRequest;
import com.edu.teaching.service.LeaveRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 请假管理控制器
 */
@RestController
@RequestMapping("/teaching/leave")
@RequiredArgsConstructor
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    /**
     * 分页查询请假申请列表
     */
    @GetMapping("/page")
    public Result<IPage<LeaveRequest>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            LeaveRequest query) {
        IPage<LeaveRequest> page = new Page<>(pageNum, pageSize);
        return Result.success(leaveRequestService.getLeaveRequestPage(page, query));
    }

    /**
     * 获取请假申请详情
     */
    @GetMapping("/{id}")
    public Result<LeaveRequest> getById(@PathVariable Long id) {
        return Result.success(leaveRequestService.getById(id));
    }

    /**
     * 提交请假申请
     */
    @PostMapping
    public Result<Void> submit(@RequestBody LeaveRequest leaveRequest) {
        leaveRequestService.submitLeaveRequest(leaveRequest);
        return Result.success();
    }

    /**
     * 审批请假申请
     */
    @PutMapping("/{id}/approve")
    public Result<Void> approve(
            @PathVariable Long id,
            @RequestParam boolean approved,
            @RequestParam(required = false) String remark) {
        leaveRequestService.approve(id, approved, remark);
        return Result.success();
    }

    /**
     * 取消请假申请
     */
    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        leaveRequestService.cancel(id);
        return Result.success();
    }

    /**
     * 安排补课
     */
    @PutMapping("/{id}/makeup")
    public Result<Void> arrangeMakeup(
            @PathVariable Long id,
            @RequestParam Long makeupScheduleId) {
        leaveRequestService.arrangeMakeup(id, makeupScheduleId);
        return Result.success();
    }
}
