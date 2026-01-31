package com.edu.teaching.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.R;
import com.edu.teaching.domain.dto.TeacherStatusChangeDTO;
import com.edu.teaching.domain.vo.TeacherStatusLogVO;
import com.edu.teaching.service.TeacherStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 教师状态管理控制器
 */
@Tag(name = "教师状态管理")
@RestController
@RequestMapping("/teaching/teacher/status")
@RequiredArgsConstructor
public class TeacherStatusController {

    private final TeacherStatusService teacherStatusService;

    @Operation(summary = "变更教师状态")
    @PostMapping("/change")
    public R<Boolean> changeStatus(@Valid @RequestBody TeacherStatusChangeDTO dto) {
        return R.ok(teacherStatusService.changeStatus(dto));
    }

    @Operation(summary = "查询教师当前状态")
    @GetMapping("/current/{teacherId}")
    public R<String> getCurrentStatus(
            @Parameter(description = "教师ID") @PathVariable Long teacherId) {
        return R.ok(teacherStatusService.getCurrentStatus(teacherId));
    }

    @Operation(summary = "分页查询教师状态变更历史")
    @GetMapping("/log/page")
    public R<Page<TeacherStatusLogVO>> pageStatusLog(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "教师ID") @RequestParam(required = false) Long teacherId,
            @Parameter(description = "教师姓名") @RequestParam(required = false) String teacherName,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId) {
        Page<TeacherStatusLogVO> page = teacherStatusService.pageStatusLog(
                pageNum, pageSize, teacherId, teacherName, status, campusId);
        return R.ok(page);
    }

    @Operation(summary = "查询教师状态变更历史列表")
    @GetMapping("/log/list/{teacherId}")
    public R<List<TeacherStatusLogVO>> getStatusLogList(
            @Parameter(description = "教师ID") @PathVariable Long teacherId) {
        return R.ok(teacherStatusService.getStatusLogList(teacherId));
    }

    @Operation(summary = "获取教师最新的状态变更记录")
    @GetMapping("/log/latest/{teacherId}")
    public R<TeacherStatusLogVO> getLatestStatusLog(
            @Parameter(description = "教师ID") @PathVariable Long teacherId) {
        return R.ok(teacherStatusService.getLatestStatusLog(teacherId));
    }

    @Operation(summary = "批量查询教师状态")
    @PostMapping("/batch")
    public R<Map<Long, String>> batchGetStatus(@RequestBody List<Long> teacherIds) {
        return R.ok(teacherStatusService.batchGetStatus(teacherIds));
    }
}
