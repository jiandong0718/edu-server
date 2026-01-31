package com.edu.marketing.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.marketing.domain.dto.*;
import com.edu.marketing.domain.entity.Coupon;
import com.edu.marketing.domain.entity.CouponRecord;
import com.edu.marketing.domain.vo.CouponStatisticsVO;
import com.edu.marketing.domain.vo.CouponValidateVO;
import com.edu.marketing.domain.vo.CouponVO;

import java.util.List;

/**
 * 优惠券服务接口
 */
public interface CouponService extends IService<Coupon> {

    /**
     * 创建优惠券模板
     *
     * @param dto 创建DTO
     * @return 优惠券ID
     */
    Long createCoupon(CouponCreateDTO dto);

    /**
     * 更新优惠券模板
     *
     * @param id 优惠券ID
     * @param dto 更新DTO
     * @return 是否成功
     */
    boolean updateCoupon(Long id, CouponCreateDTO dto);

    /**
     * 删除优惠券模板
     *
     * @param id 优惠券ID
     * @return 是否成功
     */
    boolean deleteCoupon(Long id);

    /**
     * 获取优惠券详情
     *
     * @param id 优惠券ID
     * @return 优惠券详情
     */
    CouponVO getCouponDetail(Long id);

    /**
     * 分页查询优惠券列表
     *
     * @param page 分页对象
     * @param query 查询条件
     * @return 分页结果
     */
    IPage<Coupon> pageCoupons(IPage<Coupon> page, CouponQueryDTO query);

    /**
     * 发放优惠券
     *
     * @param dto 发放DTO
     * @return 发放成功的记录数
     */
    int issueCoupons(CouponIssueDTO dto);

    /**
     * 学员领取优惠券
     *
     * @param couponId 优惠券ID
     * @param studentId 学员ID
     * @return 优惠券记录ID
     */
    Long claimCoupon(Long couponId, Long studentId);

    /**
     * 验证优惠券是否可用
     *
     * @param dto 验证DTO
     * @return 验证结果
     */
    CouponValidateVO validateCoupon(CouponValidateDTO dto);

    /**
     * 使用优惠券
     *
     * @param dto 使用DTO
     * @return 是否成功
     */
    boolean useCoupon(CouponUseDTO dto);

    /**
     * 核销优惠券
     *
     * @param recordId 优惠券记录ID
     * @return 是否成功
     */
    boolean writeOffCoupon(Long recordId);

    /**
     * 作废优惠券
     *
     * @param recordId 优惠券记录ID
     * @param reason 作废原因
     * @return 是否成功
     */
    boolean invalidateCoupon(Long recordId, String reason);

    /**
     * 分页查询优惠券记录
     *
     * @param page 分页对象
     * @param query 查询条件
     * @return 分页结果
     */
    IPage<CouponRecord> pageCouponRecords(IPage<CouponRecord> page, CouponRecordQueryDTO query);

    /**
     * 查询学员可用的优惠券列表
     *
     * @param studentId 学员ID
     * @param orderAmount 订单金额
     * @param courseIds 课程ID列表
     * @return 可用优惠券列表
     */
    List<CouponRecord> getAvailableCoupons(Long studentId, java.math.BigDecimal orderAmount, String courseIds);

    /**
     * 查询优惠券统计信息
     *
     * @param couponId 优惠券ID（可选）
     * @param campusId 校区ID（可选）
     * @return 统计信息列表
     */
    List<CouponStatisticsVO> getCouponStatistics(Long couponId, Long campusId);

    /**
     * 更新优惠券状态
     *
     * @param id 优惠券ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateCouponStatus(Long id, String status);

    /**
     * 定时任务：过期优惠券处理
     */
    void expireCoupons();
}
