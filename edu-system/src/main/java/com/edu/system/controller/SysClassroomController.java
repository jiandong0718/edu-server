package com.edu.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.R;
import com.edu.system.domain.dto.ClassroomDTO;
import com.edu.system.domain.dto.ClassroomQueryDTO;
import com.edu.system.domain.vo.ClassroomVO;
import com.edu.system.service.SysClassroomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教室管理控制器
 */
@Tag(name = "教室管理")
@RestController
@RequestMapping("/system/classroom")
@RequiredArgsConstructor
public class SysClassroomController {

    private final SysClassroomService classroomService;

    @Operation(summary = "分页查询教室列表")
    @GetMapping("/page")
    public R<Page<ClassroomVO>> page(@Validated ClassroomQueryDTO queryDTO) {
        Page<ClassroomVO> page = classroomService.getClassroomPage(queryDTO);
        return R.ok(page);
    }

    @Operation(summary = "获取教室列表")
    @GetMapping("/list")
    public R<List<ClassroomVO>> list(
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId) {
        List<ClassroomVO> list = classroomService.getAvailableClassrooms(campusId);
        return R.ok(list);
    }

    @Operation(summary = "获取教室详情")
    @GetMapping("/{id}")
    public R<ClassroomVO> getById(
            @Parameter(description = "教室ID") @PathVariable Long id) {
        ClassroomVO vo = classroomService.getClassroomDetail(id);
        return R.ok(vo);
    }

    @Operation(summary = "新增教室")
    @PostMapping
    public R<Long> add(@Validated @RequestBody ClassroomDTO dto) {
        Long id = classroomService.createClassroom(dto);
        return R.ok(id);
    }

    @Operation(summary = "修改教室")
    @PutMapping("/{id}")
    public R<Void> update(
            @Parameter(description = "教室ID") @PathVariable Long id,
            @Validated @RequestBody ClassroomDTO dto) {
        classroomService.updateClassroom(id, dto);
        return R.ok();
    }

    @Operation(summary = "删除教室")
    @DeleteMapping("/{id}")
    public R<Void> delete(
            @Parameter(description = "教室ID") @PathVariable Long id) {
        classroomService.deleteClassroom(id);
        return R.ok();
    }

    @Operation(summary = "批量删除教室")
    @DeleteMapping("/batch")
    public R<Void> deleteBatch(@RequestBody List<Long> ids) {
        classroomService.batchDeleteClassroom(ids);
        return R.ok();
    }

    @Operation(summary = "修改状态")
    @PutMapping("/{id}/status")
    public R<Void> updateStatus(
            @Parameter(description = "教室ID") @PathVariable Long id,
            @Parameter(description = "状态：0-禁用，1-启用") @RequestParam Integer status) {
        classroomService.updateStatus(id, status);
        return R.ok();
    }
}
