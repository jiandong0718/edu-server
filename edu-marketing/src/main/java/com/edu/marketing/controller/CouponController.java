package com.edu.marketing.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.R;
import com.edu.marketing.domain.dto.*;
import com.edu.marketing.domain.entity.Coupon;
import com.edu.marketing.domain.entity.CouponRecord;
import com.edu.marketing.domain.vo.CouponStatisticsVO;
import com.edu.marketing.domain.vo.CouponValidateVO;
import com.edu.marketing.domain.vo.CouponVO;
import com.edu.marketing.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 优惠券管理控制器
 */
@Tag(name = "优惠券管理")
@RestController
@RequestMapping("/marketing/coupon")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    // ==================== 优惠券模板管理 ====================

    @Operation(summary = "创建优惠券模板")
    @PostMapping
    public R<Long> createCoupon(@RequestBody CouponCreateDTO dto) {
        return R.ok(couponService.createCoupon(dto));
    }

    @Operation(summary = "更新优惠券模板")
    @PutMapping("/{id}")
    public R<Boolean> updateCoupon(@PathVariable Long id, @RequestBody CouponCreateDTO dto) {
        return R.ok(couponService.updateCoupon(id, dto));
    }

    @Operation(summary = "删除优惠券模板")
    @DeleteMapping("/{id}")
    public R<Boolean> deleteCoupon(@PathVariable Long id) {
        return R.ok(couponService.deleteCoupon(id));
    }

    @Operation(summary = "获取优惠券详情")
    @GetMapping("/{id}")
    public R<CouponVO> getCouponDetail(@PathVariable Long id) {
        return R.ok(couponService.getCouponDetail(id));
    }

    @Operation(summary = "分页查询优惠券列表")
    @GetMapping("/page")
    public R<com.baomidou.mybatisplus.core.metadata.IPage<Coupon>> pageCoupons(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "优惠券名称") @RequestParam(required = false) String name,
            @Parameter(description = "优惠券类型") @RequestParam(required = false) String type,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId) {

        CouponQueryDTO query = new CouponQueryDTO();
        query.setName(name);
        query.setType(type);
        query.setStatus(status);
        query.setCampusId(campusId);
        query.setPageNum(pageNum);
        query.setPageSize(pageSize);

        Page<Coupon> page = new Page<>(pageNum, pageSize);
        return R.ok(couponService.pageCoupons(page, query));
    }

    @Operation(summary = "更新优惠券状态")
    @PutMapping("/{id}/status")
    public R<Boolean> updateCouponStatus(
            @PathVariable Long id,
            @Parameter(description = "状态：draft-草稿，active-生效中，paused-已暂停，expired-已过期")
            @RequestParam String status) {
        return R.ok(couponService.updateCouponStatus(id, status));
    }

    // ==================== 优惠券发放 ====================

    @Operation(summary = "发放优惠券", description = "手动发放优惠券给指定学员")
    @PostMapping("/issue")
    public R<Integer> issueCoupons(@RequestBody CouponIssueDTO dto) {
        int count = couponService.issueCoupons(dto);
        return R.ok("成功发放" + count + "张优惠券", count);
    }

    @Operation(summary = "学员领取优惠券", description = "学员自助领取优惠券")
    @PostMapping("/{couponId}/claim")
    public R<Long> claimCoupon(
            @PathVariable Long couponId,
            @Parameter(description = "学员ID") @RequestParam Long studentId) {
        return R.ok(couponService.claimCoupon(couponId, studentId));
    }

    // ==================== 优惠券使用 ====================

    @Operation(summary = "验证优惠券", description = "验证优惠券是否可用并计算优惠金额")
    @PostMapping("/validate")
    public R<CouponValidateVO> validateCoupon(@RequestBody CouponValidateDTO dto) {
        return R.ok(couponService.validateCoupon(dto));
    }

    @Operation(summary = "使用优惠券", description = "在支付时使用优惠券")
    @PostMapping("/use")
    public R<Boolean> useCoupon(@RequestBody CouponUseDTO dto) {
        return R.ok(couponService.useCoupon(dto));
    }

    @Operation(summary = "核销优惠券", description = "手动核销优惠券")
    @PutMapping("/record/{recordId}/write-off")
    public R<Boolean> writeOffCoupon(@PathVariable Long recordId) {
        return R.ok(couponService.writeOffCoupon(recordId));
    }

    @Operation(summary = "作废优惠券", description = "将优惠券标记为失效")
    @PutMapping("/record/{recordId}/invalidate")
    public R<Boolean> invalidateCoupon(
            @PathVariable Long recordId,
            @Parameter(description = "作废原因") @RequestParam String reason) {
        return R.ok(couponService.invalidateCoupon(recordId, reason));
    }

    // ==================== 优惠券记录查询 ====================

    @Operation(summary = "分页查询优惠券记录")
    @GetMapping("/record/page")
    public R<com.baomidou.mybatisplus.core.metadata.IPage<CouponRecord>> pageCouponRecords(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "优惠券ID") @RequestParam(required = false) Long couponId,
            @Parameter(description = "学员ID") @RequestParam(required = false) Long studentId,
            @Parameter(description = "学员姓名") @RequestParam(required = false) String studentName,
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId,
            @Parameter(description = "状态") @RequestParam(required = false) String status) {

        CouponRecordQueryDTO query = new CouponRecordQueryDTO();
        query.setCouponId(couponId);
        query.setStudentId(studentId);
        query.setStudentName(studentName);
        query.setCampusId(campusId);
        query.setStatus(status);
        query.setPageNum(pageNum);
        query.setPageSize(pageSize);

        Page<CouponRecord> page = new Page<>(pageNum, pageSize);
        return R.ok(couponService.pageCouponRecords(page, query));
    }

    @Operation(summary = "查询学员可用优惠券", description = "查询学员在当前订单可用的优惠券列表")
    @GetMapping("/available")
    public R<List<CouponRecord>> getAvailableCoupons(
            @Parameter(description = "学员ID") @RequestParam Long studentId,
            @Parameter(description = "订单金额") @RequestParam BigDecimal orderAmount,
            @Parameter(description = "课程ID列表（逗号分隔）") @RequestParam(required = false) String courseIds) {
        return R.ok(couponService.getAvailableCoupons(studentId, orderAmount, courseIds));
    }

    // ==================== 优惠券统计 ====================

    @Operation(summary = "查询优惠券统计信息")
    @GetMapping("/statistics")
    public R<List<CouponStatisticsVO>> getCouponStatistics(
            @Parameter(description = "优惠券ID") @RequestParam(required = false) Long couponId,
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId) {
        return R.ok(couponService.getCouponStatistics(couponId, campusId));
    }

    @Operation(summary = "处理过期优惠券", description = "定时任务：将已过期的优惠券标记为过期状态")
    @PostMapping("/expire")
    public R<Void> expireCoupons() {
        couponService.expireCoupons();
        return R.ok();
    }
}
