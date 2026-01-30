package com.edu.notification.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.Result;
import com.edu.notification.domain.entity.MessageTemplate;
import com.edu.notification.service.MessageTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 消息模板控制器
 */
@RestController
@RequestMapping("/notification/template")
@RequiredArgsConstructor
public class MessageTemplateController {

    private final MessageTemplateService messageTemplateService;

    /**
     * 分页查询模板列表
     */
    @GetMapping("/page")
    public Result<IPage<MessageTemplate>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            MessageTemplate query) {
        IPage<MessageTemplate> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<MessageTemplate> wrapper = new LambdaQueryWrapper<>();
        if (query.getType() != null) {
            wrapper.eq(MessageTemplate::getType, query.getType());
        }
        if (query.getChannel() != null) {
            wrapper.eq(MessageTemplate::getChannel, query.getChannel());
        }
        if (query.getStatus() != null) {
            wrapper.eq(MessageTemplate::getStatus, query.getStatus());
        }
        if (query.getName() != null) {
            wrapper.like(MessageTemplate::getName, query.getName());
        }
        wrapper.orderByDesc(MessageTemplate::getCreateTime);
        return Result.success(messageTemplateService.page(page, wrapper));
    }

    /**
     * 获取模板详情
     */
    @GetMapping("/{id}")
    public Result<MessageTemplate> getById(@PathVariable Long id) {
        return Result.success(messageTemplateService.getById(id));
    }

    /**
     * 根据编码获取模板
     */
    @GetMapping("/code/{code}")
    public Result<MessageTemplate> getByCode(@PathVariable String code) {
        return Result.success(messageTemplateService.getByCode(code));
    }

    /**
     * 创建模板
     */
    @PostMapping
    public Result<Void> create(@RequestBody MessageTemplate template) {
        messageTemplateService.save(template);
        return Result.success();
    }

    /**
     * 更新模板
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody MessageTemplate template) {
        template.setId(id);
        messageTemplateService.updateById(template);
        return Result.success();
    }

    /**
     * 删除模板
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        messageTemplateService.removeById(id);
        return Result.success();
    }
}
