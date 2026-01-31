package com.edu.teaching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.teaching.domain.dto.PriceStrategyDTO;
import com.edu.teaching.domain.entity.PriceStrategy;
import com.edu.teaching.mapper.PriceStrategyMapper;
import com.edu.teaching.service.PriceStrategyService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

/**
 * 价格策略服务实现
 */
@Service
public class PriceStrategyServiceImpl extends ServiceImpl<PriceStrategyMapper, PriceStrategy> implements PriceStrategyService {

    @Override
    public boolean createStrategy(PriceStrategyDTO dto) {
        PriceStrategy strategy = new PriceStrategy();
        BeanUtils.copyProperties(dto, strategy);
        return save(strategy);
    }

    @Override
    public boolean updateStrategy(PriceStrategyDTO dto) {
        PriceStrategy strategy = new PriceStrategy();
        BeanUtils.copyProperties(dto, strategy);
        return updateById(strategy);
    }

    @Override
    public Page<PriceStrategy> pageStrategies(Integer pageNum, Integer pageSize, String name, String type, Integer status) {
        Page<PriceStrategy> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PriceStrategy> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null, PriceStrategy::getName, name)
                .eq(type != null, PriceStrategy::getType, type)
                .eq(status != null, PriceStrategy::getStatus, status)
                .orderByDesc(PriceStrategy::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public boolean enableStrategy(Long id) {
        PriceStrategy strategy = new PriceStrategy();
        strategy.setId(id);
        strategy.setStatus(1);
        return updateById(strategy);
    }

    @Override
    public boolean disableStrategy(Long id) {
        PriceStrategy strategy = new PriceStrategy();
        strategy.setId(id);
        strategy.setStatus(0);
        return updateById(strategy);
    }

    @Override
    public BigDecimal calculateDiscountPrice(Long targetId, String targetType, BigDecimal originalPrice, String studentType) {
        if (originalPrice == null || originalPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return originalPrice;
        }

        LocalDate now = LocalDate.now();

        // 查询适用的价格策略
        LambdaQueryWrapper<PriceStrategy> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PriceStrategy::getTargetId, targetId)
                .eq(PriceStrategy::getTargetType, targetType)
                .eq(PriceStrategy::getStatus, 1)
                .le(PriceStrategy::getStartDate, now)
                .ge(PriceStrategy::getEndDate, now);

        // 如果指定了学员类型，添加学员类型过滤
        if (studentType != null) {
            wrapper.and(w -> w.eq(PriceStrategy::getType, "student_type")
                    .eq(PriceStrategy::getStudentType, studentType)
                    .or()
                    .eq(PriceStrategy::getType, "time_period"));
        }

        List<PriceStrategy> strategies = list(wrapper);

        if (strategies.isEmpty()) {
            return originalPrice;
        }

        // 应用所有适用的策略（可以叠加）
        BigDecimal finalPrice = originalPrice;
        for (PriceStrategy strategy : strategies) {
            if ("percentage".equals(strategy.getDiscountType())) {
                // 百分比折扣
                BigDecimal discount = strategy.getDiscountValue().divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
                finalPrice = finalPrice.multiply(discount);
            } else if ("fixed".equals(strategy.getDiscountType())) {
                // 固定金额折扣
                finalPrice = finalPrice.subtract(strategy.getDiscountValue());
            }
        }

        // 确保价格不为负数
        if (finalPrice.compareTo(BigDecimal.ZERO) < 0) {
            finalPrice = BigDecimal.ZERO;
        }

        return finalPrice.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public boolean validateStrategy(PriceStrategyDTO dto) {
        // 验证折扣类型和折扣值
        if ("percentage".equals(dto.getDiscountType())) {
            // 百分比折扣：0-100
            if (dto.getDiscountValue().compareTo(BigDecimal.ZERO) < 0
                    || dto.getDiscountValue().compareTo(new BigDecimal("100")) > 0) {
                return false;
            }
        } else if ("fixed".equals(dto.getDiscountType())) {
            // 固定金额折扣：必须大于0
            if (dto.getDiscountValue().compareTo(BigDecimal.ZERO) <= 0) {
                return false;
            }
        }

        // 验证日期范围
        if (dto.getStartDate() != null && dto.getEndDate() != null) {
            if (dto.getStartDate().isAfter(dto.getEndDate())) {
                return false;
            }
        }

        // 验证学员类型策略必须指定学员类型
        if ("student_type".equals(dto.getType()) && dto.getStudentType() == null) {
            return false;
        }

        return true;
    }
}
