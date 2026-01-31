package com.edu.marketing.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.exception.BusinessException;
import com.edu.marketing.domain.dto.*;
import com.edu.marketing.domain.entity.Coupon;
import com.edu.marketing.domain.entity.CouponRecord;
import com.edu.marketing.domain.entity.CouponRule;
import com.edu.marketing.domain.vo.CouponRuleVO;
import com.edu.marketing.domain.vo.CouponStatisticsVO;
import com.edu.marketing.domain.vo.CouponValidateVO;
import com.edu.marketing.domain.vo.CouponVO;
import com.edu.marketing.mapper.CouponMapper;
import com.edu.marketing.mapper.CouponRecordMapper;
import com.edu.marketing.mapper.CouponRuleMapper;
import com.edu.marketing.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 优惠券服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements CouponService {

    private final CouponRecordMapper couponRecordMapper;
    private final CouponRuleMapper couponRuleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCoupon(CouponCreateDTO dto) {
        // 创建优惠券
        Coupon coupon = new Coupon();
        BeanUtil.copyProperties(dto, coupon);

        // 生成优惠券编号
        coupon.setCouponNo("CPN" + DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmss") + IdUtil.randomUUID().substring(0, 4));

        // 设置校区ID列表
        if (dto.getCampusIds() != null && !dto.getCampusIds().isEmpty()) {
            coupon.setCampusIds(dto.getCampusIds().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(",")));
        }

        // 初始化数量
        coupon.setIssuedQuantity(0);
        coupon.setUsedQuantity(0);

        // 设置状态为草稿
        if (StrUtil.isBlank(coupon.getStatus())) {
            coupon.setStatus("draft");
        }

        // 保存优惠券
        this.save(coupon);

        // 保存使用规则
        if (dto.getRules() != null && !dto.getRules().isEmpty()) {
            for (CouponRuleDTO ruleDTO : dto.getRules()) {
                CouponRule rule = new CouponRule();
                rule.setCouponId(coupon.getId());
                rule.setRuleType(ruleDTO.getRuleType());
                rule.setRuleValue(String.join(",", ruleDTO.getRuleValues()));
                rule.setRuleName(ruleDTO.getRuleName());
                couponRuleMapper.insert(rule);
            }
        }

        return coupon.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCoupon(Long id, CouponCreateDTO dto) {
        Coupon coupon = this.getById(id);
        if (coupon == null) {
            throw new BusinessException("优惠券不存在");
        }

        // 如果已发放，不允许修改核心字段
        if (coupon.getIssuedQuantity() > 0) {
            throw new BusinessException("优惠券已发放，不允许修改");
        }

        BeanUtil.copyProperties(dto, coupon, "id", "couponNo", "issuedQuantity", "usedQuantity");

        // 设置校区ID列表
        if (dto.getCampusIds() != null && !dto.getCampusIds().isEmpty()) {
            coupon.setCampusIds(dto.getCampusIds().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(",")));
        }

        this.updateById(coupon);

        // 删除旧规则
        couponRuleMapper.delete(new LambdaQueryWrapper<CouponRule>()
                .eq(CouponRule::getCouponId, id));

        // 保存新规则
        if (dto.getRules() != null && !dto.getRules().isEmpty()) {
            for (CouponRuleDTO ruleDTO : dto.getRules()) {
                CouponRule rule = new CouponRule();
                rule.setCouponId(id);
                rule.setRuleType(ruleDTO.getRuleType());
                rule.setRuleValue(String.join(",", ruleDTO.getRuleValues()));
                rule.setRuleName(ruleDTO.getRuleName());
                couponRuleMapper.insert(rule);
            }
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCoupon(Long id) {
        Coupon coupon = this.getById(id);
        if (coupon == null) {
            throw new BusinessException("优惠券不存在");
        }

        // 如果已发放，不允许删除
        if (coupon.getIssuedQuantity() > 0) {
            throw new BusinessException("优惠券已发放，不允许删除");
        }

        // 删除优惠券
        this.removeById(id);

        // 删除规则
        couponRuleMapper.delete(new LambdaQueryWrapper<CouponRule>()
                .eq(CouponRule::getCouponId, id));

        return true;
    }

    @Override
    public CouponVO getCouponDetail(Long id) {
        Coupon coupon = this.getById(id);
        if (coupon == null) {
            throw new BusinessException("优惠券不存在");
        }

        CouponVO vo = new CouponVO();
        BeanUtil.copyProperties(coupon, vo);

        // 计算剩余数量
        vo.setRemainingQuantity(coupon.getTotalQuantity() - coupon.getIssuedQuantity());

        // 查询使用规则
        List<CouponRule> rules = couponRuleMapper.selectList(
                new LambdaQueryWrapper<CouponRule>()
                        .eq(CouponRule::getCouponId, id));

        if (rules != null && !rules.isEmpty()) {
            List<CouponRuleVO> ruleVOs = rules.stream().map(rule -> {
                CouponRuleVO ruleVO = new CouponRuleVO();
                BeanUtil.copyProperties(rule, ruleVO);
                return ruleVO;
            }).collect(Collectors.toList());
            vo.setRules(ruleVOs);
        }

        return vo;
    }

    @Override
    public IPage<Coupon> pageCoupons(IPage<Coupon> page, CouponQueryDTO query) {
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();

        wrapper.like(StrUtil.isNotBlank(query.getName()), Coupon::getName, query.getName())
                .eq(StrUtil.isNotBlank(query.getType()), Coupon::getType, query.getType())
                .eq(StrUtil.isNotBlank(query.getStatus()), Coupon::getStatus, query.getStatus())
                .ge(query.getCreateStartTime() != null, Coupon::getCreateTime, query.getCreateStartTime())
                .le(query.getCreateEndTime() != null, Coupon::getCreateTime, query.getCreateEndTime())
                .orderByDesc(Coupon::getCreateTime);

        // 校区过滤
        if (query.getCampusId() != null) {
            wrapper.and(w -> w.isNull(Coupon::getCampusIds)
                    .or()
                    .eq(Coupon::getCampusIds, "")
                    .or()
                    .apply("FIND_IN_SET({0}, campus_ids)", query.getCampusId()));
        }

        return this.page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int issueCoupons(CouponIssueDTO dto) {
        Coupon coupon = this.getById(dto.getCouponId());
        if (coupon == null) {
            throw new BusinessException("优惠券不存在");
        }

        // 检查优惠券状态
        if (!"active".equals(coupon.getStatus())) {
            throw new BusinessException("优惠券未生效，无法发放");
        }

        // 检查库存
        int remainingQuantity = coupon.getTotalQuantity() - coupon.getIssuedQuantity();
        if (remainingQuantity < dto.getStudentIds().size()) {
            throw new BusinessException("优惠券库存不足");
        }

        int successCount = 0;

        for (Long studentId : dto.getStudentIds()) {
            try {
                // 检查领取限制
                int receivedCount = couponRecordMapper.countReceivedByStudent(studentId, dto.getCouponId());
                if (receivedCount >= coupon.getReceiveLimit()) {
                    log.warn("学员{}已达到优惠券{}的领取上限", studentId, dto.getCouponId());
                    continue;
                }

                // 创建优惠券记录
                CouponRecord record = new CouponRecord();
                record.setRecordNo("CPR" + DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmss") + IdUtil.randomUUID().substring(0, 6));
                record.setCouponId(coupon.getId());
                record.setCouponNo(coupon.getCouponNo());
                record.setCouponName(coupon.getName());
                record.setStudentId(studentId);
                record.setStatus("unused");
                record.setReceiveTime(LocalDateTime.now());
                record.setReceiveType(dto.getIssueType());
                record.setRemark(dto.getRemark());

                // 计算有效期
                if ("fixed".equals(coupon.getValidType())) {
                    record.setValidStartTime(coupon.getValidStartTime());
                    record.setValidEndTime(coupon.getValidEndTime());
                } else if ("relative".equals(coupon.getValidType())) {
                    record.setValidStartTime(LocalDateTime.now());
                    record.setValidEndTime(LocalDateTime.now().plusDays(coupon.getValidDays()));
                }

                couponRecordMapper.insert(record);
                successCount++;

            } catch (Exception e) {
                log.error("发放优惠券给学员{}失败", studentId, e);
            }
        }

        // 更新已发放数量
        coupon.setIssuedQuantity(coupon.getIssuedQuantity() + successCount);
        this.updateById(coupon);

        return successCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long claimCoupon(Long couponId, Long studentId) {
        Coupon coupon = this.getById(couponId);
        if (coupon == null) {
            throw new BusinessException("优惠券不存在");
        }

        // 检查优惠券状态
        if (!"active".equals(coupon.getStatus())) {
            throw new BusinessException("优惠券未生效");
        }

        // 检查领取方式
        if (!"auto".equals(coupon.getReceiveType())) {
            throw new BusinessException("该优惠券不支持自动领取");
        }

        // 检查库存
        if (coupon.getIssuedQuantity() >= coupon.getTotalQuantity()) {
            throw new BusinessException("优惠券已领完");
        }

        // 检查领取限制
        int receivedCount = couponRecordMapper.countReceivedByStudent(studentId, couponId);
        if (receivedCount >= coupon.getReceiveLimit()) {
            throw new BusinessException("已达到领取上限");
        }

        // 创建优惠券记录
        CouponRecord record = new CouponRecord();
        record.setRecordNo("CPR" + DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmss") + IdUtil.randomUUID().substring(0, 6));
        record.setCouponId(coupon.getId());
        record.setCouponNo(coupon.getCouponNo());
        record.setCouponName(coupon.getName());
        record.setStudentId(studentId);
        record.setStatus("unused");
        record.setReceiveTime(LocalDateTime.now());
        record.setReceiveType("auto");

        // 计算有效期
        if ("fixed".equals(coupon.getValidType())) {
            record.setValidStartTime(coupon.getValidStartTime());
            record.setValidEndTime(coupon.getValidEndTime());
        } else if ("relative".equals(coupon.getValidType())) {
            record.setValidStartTime(LocalDateTime.now());
            record.setValidEndTime(LocalDateTime.now().plusDays(coupon.getValidDays()));
        }

        couponRecordMapper.insert(record);

        // 更新已发放数量
        coupon.setIssuedQuantity(coupon.getIssuedQuantity() + 1);
        this.updateById(coupon);

        return record.getId();
    }

    @Override
    public CouponValidateVO validateCoupon(CouponValidateDTO dto) {
        CouponValidateVO result = new CouponValidateVO();
        result.setValid(false);

        // 查询优惠券记录
        CouponRecord record = couponRecordMapper.selectById(dto.getRecordId());
        if (record == null) {
            result.setReason("优惠券不存在");
            return result;
        }

        // 检查学员
        if (!record.getStudentId().equals(dto.getStudentId())) {
            result.setReason("优惠券不属于该学员");
            return result;
        }

        // 检查状态
        if (!"unused".equals(record.getStatus())) {
            result.setReason("优惠券已使用或已失效");
            return result;
        }

        // 检查有效期
        LocalDateTime now = LocalDateTime.now();
        if (record.getValidStartTime() != null && now.isBefore(record.getValidStartTime())) {
            result.setReason("优惠券未到使用时间");
            return result;
        }
        if (record.getValidEndTime() != null && now.isAfter(record.getValidEndTime())) {
            result.setReason("优惠券已过期");
            return result;
        }

        // 查询优惠券模板
        Coupon coupon = this.getById(record.getCouponId());
        if (coupon == null) {
            result.setReason("优惠券模板不存在");
            return result;
        }

        // 检查最低消费金额
        if (coupon.getMinAmount() != null && dto.getOrderAmount().compareTo(coupon.getMinAmount()) < 0) {
            result.setReason("订单金额未达到最低消费要求：" + coupon.getMinAmount() + "元");
            return result;
        }

        // 检查使用限制
        int usedCount = couponRecordMapper.countUsedByStudent(dto.getStudentId(), coupon.getId());
        if (usedCount >= coupon.getUseLimit()) {
            result.setReason("已达到使用上限");
            return result;
        }

        // 检查校区限制
        if (dto.getCampusId() != null && StrUtil.isNotBlank(coupon.getCampusIds())) {
            List<String> campusIdList = Arrays.asList(coupon.getCampusIds().split(","));
            if (!campusIdList.contains(dto.getCampusId().toString())) {
                result.setReason("该优惠券不适用于当前校区");
                return result;
            }
        }

        // 检查使用规则
        List<CouponRule> rules = couponRuleMapper.selectList(
                new LambdaQueryWrapper<CouponRule>()
                        .eq(CouponRule::getCouponId, coupon.getId()));

        if (rules != null && !rules.isEmpty()) {
            for (CouponRule rule : rules) {
                if ("course".equals(rule.getRuleType()) && StrUtil.isNotBlank(dto.getCourseIds())) {
                    // 检查课程限制
                    List<String> allowedCourses = Arrays.asList(rule.getRuleValue().split(","));
                    List<String> orderCourses = Arrays.asList(dto.getCourseIds().split(","));
                    boolean hasMatch = orderCourses.stream().anyMatch(allowedCourses::contains);
                    if (!hasMatch) {
                        result.setReason("该优惠券不适用于所选课程");
                        return result;
                    }
                } else if ("contract_type".equals(rule.getRuleType()) && StrUtil.isNotBlank(dto.getContractType())) {
                    // 检查合同类型限制
                    List<String> allowedTypes = Arrays.asList(rule.getRuleValue().split(","));
                    if (!allowedTypes.contains(dto.getContractType())) {
                        result.setReason("该优惠券不适用于当前合同类型");
                        return result;
                    }
                }
            }
        }

        // 计算优惠金额
        BigDecimal discountAmount = calculateDiscountAmount(coupon, dto.getOrderAmount());

        result.setValid(true);
        result.setDiscountAmount(discountAmount);
        result.setRecordId(record.getId());
        result.setCouponName(coupon.getName());
        result.setCouponType(coupon.getType());
        result.setDiscountValue(coupon.getDiscountValue());
        result.setDiscountType(coupon.getDiscountType());

        return result;
    }

    /**
     * 计算优惠金额
     */
    private BigDecimal calculateDiscountAmount(Coupon coupon, BigDecimal orderAmount) {
        BigDecimal discountAmount = BigDecimal.ZERO;

        if ("cash".equals(coupon.getType()) || "full_reduction".equals(coupon.getType())) {
            // 代金券或满减券：直接减免金额
            discountAmount = coupon.getDiscountValue();
        } else if ("discount".equals(coupon.getType())) {
            // 折扣券：按百分比计算
            if ("percent".equals(coupon.getDiscountType())) {
                // 折扣百分比（如85表示8.5折）
                BigDecimal discountRate = BigDecimal.valueOf(100).subtract(coupon.getDiscountValue())
                        .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                discountAmount = orderAmount.multiply(discountRate);
            } else {
                // 直接金额折扣
                discountAmount = coupon.getDiscountValue();
            }

            // 检查最大优惠金额限制
            if (coupon.getMaxDiscountAmount() != null &&
                    discountAmount.compareTo(coupon.getMaxDiscountAmount()) > 0) {
                discountAmount = coupon.getMaxDiscountAmount();
            }
        }

        // 优惠金额不能超过订单金额
        if (discountAmount.compareTo(orderAmount) > 0) {
            discountAmount = orderAmount;
        }

        return discountAmount.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean useCoupon(CouponUseDTO dto) {
        // 先验证优惠券
        CouponValidateDTO validateDTO = new CouponValidateDTO();
        BeanUtil.copyProperties(dto, validateDTO);

        CouponValidateVO validateResult = validateCoupon(validateDTO);
        if (!validateResult.getValid()) {
            throw new BusinessException("优惠券不可用：" + validateResult.getReason());
        }

        // 更新优惠券记录
        CouponRecord record = couponRecordMapper.selectById(dto.getRecordId());
        record.setStatus("used");
        record.setUseTime(LocalDateTime.now());
        record.setUseContractId(dto.getContractId());
        record.setUsePaymentId(dto.getPaymentId());
        record.setDiscountAmount(validateResult.getDiscountAmount());
        couponRecordMapper.updateById(record);

        // 更新优惠券已使用数量
        Coupon coupon = this.getById(record.getCouponId());
        coupon.setUsedQuantity(coupon.getUsedQuantity() + 1);
        this.updateById(coupon);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean writeOffCoupon(Long recordId) {
        CouponRecord record = couponRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException("优惠券记录不存在");
        }

        if (!"unused".equals(record.getStatus())) {
            throw new BusinessException("优惠券状态不正确");
        }

        record.setStatus("used");
        record.setUseTime(LocalDateTime.now());
        couponRecordMapper.updateById(record);

        // 更新优惠券已使用数量
        Coupon coupon = this.getById(record.getCouponId());
        coupon.setUsedQuantity(coupon.getUsedQuantity() + 1);
        this.updateById(coupon);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean invalidateCoupon(Long recordId, String reason) {
        CouponRecord record = couponRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException("优惠券记录不存在");
        }

        record.setStatus("invalid");
        record.setInvalidReason(reason);
        couponRecordMapper.updateById(record);

        return true;
    }

    @Override
    public IPage<CouponRecord> pageCouponRecords(IPage<CouponRecord> page, CouponRecordQueryDTO query) {
        LambdaQueryWrapper<CouponRecord> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(query.getCouponId() != null, CouponRecord::getCouponId, query.getCouponId())
                .eq(query.getStudentId() != null, CouponRecord::getStudentId, query.getStudentId())
                .like(StrUtil.isNotBlank(query.getStudentName()), CouponRecord::getStudentName, query.getStudentName())
                .eq(query.getCampusId() != null, CouponRecord::getCampusId, query.getCampusId())
                .eq(StrUtil.isNotBlank(query.getStatus()), CouponRecord::getStatus, query.getStatus())
                .ge(query.getReceiveStartTime() != null, CouponRecord::getReceiveTime, query.getReceiveStartTime())
                .le(query.getReceiveEndTime() != null, CouponRecord::getReceiveTime, query.getReceiveEndTime())
                .ge(query.getUseStartTime() != null, CouponRecord::getUseTime, query.getUseStartTime())
                .le(query.getUseEndTime() != null, CouponRecord::getUseTime, query.getUseEndTime())
                .orderByDesc(CouponRecord::getCreateTime);

        return couponRecordMapper.selectPage(page, wrapper);
    }

    @Override
    public List<CouponRecord> getAvailableCoupons(Long studentId, BigDecimal orderAmount, String courseIds) {
        // 查询学员未使用的优惠券
        LambdaQueryWrapper<CouponRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CouponRecord::getStudentId, studentId)
                .eq(CouponRecord::getStatus, "unused")
                .le(CouponRecord::getValidStartTime, LocalDateTime.now())
                .ge(CouponRecord::getValidEndTime, LocalDateTime.now())
                .orderByDesc(CouponRecord::getReceiveTime);

        List<CouponRecord> records = couponRecordMapper.selectList(wrapper);

        // 过滤可用的优惠券
        List<CouponRecord> availableRecords = new ArrayList<>();
        for (CouponRecord record : records) {
            CouponValidateDTO validateDTO = new CouponValidateDTO();
            validateDTO.setRecordId(record.getId());
            validateDTO.setStudentId(studentId);
            validateDTO.setOrderAmount(orderAmount);
            validateDTO.setCourseIds(courseIds);

            CouponValidateVO result = validateCoupon(validateDTO);
            if (result.getValid()) {
                record.setDiscountAmount(result.getDiscountAmount());
                availableRecords.add(record);
            }
        }

        return availableRecords;
    }

    @Override
    public List<CouponStatisticsVO> getCouponStatistics(Long couponId, Long campusId) {
        return couponRecordMapper.getCouponStatistics(couponId, campusId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCouponStatus(Long id, String status) {
        Coupon coupon = this.getById(id);
        if (coupon == null) {
            throw new BusinessException("优惠券不存在");
        }

        coupon.setStatus(status);
        return this.updateById(coupon);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void expireCoupons() {
        // 查询已过期但状态未更新的优惠券记录
        LambdaQueryWrapper<CouponRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CouponRecord::getStatus, "unused")
                .lt(CouponRecord::getValidEndTime, LocalDateTime.now());

        List<CouponRecord> expiredRecords = couponRecordMapper.selectList(wrapper);

        for (CouponRecord record : expiredRecords) {
            record.setStatus("expired");
            couponRecordMapper.updateById(record);
        }

        log.info("处理过期优惠券{}张", expiredRecords.size());
    }
}
