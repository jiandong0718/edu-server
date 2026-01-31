package com.edu.notification.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.exception.BusinessException;
import com.edu.notification.domain.dto.NotificationRuleDTO;
import com.edu.notification.domain.dto.NotificationRuleQueryDTO;
import com.edu.notification.domain.entity.NotificationRule;
import com.edu.notification.domain.vo.NotificationRuleVO;
import com.edu.notification.mapper.NotificationRuleMapper;
import com.edu.notification.service.NotificationRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 通知规则Service实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationRuleServiceImpl extends ServiceImpl<NotificationRuleMapper, NotificationRule> implements NotificationRuleService {

    @Override
    public IPage<NotificationRuleVO> page(NotificationRuleQueryDTO queryDTO, int pageNum, int pageSize) {
        Page<NotificationRule> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<NotificationRule> wrapper = buildQueryWrapper(queryDTO);
        wrapper.orderByDesc(NotificationRule::getPriority)
                .orderByDesc(NotificationRule::getCreateTime);

        IPage<NotificationRule> resultPage = page(page, wrapper);
        return resultPage.convert(this::convertToVO);
    }

    @Override
    public List<NotificationRuleVO> list(NotificationRuleQueryDTO queryDTO) {
        LambdaQueryWrapper<NotificationRule> wrapper = buildQueryWrapper(queryDTO);
        wrapper.orderByDesc(NotificationRule::getPriority)
                .orderByDesc(NotificationRule::getCreateTime);

        List<NotificationRule> list = list(wrapper);
        return list.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public NotificationRuleVO getById(Long id) {
        NotificationRule rule = getBaseMapper().selectById(id);
        if (rule == null) {
            throw new BusinessException("通知规则不存在");
        }
        return convertToVO(rule);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "notificationRules", allEntries = true)
    public Long create(NotificationRuleDTO dto) {
        // 检查规则编码是否已存在
        NotificationRule existRule = getByRuleCode(dto.getRuleCode());
        if (existRule != null) {
            throw new BusinessException("规则编码已存在");
        }

        NotificationRule rule = new NotificationRule();
        BeanUtil.copyProperties(dto, rule);

        // 设置默认状态
        if (StrUtil.isBlank(rule.getStatus())) {
            rule.setStatus("ACTIVE");
        }

        save(rule);
        log.info("创建通知规则成功，规则ID：{}", rule.getId());
        return rule.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "notificationRules", allEntries = true)
    public void update(Long id, NotificationRuleDTO dto) {
        NotificationRule rule = getBaseMapper().selectById(id);
        if (rule == null) {
            throw new BusinessException("通知规则不存在");
        }

        // 如果修改了规则编码，检查新编码是否已存在
        if (!rule.getRuleCode().equals(dto.getRuleCode())) {
            NotificationRule existRule = getByRuleCode(dto.getRuleCode());
            if (existRule != null && !existRule.getId().equals(id)) {
                throw new BusinessException("规则编码已存在");
            }
        }

        BeanUtil.copyProperties(dto, rule, "id");
        updateById(rule);
        log.info("更新通知规则成功，规则ID：{}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "notificationRules", allEntries = true)
    public void delete(Long id) {
        NotificationRule rule = getBaseMapper().selectById(id);
        if (rule == null) {
            throw new BusinessException("通知规则不存在");
        }

        removeById(id);
        log.info("删除通知规则成功，规则ID：{}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "notificationRules", allEntries = true)
    public void updateStatus(Long id, String status) {
        NotificationRule rule = getBaseMapper().selectById(id);
        if (rule == null) {
            throw new BusinessException("通知规则不存在");
        }

        rule.setStatus(status);
        updateById(rule);
        log.info("更新通知规则状态成功，规则ID：{}，状态：{}", id, status);
    }

    @Override
    @Cacheable(value = "notificationRules", key = "'eventType:' + #eventType")
    public List<NotificationRule> getActiveRulesByEventType(String eventType) {
        LambdaQueryWrapper<NotificationRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NotificationRule::getEventType, eventType)
                .eq(NotificationRule::getStatus, "ACTIVE")
                .orderByDesc(NotificationRule::getPriority);

        return list(wrapper);
    }

    @Override
    public NotificationRule getByRuleCode(String ruleCode) {
        LambdaQueryWrapper<NotificationRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NotificationRule::getRuleCode, ruleCode);
        return getOne(wrapper);
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<NotificationRule> buildQueryWrapper(NotificationRuleQueryDTO queryDTO) {
        LambdaQueryWrapper<NotificationRule> wrapper = new LambdaQueryWrapper<>();

        if (queryDTO != null) {
            wrapper.like(StrUtil.isNotBlank(queryDTO.getRuleName()),
                    NotificationRule::getRuleName, queryDTO.getRuleName())
                    .eq(StrUtil.isNotBlank(queryDTO.getRuleCode()),
                    NotificationRule::getRuleCode, queryDTO.getRuleCode())
                    .eq(StrUtil.isNotBlank(queryDTO.getEventType()),
                    NotificationRule::getEventType, queryDTO.getEventType())
                    .eq(StrUtil.isNotBlank(queryDTO.getStatus()),
                    NotificationRule::getStatus, queryDTO.getStatus())
                    .eq(queryDTO.getCampusId() != null,
                    NotificationRule::getCampusId, queryDTO.getCampusId());
        }

        return wrapper;
    }

    /**
     * 转换为VO
     */
    private NotificationRuleVO convertToVO(NotificationRule rule) {
        NotificationRuleVO vo = new NotificationRuleVO();
        BeanUtil.copyProperties(rule, vo);
        return vo;
    }
}
