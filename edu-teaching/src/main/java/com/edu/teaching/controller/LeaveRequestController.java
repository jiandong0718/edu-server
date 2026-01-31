package com.edu.teaching.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.Result;
import com.edu.teaching.domain.dto.LeaveApprovalDTO;
import com.edu.teaching.domain.dto.LeaveRequestDTO;
import com.edu.teaching.domain.entity.LeaveRequest;
import com.edu.teaching.service.LeaveRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 请假管理控制器
 */
@RestController
@RequestMapping("/teaching/leave")
@RequiredArgsConstructor
@Tag(name = "请假管理", description = "学员请假申请和审批接口")
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    /**
     * 分页查询请假申请列表
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询请假申请列表")
    public Result<IPage<LeaveRequest>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "查询条件") LeaveRequest query) {
        IPage<LeaveRequest> page = new Page<>(pageNum, pageSize);
        return Result.success(leaveRequestService.getLeaveRequestPage(page, query));
    }

    /**
     * 获取请假申请详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取请假申请详情")
    public Result<LeaveRequest> getById(
            @Parameter(description = "请假申请ID") @PathVariable Long id) {
        return Result.success(leaveRequestService.getById(id));
    }

    /**
     * 提交请假申请
     */
    @PostMapping
    @Operation(summary = "提交请假申请")
    public Result<Void> submit(@Valid @RequestBody LeaveRequestDTO dto) {
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setStudentId(dto.getStudentId());
        leaveRequest.setScheduleId(dto.getScheduleId());
        leaveRequest.setClassId(dto.getClassId());
        leaveRequest.setCampusId(dto.getCampusId());
        leaveRequest.setType(dto.getType());
        leaveRequest.setStartDate(dto.getStartDate());
        leaveRequest.setEndDate(dto.getEndDate());
        leaveRequest.setReason(dto.getReason());
        leaveRequest.setNeedMakeup(dto.getNeedMakeup());
        leaveRequest.setRemark(dto.getRemark());

        leaveRequestService.submitLeaveRequest(leaveRequest);
        return Result.success();
    }

    /**
     * 审批请假申请
     */
    @PutMapping("/{id}/approve")
    @Operation(summary = "审批请假申请")
    public Result<Void> approve(
            @Parameter(description = "请假申请ID") @PathVariable Long id,
            @Parameter(description = "审批状态") @RequestParam String status,
            @Parameter(description = "审批意见") @RequestParam(required = false) String remark) {
        boolean approved = "approved".equals(status);
        leaveRequestService.approve(id, approved, remark);
        return Result.success();
    }

    /**
     * 取消请假申请
     */
    @PutMapping("/{id}/cancel")
    @Operation(summary = "取消请假申请")
    public Result<Void> cancel(
            @Parameter(description = "请假申请ID") @PathVariable Long id) {
        leaveRequestService.cancel(id);
        return Result.success();
    }

    /**
     * 安排补课
     */
    @PutMapping("/{id}/makeup")
    @Operation(summary = "为请假安排补课")
    public Result<Void> arrangeMakeup(
            @Parameter(description = "请假申请ID") @PathVariable Long id,
            @Parameter(description = "补课排课ID") @RequestParam Long makeupScheduleId) {
        leaveRequestService.arrangeMakeup(id, makeupScheduleId);
        return Result.success();
    }
}
