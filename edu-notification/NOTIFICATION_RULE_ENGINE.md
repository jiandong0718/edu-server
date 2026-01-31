# 通知规则引擎实现文档

## 概述

本文档描述了教育机构学生管理系统中自动通知规则引擎的实现。该引擎能够根据业务事件自动触发通知，支持多种通知类型和灵活的规则配置。

## 实现的功能

### 1. 数据库表结构

**表名**: `sys_notification_rule`

**字段说明**:
- `id`: 主键ID
- `rule_name`: 规则名称
- `rule_code`: 规则编码（唯一）
- `description`: 规则描述
- `event_type`: 事件类型（学员注册、合同签署、支付成功等）
- `trigger_condition`: 触发条件（JSON格式）
- `notification_type`: 通知类型（短信、邮件、微信、站内信）
- `template_id`: 消息模板ID
- `receiver_type`: 接收人类型（学员、家长、教师、顾问、管理员）
- `send_time_type`: 发送时间类型（立即、定时、延迟）
- `send_time_config`: 发送时间配置（JSON格式）
- `status`: 状态（启用、禁用）
- `priority`: 优先级
- `campus_id`: 校区ID

### 2. 支持的事件类型

1. **STUDENT_REGISTER** - 学员注册
2. **CONTRACT_SIGNED** - 合同签署
3. **PAYMENT_SUCCESS** - 支付成功
4. **CLASS_REMIND** - 上课提醒
5. **ATTENDANCE_ABSENT** - 缺勤
6. **CLASS_HOUR_LOW** - 课时不足
7. **TRIAL_LESSON** - 试听预约
8. **CONTRACT_EXPIRE** - 合同到期

### 3. 核心组件

#### 3.1 实体类
- `NotificationRule`: 通知规则实体

#### 3.2 事件类
- `BusinessEvent`: 业务事件基类
- `StudentRegisterEvent`: 学员注册事件
- `ContractSignedEvent`: 合同签署事件
- `PaymentSuccessEvent`: 支付成功事件
- `ClassRemindEvent`: 上课提醒事件
- `AttendanceAbsentEvent`: 缺勤事件
- `ClassHourLowEvent`: 课时不足事件
- `TrialLessonEvent`: 试听预约事件
- `ContractExpireEvent`: 合同到期事件

#### 3.3 DTO和VO
- `NotificationRuleDTO`: 创建/更新规则参数
- `NotificationRuleQueryDTO`: 查询参数
- `NotificationRuleVO`: 规则响应数据
- `TriggerConditionDTO`: 触发条件配置
- `SendTimeConfigDTO`: 发送时间配置

#### 3.4 服务层
- `NotificationRuleService`: 规则管理服务接口
- `NotificationRuleServiceImpl`: 规则管理服务实现
- `NotificationRuleEngine`: 规则引擎核心

#### 3.5 监听器
- `NotificationEventListener`: 事件监听器，监听所有业务事件

#### 3.6 定时任务
- `NotificationScheduledTask`: 定时任务，用于周期性检查和触发通知

#### 3.7 控制器
- `NotificationRuleController`: 规则管理API
- `NotificationRuleTestController`: 规则测试API

## API接口

### 规则管理接口

1. **分页查询规则**
   - `GET /notification/rule/page`
   - 参数: `ruleName`, `ruleCode`, `eventType`, `status`, `campusId`, `pageNum`, `pageSize`

2. **获取规则列表**
   - `GET /notification/rule/list`
   - 参数: 同分页查询

3. **获取规则详情**
   - `GET /notification/rule/{id}`

4. **创建规则**
   - `POST /notification/rule`
   - Body: `NotificationRuleDTO`

5. **更新规则**
   - `PUT /notification/rule/{id}`
   - Body: `NotificationRuleDTO`

6. **删除规则**
   - `DELETE /notification/rule/{id}`

7. **启用/禁用规则**
   - `PUT /notification/rule/{id}/status`
   - 参数: `status`

8. **获取事件类型列表**
   - `GET /notification/rule/event-types`

9. **获取通知类型列表**
   - `GET /notification/rule/notification-types`

10. **获取接收人类型列表**
    - `GET /notification/rule/receiver-types`

11. **获取发送时间类型列表**
    - `GET /notification/rule/send-time-types`

### 测试接口

1. **测试学员注册事件**
   - `POST /notification/rule/test/student-register`

2. **测试合同签署事件**
   - `POST /notification/rule/test/contract-signed`

3. **测试支付成功事件**
   - `POST /notification/rule/test/payment-success`

4. **测试上课提醒事件**
   - `POST /notification/rule/test/class-remind`

5. **测试缺勤事件**
   - `POST /notification/rule/test/attendance-absent`

6. **测试课时不足事件**
   - `POST /notification/rule/test/class-hour-low`

7. **测试试听预约事件**
   - `POST /notification/rule/test/trial-lesson`

8. **测试合同到期事件**
   - `POST /notification/rule/test/contract-expire`

## 规则配置示例

### 1. 触发条件配置

```json
{
  "type": "AND",
  "conditions": [
    {
      "field": "remainingClassHours",
      "operator": "<=",
      "value": 5
    },
    {
      "field": "studentStatus",
      "operator": "==",
      "value": "ACTIVE"
    }
  ]
}
```

**支持的操作符**:
- `==`: 等于
- `!=`: 不等于
- `>`: 大于
- `<`: 小于
- `>=`: 大于等于
- `<=`: 小于等于
- `in`: 包含在列表中
- `not_in`: 不包含在列表中
- `contains`: 字符串包含

### 2. 发送时间配置

#### 立即发送
```json
{
  "type": "IMMEDIATE"
}
```

#### 延迟发送（事件前30分钟）
```json
{
  "type": "DELAYED",
  "delay": 30,
  "unit": "MINUTES",
  "beforeEvent": true
}
```

#### 延迟发送（事件后7天）
```json
{
  "type": "DELAYED",
  "delay": 7,
  "unit": "DAYS",
  "beforeEvent": false
}
```

## 使用方法

### 1. 在业务模块中发布事件

```java
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void registerStudent(StudentDTO dto) {
        // 保存学员信息
        Student student = saveStudent(dto);

        // 发布学员注册事件
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("studentId", student.getId());
        eventData.put("studentName", student.getName());
        eventData.put("phone", student.getPhone());
        eventData.put("registerTime", LocalDateTime.now());

        StudentRegisterEvent event = new StudentRegisterEvent(
            this, eventData, student.getCampusId(), student.getId()
        );
        eventPublisher.publishEvent(event);
    }
}
```

### 2. 规则引擎自动处理

规则引擎会自动：
1. 监听事件
2. 匹配启用的规则
3. 评估触发条件
4. 发送通知

### 3. 定时任务

系统包含以下定时任务：

1. **上课提醒任务**: 每小时执行，检查即将开始的课程
2. **合同到期提醒任务**: 每天凌晨1点执行，检查即将到期的合同
3. **课时不足检查任务**: 每天早上8点执行，检查课时不足的学员

## 特性

1. **灵活的规则配置**: 支持复杂的触发条件和多种发送时间策略
2. **异步处理**: 事件监听器使用 `@Async` 注解，不阻塞主业务流程
3. **缓存优化**: 规则查询使用缓存，提高性能
4. **优先级支持**: 规则按优先级执行
5. **多校区支持**: 支持全局规则和校区级规则
6. **多通知类型**: 支持短信、邮件、微信、站内信
7. **多接收人类型**: 支持学员、家长、教师、顾问、管理员
8. **测试接口**: 提供完整的测试接口，方便调试

## 注意事项

1. **性能优化**: 规则引擎使用缓存减少数据库查询
2. **异步处理**: 事件处理不会阻塞主业务流程
3. **错误处理**: 单个规则失败不影响其他规则执行
4. **日志记录**: 完整的日志记录便于问题排查
5. **扩展性**: 易于添加新的事件类型和规则

## 后续优化建议

1. 实现通知发送失败重试机制
2. 添加通知频率限制（防止骚扰）
3. 实现规则测试功能（模拟执行）
4. 添加规则执行统计和监控
5. 支持更复杂的条件表达式（如脚本引擎）
6. 实现规则版本管理
7. 添加规则审批流程

## 文件清单

### 数据库迁移
- `/edu-admin/src/main/resources/db/migration/V1.0.27__add_notification_rule_table.sql`

### 实体类
- `/edu-notification/src/main/java/com/edu/notification/domain/entity/NotificationRule.java`

### 事件类
- `/edu-notification/src/main/java/com/edu/notification/event/BusinessEvent.java`
- `/edu-notification/src/main/java/com/edu/notification/event/StudentRegisterEvent.java`
- `/edu-notification/src/main/java/com/edu/notification/event/ContractSignedEvent.java`
- `/edu-notification/src/main/java/com/edu/notification/event/PaymentSuccessEvent.java`
- `/edu-notification/src/main/java/com/edu/notification/event/ClassRemindEvent.java`
- `/edu-notification/src/main/java/com/edu/notification/event/AttendanceAbsentEvent.java`
- `/edu-notification/src/main/java/com/edu/notification/event/ClassHourLowEvent.java`
- `/edu-notification/src/main/java/com/edu/notification/event/TrialLessonEvent.java`
- `/edu-notification/src/main/java/com/edu/notification/event/ContractExpireEvent.java`

### DTO和VO
- `/edu-notification/src/main/java/com/edu/notification/domain/dto/NotificationRuleDTO.java`
- `/edu-notification/src/main/java/com/edu/notification/domain/dto/NotificationRuleQueryDTO.java`
- `/edu-notification/src/main/java/com/edu/notification/domain/dto/TriggerConditionDTO.java`
- `/edu-notification/src/main/java/com/edu/notification/domain/dto/SendTimeConfigDTO.java`
- `/edu-notification/src/main/java/com/edu/notification/domain/vo/NotificationRuleVO.java`

### Mapper
- `/edu-notification/src/main/java/com/edu/notification/mapper/NotificationRuleMapper.java`

### 服务层
- `/edu-notification/src/main/java/com/edu/notification/service/NotificationRuleService.java`
- `/edu-notification/src/main/java/com/edu/notification/service/impl/NotificationRuleServiceImpl.java`
- `/edu-notification/src/main/java/com/edu/notification/service/NotificationRuleEngine.java`

### 监听器
- `/edu-notification/src/main/java/com/edu/notification/listener/NotificationEventListener.java`

### 定时任务
- `/edu-notification/src/main/java/com/edu/notification/task/NotificationScheduledTask.java`

### 控制器
- `/edu-notification/src/main/java/com/edu/notification/controller/NotificationRuleController.java`
- `/edu-notification/src/main/java/com/edu/notification/controller/NotificationRuleTestController.java`
