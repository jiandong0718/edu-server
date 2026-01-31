package com.edu.teaching.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.teaching.domain.dto.PriceStrategyDTO;
import com.edu.teaching.domain.entity.PriceStrategy;

import java.math.BigDecimal;

/**
 * 价格策略服务接口
 */
public interface PriceStrategyService extends IService<PriceStrategy> {

    /**
     * 创建价格策略
     */
    boolean createStrategy(PriceStrategyDTO dto);

    /**
     * 更新价格策略
     */
    boolean updateStrategy(PriceStrategyDTO dto);

    /**
     * 分页查询价格策略
     */
    Page<PriceStrategy> pageStrategies(Integer pageNum, Integer pageSize, String name, String type, Integer status);

    /**
     * 启用价格策略
     */
    boolean enableStrategy(Long id);

    /**
     * 禁用价格策略
     */
    boolean disableStrategy(Long id);

    /**
     * 计算折扣后价格
     */
    BigDecimal calculateDiscountPrice(Long targetId, String targetType, BigDecimal originalPrice, String studentType);

    /**
     * 验证策略配置
     */
    boolean validateStrategy(PriceStrategyDTO dto);
}
