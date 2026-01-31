package com.edu.teaching.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.R;
import com.edu.teaching.domain.dto.CoursePackageDTO;
import com.edu.teaching.domain.entity.CoursePackage;
import com.edu.teaching.domain.vo.CoursePackageVO;
import com.edu.teaching.service.CoursePackageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程包管理控制器
 */
@Tag(name = "课程包管理")
@RestController
@RequestMapping("/teaching/course-package")
@RequiredArgsConstructor
public class CoursePackageController {

    private final CoursePackageService coursePackageService;

    @Operation(summary = "分页查询课程包列表")
    @GetMapping("/page")
    public R<Page<CoursePackageVO>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long campusId) {
        Page<CoursePackageVO> page = coursePackageService.pagePackagesWithDetails(pageNum, pageSize, name, status, campusId);
        return R.ok(page);
    }

    @Operation(summary = "获取在售课程包列表")
    @GetMapping("/list")
    public R<List<CoursePackageVO>> list(@RequestParam(required = false) Long campusId) {
        List<CoursePackageVO> list = coursePackageService.listOnSalePackages(campusId);
        return R.ok(list);
    }

    @Operation(summary = "获取课程包详情")
    @GetMapping("/{id}")
    public R<CoursePackageVO> getById(@PathVariable Long id) {
        CoursePackageVO vo = coursePackageService.getPackageDetail(id);
        if (vo == null) {
            return R.fail("课程包不存在");
        }
        return R.ok(vo);
    }

    @Operation(summary = "新增课程包")
    @PostMapping
    public R<Boolean> add(@RequestBody CoursePackageDTO dto) {
        // 验证课程是否存在
        if (!coursePackageService.validateCourses(dto)) {
            return R.fail("课程包中包含不存在的课程");
        }
        boolean result = coursePackageService.createPackage(dto);
        return result ? R.ok(true) : R.fail("创建课程包失败");
    }

    @Operation(summary = "修改课程包")
    @PutMapping("/{id}")
    public R<Boolean> update(@PathVariable Long id, @RequestBody CoursePackageDTO dto) {
        dto.setId(id);
        // 验证课程是否存在
        if (!coursePackageService.validateCourses(dto)) {
            return R.fail("课程包中包含不存在的课程");
        }
        boolean result = coursePackageService.updatePackage(dto);
        return result ? R.ok(true) : R.fail("更新课程包失败");
    }

    @Operation(summary = "删除课程包")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        boolean result = coursePackageService.deletePackage(id);
        return result ? R.ok(true) : R.fail("删除课程包失败");
    }

    @Operation(summary = "上架课程包")
    @PutMapping("/{id}/publish")
    public R<Boolean> publish(@PathVariable Long id) {
        boolean result = coursePackageService.publishPackage(id);
        return result ? R.ok(true) : R.fail("上架课程包失败");
    }

    @Operation(summary = "下架课程包")
    @PutMapping("/{id}/unpublish")
    public R<Boolean> unpublish(@PathVariable Long id) {
        boolean result = coursePackageService.unpublishPackage(id);
        return result ? R.ok(true) : R.fail("下架课程包失败");
    }

    @Operation(summary = "更新课程包状态")
    @PutMapping("/{id}/status")
    public R<Boolean> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        if (status == 1) {
            return publish(id);
        } else {
            return unpublish(id);
        }
    }
}
