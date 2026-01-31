package com.edu.teaching.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.teaching.domain.dto.PriceCalculateDTO;
import com.edu.teaching.domain.dto.PriceStrategyDTO;
import com.edu.teaching.domain.dto.PriceStrategyQueryDTO;
import com.edu.teaching.domain.entity.PriceStrategy;
import com.edu.teaching.domain.vo.PriceCalculateVO;
import com.edu.teaching.domain.vo.PriceStrategyVO;

import java.util.List;

/**
 * 价格策略服务接口
 */
public interface PriceStrategyService extends IService<PriceStrategy> {

    /**
     * 分页查询价格策略
     */
    Page<PriceStrategyVO> pageStrategies(PriceStrategyQueryDTO queryDTO);

    /**
     * 获取价格策略详情（包含规则列表）
     */
    PriceStrategyVO getStrategyDetail(Long id);

    /**
     * 创建价格策略（包含规则）
     */
    boolean createStrategy(PriceStrategyDTO dto);

    /**
     * 更新价格策略
     */
    boolean updateStrategy(PriceStrategyDTO dto);

    /**
     * 删除价格策略
     */
    boolean deleteStrategy(Long id);

    /**
     * 启用价格策略
     */
    boolean enableStrategy(Long id);

    /**
     * 禁用价格策略
     */
    boolean disableStrategy(Long id);

    /**
     * 获取启用的价格策略列表
     */
    List<PriceStrategyVO> getActiveStrategies(Long courseId, Long campusId);

    /**
     * 获取适用的价格策略列表
     */
    List<PriceStrategyVO> getApplicableStrategies(Long courseId, Long campusId);

    /**
     * 计算价格（根据策略和条件）
     */
    PriceCalculateVO calculatePrice(PriceCalculateDTO request);

    /**
     * 验证策略配置
     */
    boolean validateStrategy(PriceStrategyDTO dto);
}
