package com.edu.teaching.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.teaching.domain.dto.PriceCalculateDTO;
import com.edu.teaching.domain.dto.PriceStrategyDTO;
import com.edu.teaching.domain.dto.PriceStrategyQueryDTO;
import com.edu.teaching.domain.dto.PriceStrategyRuleDTO;
import com.edu.teaching.domain.entity.PriceStrategy;
import com.edu.teaching.domain.entity.PriceStrategyRule;
import com.edu.teaching.domain.vo.PriceCalculateVO;
import com.edu.teaching.domain.vo.PriceStrategyRuleVO;
import com.edu.teaching.domain.vo.PriceStrategyVO;
import com.edu.teaching.mapper.PriceStrategyMapper;
import com.edu.teaching.mapper.PriceStrategyRuleMapper;
import com.edu.teaching.service.PriceStrategyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 价格策略服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PriceStrategyServiceImpl extends ServiceImpl<PriceStrategyMapper, PriceStrategy> implements PriceStrategyService {

    private final PriceStrategyRuleMapper ruleMapper;

    @Override
    public Page<PriceStrategyVO> pageStrategies(PriceStrategyQueryDTO queryDTO) {
        Page<PriceStrategy> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<PriceStrategy> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(queryDTO.getStrategyName()), PriceStrategy::getStrategyName, queryDTO.getStrategyName())
                .eq(StringUtils.hasText(queryDTO.getStrategyCode()), PriceStrategy::getStrategyCode, queryDTO.getStrategyCode())
                .eq(StringUtils.hasText(queryDTO.getStrategyType()), PriceStrategy::getStrategyType, queryDTO.getStrategyType())
                .eq(queryDTO.getCourseId() != null, PriceStrategy::getCourseId, queryDTO.getCourseId())
                .eq(StringUtils.hasText(queryDTO.getStatus()), PriceStrategy::getStatus, queryDTO.getStatus())
                .eq(queryDTO.getCampusId() != null, PriceStrategy::getCampusId, queryDTO.getCampusId())
                .orderByDesc(PriceStrategy::getPriority)
                .orderByDesc(PriceStrategy::getCreateTime);

        Page<PriceStrategy> strategyPage = page(page, wrapper);

        // 转换为VO
        Page<PriceStrategyVO> voPage = new Page<>(strategyPage.getCurrent(), strategyPage.getSize(), strategyPage.getTotal());
        List<PriceStrategyVO> voList = strategyPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public PriceStrategyVO getStrategyDetail(Long id) {
        PriceStrategy strategy = getById(id);
        if (strategy == null) {
            return null;
        }

        PriceStrategyVO vo = convertToVO(strategy);

        // 查询规则列表
        LambdaQueryWrapper<PriceStrategyRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PriceStrategyRule::getStrategyId, id);
        List<PriceStrategyRule> rules = ruleMapper.selectList(wrapper);

        List<PriceStrategyRuleVO> ruleVOList = rules.stream()
                .map(this::convertRuleToVO)
                .collect(Collectors.toList());
        vo.setRules(ruleVOList);

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createStrategy(PriceStrategyDTO dto) {
        // 检查策略编码是否已存在
        LambdaQueryWrapper<PriceStrategy> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PriceStrategy::getStrategyCode, dto.getStrategyCode());
        if (count(wrapper) > 0) {
            throw new RuntimeException("策略编码已存在");
        }

        // 创建策略
        PriceStrategy strategy = new PriceStrategy();
        BeanUtils.copyProperties(dto, strategy);
        if (!StringUtils.hasText(strategy.getStatus())) {
            strategy.setStatus("ACTIVE");
        }
        save(strategy);

        // 创建规则
        if (dto.getRules() != null && !dto.getRules().isEmpty()) {
            for (PriceStrategyRuleDTO ruleDTO : dto.getRules()) {
                PriceStrategyRule rule = new PriceStrategyRule();
                BeanUtils.copyProperties(ruleDTO, rule);
                rule.setStrategyId(strategy.getId());
                ruleMapper.insert(rule);
            }
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStrategy(PriceStrategyDTO dto) {
        if (dto.getId() == null) {
            throw new RuntimeException("策略ID不能为空");
        }

        PriceStrategy existStrategy = getById(dto.getId());
        if (existStrategy == null) {
            throw new RuntimeException("策略不存在");
        }

        // 检查策略编码是否与其他策略重复
        if (!existStrategy.getStrategyCode().equals(dto.getStrategyCode())) {
            LambdaQueryWrapper<PriceStrategy> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PriceStrategy::getStrategyCode, dto.getStrategyCode())
                    .ne(PriceStrategy::getId, dto.getId());
            if (count(wrapper) > 0) {
                throw new RuntimeException("策略编码已存在");
            }
        }

        // 更新策略
        PriceStrategy strategy = new PriceStrategy();
        BeanUtils.copyProperties(dto, strategy);
        updateById(strategy);

        // 删除旧规则
        LambdaQueryWrapper<PriceStrategyRule> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(PriceStrategyRule::getStrategyId, dto.getId());
        ruleMapper.delete(deleteWrapper);

        // 创建新规则
        if (dto.getRules() != null && !dto.getRules().isEmpty()) {
            for (PriceStrategyRuleDTO ruleDTO : dto.getRules()) {
                PriceStrategyRule rule = new PriceStrategyRule();
                BeanUtils.copyProperties(ruleDTO, rule);
                rule.setStrategyId(strategy.getId());
                rule.setId(null); // 确保创建新记录
                ruleMapper.insert(rule);
            }
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteStrategy(Long id) {
        // 删除策略
        boolean result = removeById(id);

        // 删除关联的规则
        if (result) {
            LambdaQueryWrapper<PriceStrategyRule> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PriceStrategyRule::getStrategyId, id);
            ruleMapper.delete(wrapper);
        }

        return result;
    }

    @Override
    public boolean enableStrategy(Long id) {
        PriceStrategy strategy = new PriceStrategy();
        strategy.setId(id);
        strategy.setStatus("ACTIVE");
        return updateById(strategy);
    }

    @Override
    public boolean disableStrategy(Long id) {
        PriceStrategy strategy = new PriceStrategy();
        strategy.setId(id);
        strategy.setStatus("INACTIVE");
        return updateById(strategy);
    }

    @Override
    public List<PriceStrategyVO> getActiveStrategies(Long courseId, Long campusId) {
        LambdaQueryWrapper<PriceStrategy> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PriceStrategy::getStatus, "ACTIVE")
                .and(w -> w.eq(courseId != null, PriceStrategy::getCourseId, courseId)
                        .or()
                        .isNull(PriceStrategy::getCourseId))
                .and(w -> w.eq(campusId != null, PriceStrategy::getCampusId, campusId)
                        .or()
                        .isNull(PriceStrategy::getCampusId))
                .orderByDesc(PriceStrategy::getPriority);

        List<PriceStrategy> strategies = list(wrapper);
        return strategies.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PriceStrategyVO> getApplicableStrategies(Long courseId, Long campusId) {
        LocalDate now = LocalDate.now();

        LambdaQueryWrapper<PriceStrategy> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PriceStrategy::getStatus, "ACTIVE")
                .and(w -> w.eq(courseId != null, PriceStrategy::getCourseId, courseId)
                        .or()
                        .isNull(PriceStrategy::getCourseId))
                .and(w -> w.eq(campusId != null, PriceStrategy::getCampusId, campusId)
                        .or()
                        .isNull(PriceStrategy::getCampusId))
                .and(w -> w.le(PriceStrategy::getStartDate, now)
                        .or()
                        .isNull(PriceStrategy::getStartDate))
                .and(w -> w.ge(PriceStrategy::getEndDate, now)
                        .or()
                        .isNull(PriceStrategy::getEndDate))
                .orderByDesc(PriceStrategy::getPriority);

        List<PriceStrategy> strategies = list(wrapper);
        return strategies.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public PriceCalculateVO calculatePrice(PriceCalculateDTO request) {
        PriceCalculateVO result = new PriceCalculateVO();
        result.setOriginalPrice(request.getOriginalPrice());
        result.setFinalPrice(request.getOriginalPrice());
        result.setAppliedStrategies(new ArrayList<>());

        if (request.getOriginalPrice() == null || request.getOriginalPrice().compareTo(BigDecimal.ZERO) <= 0) {
            result.setDescription("原价无效");
            return result;
        }

        // 获取适用的策略（按优先级排序）
        List<PriceStrategyVO> strategies = getApplicableStrategies(request.getCourseId(), request.getCampusId());

        if (strategies.isEmpty()) {
            result.setDescription("无适用的价格策略");
            return result;
        }

        BigDecimal currentPrice = request.getOriginalPrice();

        // 按优先级应用策略
        for (PriceStrategyVO strategy : strategies) {
            // 获取策略规则
            LambdaQueryWrapper<PriceStrategyRule> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PriceStrategyRule::getStrategyId, strategy.getId());
            List<PriceStrategyRule> rules = ruleMapper.selectList(wrapper);

            // 查找匹配的规则
            for (PriceStrategyRule rule : rules) {
                if (isRuleMatched(rule, request)) {
                    BigDecimal discountAmount = applyDiscount(currentPrice, rule);
                    currentPrice = currentPrice.subtract(discountAmount);

                    // 记录应用的策略
                    PriceCalculateVO.AppliedStrategyVO appliedStrategy = new PriceCalculateVO.AppliedStrategyVO();
                    appliedStrategy.setStrategyId(strategy.getId());
                    appliedStrategy.setStrategyName(strategy.getStrategyName());
                    appliedStrategy.setStrategyType(strategy.getStrategyType());
                    appliedStrategy.setPriority(strategy.getPriority());
                    appliedStrategy.setRuleId(rule.getId());
                    appliedStrategy.setRuleDescription(buildRuleDescription(rule));
                    appliedStrategy.setDiscountAmount(discountAmount);
                    result.getAppliedStrategies().add(appliedStrategy);

                    break; // 每个策略只应用一个规则
                }
            }
        }

        // 确保价格不为负数
        if (currentPrice.compareTo(BigDecimal.ZERO) < 0) {
            currentPrice = BigDecimal.ZERO;
        }

        result.setFinalPrice(currentPrice.setScale(2, RoundingMode.HALF_UP));
        result.setDiscountAmount(request.getOriginalPrice().subtract(currentPrice).setScale(2, RoundingMode.HALF_UP));

        // 计算折扣率
        if (request.getOriginalPrice().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discountRate = result.getDiscountAmount()
                    .divide(request.getOriginalPrice(), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .setScale(2, RoundingMode.HALF_UP);
            result.setDiscountRate(discountRate);
        }

        result.setDescription(buildCalculationDescription(result));

        return result;
    }

    @Override
    public boolean validateStrategy(PriceStrategyDTO dto) {
        // 验证策略名称
        if (!StringUtils.hasText(dto.getStrategyName())) {
            return false;
        }

        // 验证策略编码
        if (!StringUtils.hasText(dto.getStrategyCode())) {
            return false;
        }

        // 验证策略类型
        if (!StringUtils.hasText(dto.getStrategyType())) {
            return false;
        }
        List<String> validTypes = List.of("TIERED", "MEMBER", "PROMOTION", "CUSTOM");
        if (!validTypes.contains(dto.getStrategyType())) {
            return false;
        }

        // 验证优先级
        if (dto.getPriority() == null || dto.getPriority() < 0) {
            return false;
        }

        // 验证日期范围
        if (dto.getStartDate() != null && dto.getEndDate() != null) {
            if (dto.getStartDate().isAfter(dto.getEndDate())) {
                return false;
            }
        }

        // 验证规则
        if (dto.getRules() == null || dto.getRules().isEmpty()) {
            return false;
        }

        for (PriceStrategyRuleDTO rule : dto.getRules()) {
            // 验证条件类型
            List<String> validConditionTypes = List.of("CLASS_HOURS", "AMOUNT", "MEMBER_LEVEL");
            if (!validConditionTypes.contains(rule.getConditionType())) {
                return false;
            }

            // 验证条件值是否为有效JSON
            if (!StringUtils.hasText(rule.getConditionValue())) {
                return false;
            }
            try {
                JSONUtil.parseObj(rule.getConditionValue());
            } catch (Exception e) {
                return false;
            }

            // 验证折扣类型
            List<String> validDiscountTypes = List.of("PERCENTAGE", "FIXED", "PRICE");
            if (!validDiscountTypes.contains(rule.getDiscountType())) {
                return false;
            }

            // 验证折扣值
            if (rule.getDiscountValue() == null || rule.getDiscountValue().compareTo(BigDecimal.ZERO) <= 0) {
                return false;
            }

            // 百分比折扣值应在0-100之间
            if ("PERCENTAGE".equals(rule.getDiscountType())) {
                if (rule.getDiscountValue().compareTo(new BigDecimal("100")) > 0) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 判断规则是否匹配
     */
    private boolean isRuleMatched(PriceStrategyRule rule, PriceCalculateDTO request) {
        try {
            JSONObject condition = JSONUtil.parseObj(rule.getConditionValue());

            switch (rule.getConditionType()) {
                case "CLASS_HOURS":
                    if (request.getClassHours() == null) {
                        return false;
                    }
                    Integer min = condition.getInt("min");
                    Integer max = condition.getInt("max");
                    if (min != null && request.getClassHours() < min) {
                        return false;
                    }
                    if (max != null && request.getClassHours() > max) {
                        return false;
                    }
                    return true;

                case "AMOUNT":
                    if (request.getAmount() == null) {
                        return false;
                    }
                    BigDecimal minAmount = condition.getBigDecimal("min");
                    BigDecimal maxAmount = condition.getBigDecimal("max");
                    if (minAmount != null && request.getAmount().compareTo(minAmount) < 0) {
                        return false;
                    }
                    if (maxAmount != null && request.getAmount().compareTo(maxAmount) > 0) {
                        return false;
                    }
                    return true;

                case "MEMBER_LEVEL":
                    if (!StringUtils.hasText(request.getMemberLevel())) {
                        return false;
                    }
                    String level = condition.getStr("level");
                    return request.getMemberLevel().equals(level);

                default:
                    return false;
            }
        } catch (Exception e) {
            log.error("规则匹配失败", e);
            return false;
        }
    }

    /**
     * 应用折扣
     */
    private BigDecimal applyDiscount(BigDecimal currentPrice, PriceStrategyRule rule) {
        switch (rule.getDiscountType()) {
            case "PERCENTAGE":
                // 百分比折扣：折扣值表示折后价格的百分比
                BigDecimal discount = new BigDecimal("100").subtract(rule.getDiscountValue());
                return currentPrice.multiply(discount).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

            case "FIXED":
                // 固定金额折扣
                return rule.getDiscountValue();

            case "PRICE":
                // 直接定价：返回差价
                return currentPrice.subtract(rule.getDiscountValue());

            default:
                return BigDecimal.ZERO;
        }
    }

    /**
     * 构建规则描述
     */
    private String buildRuleDescription(PriceStrategyRule rule) {
        try {
            JSONObject condition = JSONUtil.parseObj(rule.getConditionValue());
            StringBuilder desc = new StringBuilder();

            switch (rule.getConditionType()) {
                case "CLASS_HOURS":
                    Integer min = condition.getInt("min");
                    Integer max = condition.getInt("max");
                    if (min != null && max != null) {
                        desc.append(String.format("购买%d-%d课时", min, max));
                    } else if (min != null) {
                        desc.append(String.format("购买%d课时以上", min));
                    } else if (max != null) {
                        desc.append(String.format("购买%d课时以下", max));
                    }
                    break;

                case "AMOUNT":
                    BigDecimal minAmount = condition.getBigDecimal("min");
                    BigDecimal maxAmount = condition.getBigDecimal("max");
                    if (minAmount != null && maxAmount != null) {
                        desc.append(String.format("金额%.2f-%.2f元", minAmount, maxAmount));
                    } else if (minAmount != null) {
                        desc.append(String.format("金额%.2f元以上", minAmount));
                    } else if (maxAmount != null) {
                        desc.append(String.format("金额%.2f元以下", maxAmount));
                    }
                    break;

                case "MEMBER_LEVEL":
                    String level = condition.getStr("level");
                    desc.append(getMemberLevelName(level)).append("会员");
                    break;
            }

            desc.append("，");

            switch (rule.getDiscountType()) {
                case "PERCENTAGE":
                    desc.append(String.format("%.0f折", rule.getDiscountValue()));
                    break;
                case "FIXED":
                    desc.append(String.format("减%.2f元", rule.getDiscountValue()));
                    break;
                case "PRICE":
                    desc.append(String.format("特价%.2f元", rule.getDiscountValue()));
                    break;
            }

            return desc.toString();
        } catch (Exception e) {
            return "规则描述解析失败";
        }
    }

    /**
     * 构建计算说明
     */
    private String buildCalculationDescription(PriceCalculateVO result) {
        if (result.getAppliedStrategies().isEmpty()) {
            return "未应用任何价格策略";
        }

        StringBuilder desc = new StringBuilder();
        desc.append(String.format("原价：%.2f元", result.getOriginalPrice()));

        for (PriceCalculateVO.AppliedStrategyVO strategy : result.getAppliedStrategies()) {
            desc.append(String.format("，应用【%s】%s，优惠%.2f元",
                    strategy.getStrategyName(),
                    strategy.getRuleDescription(),
                    strategy.getDiscountAmount()));
        }

        desc.append(String.format("，最终价格：%.2f元", result.getFinalPrice()));

        return desc.toString();
    }

    /**
     * 转换为VO
     */
    private PriceStrategyVO convertToVO(PriceStrategy strategy) {
        PriceStrategyVO vo = new PriceStrategyVO();
        BeanUtils.copyProperties(strategy, vo);

        // 设置类型名称
        vo.setStrategyTypeName(getStrategyTypeName(strategy.getStrategyType()));

        // 设置状态名称
        vo.setStatusName("ACTIVE".equals(strategy.getStatus()) ? "启用" : "禁用");

        return vo;
    }

    /**
     * 转换规则为VO
     */
    private PriceStrategyRuleVO convertRuleToVO(PriceStrategyRule rule) {
        PriceStrategyRuleVO vo = new PriceStrategyRuleVO();
        BeanUtils.copyProperties(rule, vo);

        // 设置条件类型名称
        vo.setConditionTypeName(getConditionTypeName(rule.getConditionType()));

        // 设置折扣类型名称
        vo.setDiscountTypeName(getDiscountTypeName(rule.getDiscountType()));

        // 设置条件描述
        vo.setConditionDescription(buildConditionDescription(rule));

        // 设置折扣描述
        vo.setDiscountDescription(buildDiscountDescription(rule));

        return vo;
    }

    /**
     * 构建条件描述
     */
    private String buildConditionDescription(PriceStrategyRule rule) {
        try {
            JSONObject condition = JSONUtil.parseObj(rule.getConditionValue());

            switch (rule.getConditionType()) {
                case "CLASS_HOURS":
                    Integer min = condition.getInt("min");
                    Integer max = condition.getInt("max");
                    if (min != null && max != null) {
                        return String.format("%d-%d课时", min, max);
                    } else if (min != null) {
                        return String.format("%d课时以上", min);
                    } else if (max != null) {
                        return String.format("%d课时以下", max);
                    }
                    break;

                case "AMOUNT":
                    BigDecimal minAmount = condition.getBigDecimal("min");
                    BigDecimal maxAmount = condition.getBigDecimal("max");
                    if (minAmount != null && maxAmount != null) {
                        return String.format("%.2f-%.2f元", minAmount, maxAmount);
                    } else if (minAmount != null) {
                        return String.format("%.2f元以上", minAmount);
                    } else if (maxAmount != null) {
                        return String.format("%.2f元以下", maxAmount);
                    }
                    break;

                case "MEMBER_LEVEL":
                    String level = condition.getStr("level");
                    return getMemberLevelName(level);
            }
        } catch (Exception e) {
            log.error("条件描述解析失败", e);
        }

        return rule.getConditionValue();
    }

    /**
     * 构建折扣描述
     */
    private String buildDiscountDescription(PriceStrategyRule rule) {
        switch (rule.getDiscountType()) {
            case "PERCENTAGE":
                return String.format("%.0f折", rule.getDiscountValue());
            case "FIXED":
                return String.format("减%.2f元", rule.getDiscountValue());
            case "PRICE":
                return String.format("特价%.2f元", rule.getDiscountValue());
            default:
                return "";
        }
    }

    /**
     * 获取策略类型名称
     */
    private String getStrategyTypeName(String type) {
        switch (type) {
            case "TIERED":
                return "阶梯价格";
            case "MEMBER":
                return "会员价";
            case "PROMOTION":
                return "促销价";
            case "CUSTOM":
                return "自定义";
            default:
                return type;
        }
    }

    /**
     * 获取条件类型名称
     */
    private String getConditionTypeName(String type) {
        switch (type) {
            case "CLASS_HOURS":
                return "课时数";
            case "AMOUNT":
                return "金额";
            case "MEMBER_LEVEL":
                return "会员等级";
            default:
                return type;
        }
    }

    /**
     * 获取折扣类型名称
     */
    private String getDiscountTypeName(String type) {
        switch (type) {
            case "PERCENTAGE":
                return "百分比折扣";
            case "FIXED":
                return "固定金额";
            case "PRICE":
                return "直接定价";
            default:
                return type;
        }
    }

    /**
     * 获取会员等级名称
     */
    private String getMemberLevelName(String level) {
        switch (level) {
            case "NORMAL":
                return "普通";
            case "SILVER":
                return "银卡";
            case "GOLD":
                return "金卡";
            case "DIAMOND":
                return "钻石";
            default:
                return level;
        }
    }
}
