package com.edu.marketing.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.R;
import com.edu.marketing.domain.entity.FollowUp;
import com.edu.marketing.domain.entity.Lead;
import com.edu.marketing.service.LeadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 线索管理控制器
 */
@Tag(name = "线索管理")
@RestController
@RequestMapping("/marketing/lead")
@RequiredArgsConstructor
public class LeadController {

    private final LeadService leadService;

    @Operation(summary = "分页查询线索列表")
    @GetMapping("/page")
    public R<Page<Lead>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            Lead query) {
        Page<Lead> page = new Page<>(pageNum, pageSize);
        leadService.pageList(page, query);
        return R.ok(page);
    }

    @Operation(summary = "获取线索详情")
    @GetMapping("/{id}")
    public R<Lead> getById(@PathVariable Long id) {
        return R.ok(leadService.getById(id));
    }

    @Operation(summary = "创建线索")
    @PostMapping
    public R<Boolean> create(@RequestBody Lead lead) {
        return R.ok(leadService.createLead(lead));
    }

    @Operation(summary = "修改线索")
    @PutMapping
    public R<Boolean> update(@RequestBody Lead lead) {
        return R.ok(leadService.updateById(lead));
    }

    @Operation(summary = "删除线索")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(leadService.removeById(id));
    }

    @Operation(summary = "分配线索")
    @PutMapping("/{id}/assign")
    public R<Boolean> assign(@PathVariable Long id, @RequestParam Long advisorId) {
        return R.ok(leadService.assignLead(id, advisorId));
    }

    @Operation(summary = "批量分配线索")
    @PutMapping("/batch-assign")
    public R<Boolean> batchAssign(@RequestBody List<Long> leadIds, @RequestParam Long advisorId) {
        return R.ok(leadService.batchAssignLead(leadIds, advisorId));
    }

    @Operation(summary = "更新线索状态")
    @PutMapping("/{id}/status")
    public R<Boolean> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String lostReason) {
        return R.ok(leadService.updateStatus(id, status, lostReason));
    }

    @Operation(summary = "转化为学员")
    @PostMapping("/{id}/convert")
    public R<Long> convert(@PathVariable Long id) {
        return R.ok(leadService.convertToStudent(id));
    }

    // ==================== 跟进记录 ====================

    @Operation(summary = "获取跟进记录列表")
    @GetMapping("/{leadId}/follow-ups")
    public R<List<FollowUp>> getFollowUps(@PathVariable Long leadId) {
        return R.ok(leadService.getFollowUpList(leadId));
    }

    @Operation(summary = "添加跟进记录")
    @PostMapping("/{leadId}/follow-ups")
    public R<Boolean> addFollowUp(@PathVariable Long leadId, @RequestBody FollowUp followUp) {
        followUp.setLeadId(leadId);
        return R.ok(leadService.addFollowUp(followUp));
    }
}
