# 通知规则引擎快速测试指南

## 前置条件

1. 启动应用程序
2. 数据库迁移已执行（V1.0.27__add_notification_rule_table.sql）
3. 访问 Knife4j 文档: http://localhost:8080/doc.html

## 测试步骤

### 1. 查看默认规则

访问接口查看系统预置的8条默认规则：

```bash
GET http://localhost:8080/notification/rule/list
```

默认规则包括：
- 学员注册欢迎通知
- 合同签署通知
- 支付成功通知
- 上课提醒
- 缺勤通知
- 课时不足提醒
- 试听预约通知
- 合同到期提醒

### 2. 测试学员注册事件

```bash
POST http://localhost:8080/notification/rule/test/student-register
Content-Type: application/json

{
  "studentId": 1,
  "campusId": 1
}
```

**预期结果**:
- 触发"学员注册欢迎通知"规则
- 创建站内信和短信通知
- 接收人为学员

### 3. 测试课时不足事件

```bash
POST http://localhost:8080/notification/rule/test/class-hour-low
Content-Type: application/json

{
  "accountId": 1,
  "remainingClassHours": 3,
  "campusId": 1
}
```

**预期结果**:
- 触发"课时不足提醒"规则
- 因为剩余课时(3) <= 5，满足触发条件
- 创建站内信和短信通知
- 接收人为学员和顾问

### 4. 测试上课提醒事件

```bash
POST http://localhost:8080/notification/rule/test/class-remind
Content-Type: application/json

{
  "classId": 1,
  "campusId": 1
}
```

**预期结果**:
- 触发"上课提醒"规则
- 创建站内信和短信通知
- 接收人为学员和教师

### 5. 查看生成的通知

查看系统生成的通知记录：

```bash
GET http://localhost:8080/notification/page?pageNum=1&pageSize=10
```

### 6. 创建自定义规则

创建一个新的通知规则：

```bash
POST http://localhost:8080/notification/rule
Content-Type: application/json

{
  "ruleName": "大额支付提醒",
  "ruleCode": "LARGE_PAYMENT_NOTIFY",
  "description": "支付金额超过10000元时提醒管理员",
  "eventType": "PAYMENT_SUCCESS",
  "triggerCondition": "{\"type\":\"AND\",\"conditions\":[{\"field\":\"amount\",\"operator\":\">\",\"value\":10000}]}",
  "notificationType": "SYSTEM,SMS",
  "receiverType": "ADMIN",
  "sendTimeType": "IMMEDIATE",
  "status": "ACTIVE",
  "priority": 20
}
```

### 7. 测试自定义规则

测试大额支付（金额5000，不触发）：

```bash
POST http://localhost:8080/notification/rule/test/payment-success
Content-Type: application/json

{
  "paymentId": 1,
  "campusId": 1
}
```

修改测试数据，使金额超过10000（需要在事件数据中设置）。

### 8. 禁用规则

禁用某个规则：

```bash
PUT http://localhost:8080/notification/rule/1/status?status=INACTIVE
```

### 9. 查看规则详情

```bash
GET http://localhost:8080/notification/rule/1
```

### 10. 获取配置选项

获取事件类型列表：
```bash
GET http://localhost:8080/notification/rule/event-types
```

获取通知类型列表：
```bash
GET http://localhost:8080/notification/rule/notification-types
```

获取接收人类型列表：
```bash
GET http://localhost:8080/notification/rule/receiver-types
```

获取发送时间类型列表：
```bash
GET http://localhost:8080/notification/rule/send-time-types
```

## 验证规则引擎工作流程

### 流程说明

1. **事件发布**: 业务模块通过 `ApplicationEventPublisher` 发布事件
2. **事件监听**: `NotificationEventListener` 监听事件（异步）
3. **规则匹配**: `NotificationRuleEngine` 根据事件类型匹配启用的规则
4. **条件评估**: 评估规则的触发条件
5. **通知发送**: 创建通知记录并发送

### 日志查看

查看应用日志，应该能看到类似以下内容：

```
INFO  c.e.n.listener.NotificationEventListener - 接收到学员注册事件，学员ID：1
INFO  c.e.n.service.NotificationRuleEngine - 处理业务事件：StudentRegisterEvent, 事件类型：STUDENT_REGISTER
INFO  c.e.n.service.NotificationRuleEngine - 触发通知规则：学员注册欢迎通知, 规则编码：STUDENT_REGISTER_WELCOME
```

## 常见问题

### Q1: 规则没有触发？

检查：
1. 规则状态是否为 ACTIVE
2. 事件类型是否匹配
3. 触发条件是否满足
4. 校区ID是否匹配（规则的campusId为null表示全局）

### Q2: 通知没有发送？

检查：
1. 通知服务是否正常
2. 消息模板是否存在
3. 查看通知表中的 send_status 字段

### Q3: 如何调试规则条件？

1. 使用测试接口发送事件
2. 查看日志中的条件评估过程
3. 检查事件数据中的字段值

## 高级测试场景

### 场景1: 复杂条件规则

创建一个规则，只在工作日的工作时间发送通知：

```json
{
  "ruleName": "工作时间通知",
  "ruleCode": "WORK_TIME_NOTIFY",
  "eventType": "STUDENT_REGISTER",
  "triggerCondition": "{\"type\":\"AND\",\"conditions\":[{\"field\":\"dayOfWeek\",\"operator\":\"in\",\"value\":[1,2,3,4,5]},{\"field\":\"hour\",\"operator\":\">=\",\"value\":9},{\"field\":\"hour\",\"operator\":\"<\",\"value\":18}]}",
  "notificationType": "SYSTEM",
  "receiverType": "ADMIN",
  "sendTimeType": "IMMEDIATE",
  "status": "ACTIVE",
  "priority": 10
}
```

### 场景2: 延迟发送规则

创建一个规则，在课程开始前30分钟发送提醒：

```json
{
  "ruleName": "课前30分钟提醒",
  "ruleCode": "CLASS_30MIN_REMIND",
  "eventType": "CLASS_REMIND",
  "sendTimeType": "DELAYED",
  "sendTimeConfig": "{\"type\":\"DELAYED\",\"delay\":30,\"unit\":\"MINUTES\",\"beforeEvent\":true}",
  "notificationType": "SMS,SYSTEM",
  "receiverType": "STUDENT,TEACHER",
  "status": "ACTIVE",
  "priority": 20
}
```

### 场景3: 多条件OR规则

创建一个规则，当课时不足或合同即将到期时通知：

```json
{
  "ruleName": "续费提醒",
  "ruleCode": "RENEWAL_REMIND",
  "eventType": "CLASS_HOUR_LOW",
  "triggerCondition": "{\"type\":\"OR\",\"conditions\":[{\"field\":\"remainingClassHours\",\"operator\":\"<=\",\"value\":3},{\"field\":\"daysToExpire\",\"operator\":\"<=\",\"value\":7}]}",
  "notificationType": "SMS,SYSTEM",
  "receiverType": "STUDENT,ADVISOR",
  "sendTimeType": "IMMEDIATE",
  "status": "ACTIVE",
  "priority": 15
}
```

## 性能测试

### 批量事件测试

可以编写脚本批量发送事件，测试规则引擎的性能：

```bash
for i in {1..100}; do
  curl -X POST "http://localhost:8080/notification/rule/test/student-register" \
    -H "Content-Type: application/json" \
    -d "{\"studentId\": $i, \"campusId\": 1}"
done
```

### 监控指标

关注以下指标：
1. 事件处理时间
2. 规则匹配时间
3. 通知创建时间
4. 缓存命中率

## 下一步

1. 集成到实际业务模块中
2. 配置消息模板
3. 配置短信/邮件/微信发送服务
4. 实现前端管理界面
5. 添加规则执行统计和监控
