package com.edu.notification.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.edu.notification.domain.dto.NotificationRuleDTO;
import com.edu.notification.domain.dto.NotificationRuleQueryDTO;
import com.edu.notification.domain.entity.NotificationRule;
import com.edu.notification.domain.vo.NotificationRuleVO;

import java.util.List;

/**
 * 通知规则Service
 */
public interface NotificationRuleService {

    /**
     * 分页查询规则
     */
    IPage<NotificationRuleVO> page(NotificationRuleQueryDTO queryDTO, int pageNum, int pageSize);

    /**
     * 获取规则列表
     */
    List<NotificationRuleVO> list(NotificationRuleQueryDTO queryDTO);

    /**
     * 获取规则详情
     */
    NotificationRuleVO getById(Long id);

    /**
     * 创建规则
     */
    Long create(NotificationRuleDTO dto);

    /**
     * 更新规则
     */
    void update(Long id, NotificationRuleDTO dto);

    /**
     * 删除规则
     */
    void delete(Long id);

    /**
     * 启用/禁用规则
     */
    void updateStatus(Long id, String status);

    /**
     * 根据事件类型获取启用的规则列表
     */
    List<NotificationRule> getActiveRulesByEventType(String eventType);

    /**
     * 根据规则编码获取规则
     */
    NotificationRule getByRuleCode(String ruleCode);
}
