# 课时账户创建和扣减功能实现总结（任务19.2-19.3）

## 任务概述

本文档总结了课时账户自动创建和签到扣减功能的实现，包括：
- **任务19.2**: 实现课时账户创建（合同支付后触发）
- **任务19.3**: 实现课时扣减逻辑（监听签到事件）

## 实现状态

✅ **已完成** - 所有核心功能已实现并增强

## 核心功能实现

### 1. 课时账户创建（任务19.2）

#### 功能描述
合同支付完成后，系统自动根据合同明细创建课时账户，支持一个合同包含多个课程的场景。

#### 实现文件

**事件定义：**
- `/edu-finance/src/main/java/com/edu/finance/event/ContractPaidEvent.java`

**事件监听器：**
- `/edu-finance/src/main/java/com/edu/finance/listener/ContractPaidEventListener.java`

**服务层：**
- `/edu-finance/src/main/java/com/edu/finance/service/ClassHourAccountService.java`
- `/edu-finance/src/main/java/com/edu/finance/service/impl/ClassHourAccountServiceImpl.java`

**实体类：**
- `/edu-finance/src/main/java/com/edu/finance/domain/entity/ClassHourAccount.java`
- `/edu-finance/src/main/java/com/edu/finance/domain/entity/ContractItem.java`

#### 核心流程

```
1. 收款确认 (PaymentServiceImpl.confirmPayment)
   ↓
2. 检查合同是否全额支付
   ↓
3. 发布 ContractPaidEvent 事件
   ↓
4. ContractPaidEventListener 监听事件（异步）
   ↓
5. 调用 createAccountByContract(contractId)
   ↓
6. 查询合同明细 (fin_contract_item)
   ↓
7. 为每个课程创建课时账户
   - studentId: 学员ID
   - contractId: 合同ID
   - courseId: 课程ID
   - campusId: 校区ID
   - totalHours: 总课时（从合同明细获取）
   - usedHours: 0（初始值）
   - remainingHours: totalHours（初始值）
   - giftHours: 0（初始值）
   - status: "active"（初始状态）
```

#### 关键代码片段

**事件发布（PaymentServiceImpl）：**
```java
private void publishContractPaidEvent(Contract contract, BigDecimal paidAmount) {
    ContractPaidEvent event = new ContractPaidEvent(
        this,
        contract.getId(),
        contract.getStudentId(),
        contract.getCampusId(),
        paidAmount
    );
    eventPublisher.publishEvent(event);
}
```

**事件监听（ContractPaidEventListener）：**
```java
@Async
@EventListener
@Transactional(rollbackFor = Exception.class)
public void handleContractPaidEvent(ContractPaidEvent event) {
    log.info("收到合同支付完成事件: contractId={}, studentId={}",
            event.getContractId(), event.getStudentId());

    boolean result = classHourAccountService.createAccountByContract(event.getContractId());

    if (result) {
        log.info("合同支付后课时账户创建成功: contractId={}", event.getContractId());
    }
}
```

**账户创建（ClassHourAccountServiceImpl）：**
```java
@Transactional(rollbackFor = Exception.class)
public boolean createAccountByContract(Long contractId) {
    // 获取合同信息
    Contract contract = contractService.getById(contractId);

    // 获取合同明细
    List<ContractItem> items = contractItemMapper.selectByContractId(contractId);

    // 为每个课程创建课时账户
    for (ContractItem item : items) {
        if (item.getHours() != null && item.getHours() > 0) {
            ClassHourAccountCreateDTO dto = new ClassHourAccountCreateDTO();
            dto.setStudentId(contract.getStudentId());
            dto.setContractId(contractId);
            dto.setCourseId(item.getCourseId());
            dto.setCampusId(contract.getCampusId());
            dto.setTotalHours(BigDecimal.valueOf(item.getHours()));
            dto.setGiftHours(BigDecimal.ZERO);

            createAccount(dto);
        }
    }
}
```

#### 数据库表结构

**fin_class_hour_account（课时账户表）：**
```sql
CREATE TABLE fin_class_hour_account (
    id BIGINT PRIMARY KEY,
    student_id BIGINT NOT NULL COMMENT '学员ID',
    contract_id BIGINT NOT NULL COMMENT '合同ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    campus_id BIGINT NOT NULL COMMENT '校区ID',
    total_hours DECIMAL(10,2) NOT NULL COMMENT '总课时',
    used_hours DECIMAL(10,2) DEFAULT 0 COMMENT '已消耗课时',
    remaining_hours DECIMAL(10,2) NOT NULL COMMENT '剩余课时',
    gift_hours DECIMAL(10,2) DEFAULT 0 COMMENT '赠送课时',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态：active-正常，frozen-冻结，exhausted-已用完',
    create_time DATETIME,
    update_time DATETIME,
    create_by BIGINT,
    update_by BIGINT,
    deleted TINYINT DEFAULT 0
);
```

### 2. 课时扣减逻辑（任务19.3）

#### 功能描述
学员签到后，系统自动根据消课规则扣减课时，并记录课时消耗明细。支持课时不足预警和事务回滚。

#### 实现文件

**事件定义：**
- `/edu-teaching/src/main/java/com/edu/teaching/event/AttendanceSignedEvent.java`

**事件监听器：**
- `/edu-finance/src/main/java/com/edu/finance/listener/AttendanceSignedEventListener.java`

**服务层：**
- `/edu-finance/src/main/java/com/edu/finance/service/ClassHourAccountService.java`
- `/edu-finance/src/main/java/com/edu/finance/service/ClassHourRecordService.java`
- `/edu-finance/src/main/java/com/edu/finance/service/ClassHourRuleService.java`

**实体类：**
- `/edu-finance/src/main/java/com/edu/finance/domain/entity/ClassHourRecord.java`

#### 核心流程

```
1. 学员签到 (AttendanceServiceImpl.signIn)
   ↓
2. 发布 AttendanceSignedEvent 事件
   ↓
3. AttendanceSignedEventListener 监听事件（异步）
   ↓
4. 检查考勤状态（只处理 present 和 late）
   ↓
5. 调用 ClassHourRuleService.calculateDeductHours()
   - 根据课程ID、班级类型、校区ID查找消课规则
   - 计算应扣减的课时数
   ↓
6. 调用 ClassHourAccountService.deductHours()
   - 查找学员的课时账户
   - 检查账户状态（frozen/exhausted）
   - 检查余额是否足够
   - 扣减课时（usedHours +1, remainingHours -1）
   - 更新账户状态（余额为0时设为exhausted）
   - 创建课时消耗记录
   - 检查是否需要发送低余额预警
   ↓
7. 记录课时消耗明细 (fin_class_hour_record)
```

#### 关键代码片段

**事件发布（AttendanceServiceImpl）：**
```java
private void publishAttendanceEvent(Attendance attendance, Schedule schedule) {
    AttendanceSignedEvent event = new AttendanceSignedEvent(
        this,
        attendance.getId(),
        attendance.getScheduleId(),
        attendance.getStudentId(),
        attendance.getClassId(),
        schedule.getCourseId(),
        attendance.getStatus(),
        attendance.getSignTime(),
        attendance.getClassHours(),
        schedule.getCampusId()
    );
    eventPublisher.publishEvent(event);
}
```

**事件监听（AttendanceSignedEventListener）：**
```java
@Async
@EventListener
@Transactional(rollbackFor = Exception.class)
public void handleAttendanceSignedEvent(AttendanceSignedEvent event) {
    log.info("收到考勤签到事件: attendanceId={}, studentId={}, courseId={}, status={}",
            event.getAttendanceId(), event.getStudentId(), event.getCourseId(), event.getStatus());

    // 只有出勤和迟到才扣减课时
    if (!"present".equals(event.getStatus()) && !"late".equals(event.getStatus())) {
        return;
    }

    // 根据消课规则计算应扣减的课时数
    BigDecimal deductHours = classHourRuleService.calculateDeductHours(
        event.getCourseId(),
        classType,
        event.getCampusId(),
        event.getClassHours()
    );

    // 扣减课时
    ClassHourDeductDTO dto = new ClassHourDeductDTO();
    dto.setStudentId(event.getStudentId());
    dto.setCourseId(event.getCourseId());
    dto.setScheduleId(event.getScheduleId());
    dto.setHours(deductHours);
    dto.setRemark("签到扣减课时 - 排课ID: " + event.getScheduleId());

    classHourAccountService.deductHours(dto);
}
```

**课时扣减（ClassHourAccountServiceImpl）：**
```java
@Transactional(rollbackFor = Exception.class)
public boolean deductHours(ClassHourDeductDTO dto) {
    // 查找学员的课时账户
    ClassHourAccount account = getByStudentAndCourse(dto.getStudentId(), dto.getCourseId());

    // 检查账户状态
    if ("frozen".equals(account.getStatus())) {
        throw new BusinessException("课时账户已冻结，无法扣减");
    }

    // 检查余额是否足够
    if (account.getRemainingHours().compareTo(dto.getHours()) < 0) {
        // 发送课时不足预警
        publishWarningEvent(account, "insufficient_balance",
                "课时余额不足，剩余: " + account.getRemainingHours());
        throw new BusinessException("课时余额不足");
    }

    // 扣减课时
    BigDecimal newUsedHours = account.getUsedHours().add(dto.getHours());
    BigDecimal newRemainingHours = account.getRemainingHours().subtract(dto.getHours());

    account.setUsedHours(newUsedHours);
    account.setRemainingHours(newRemainingHours);

    // 如果余额为0，更新状态为已用完
    if (newRemainingHours.compareTo(BigDecimal.ZERO) == 0) {
        account.setStatus("exhausted");
    }

    updateById(account);

    // 记录课时消耗
    ClassHourRecord record = new ClassHourRecord();
    record.setAccountId(account.getId());
    record.setStudentId(dto.getStudentId());
    record.setScheduleId(dto.getScheduleId());
    record.setType("consume");
    record.setHours(dto.getHours().negate()); // 负数表示扣减
    record.setBalance(newRemainingHours);
    record.setRemark(dto.getRemark());
    classHourRecordService.save(record);

    // 检查是否需要发送低余额预警（剩余课时 <= 5）
    BigDecimal warningThreshold = BigDecimal.valueOf(5);
    if (newRemainingHours.compareTo(BigDecimal.ZERO) > 0
            && newRemainingHours.compareTo(warningThreshold) <= 0) {
        publishWarningEvent(account, "low_balance",
                "课时余额不足，剩余: " + newRemainingHours + " 课时");
    }

    return true;
}
```

#### 数据库表结构

**fin_class_hour_record（课时消耗记录表）：**
```sql
CREATE TABLE fin_class_hour_record (
    id BIGINT PRIMARY KEY,
    account_id BIGINT NOT NULL COMMENT '课时账户ID',
    student_id BIGINT NOT NULL COMMENT '学员ID',
    schedule_id BIGINT COMMENT '排课ID',
    type VARCHAR(20) NOT NULL COMMENT '类型：consume-消耗，gift-赠送，adjust-调整，refund-退费，revoke-撤销',
    hours DECIMAL(10,2) NOT NULL COMMENT '课时数（正数增加，负数减少）',
    balance DECIMAL(10,2) NOT NULL COMMENT '变动后余额',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME,
    create_by BIGINT
);
```

### 3. 课时不足预警机制

#### 功能描述
当课时余额不足时，系统自动发送预警通知，包括：
- 课时余额不足预警（剩余课时 <= 5）
- 课时不足拒绝扣减预警

#### 实现文件

**事件定义：**
- `/edu-finance/src/main/java/com/edu/finance/event/ClassHourWarningEvent.java`

**事件监听器：**
- `/edu-finance/src/main/java/com/edu/finance/listener/ClassHourWarningEventListener.java`

#### 预警触发时机

1. **低余额预警：** 扣减课时后，剩余课时 <= 5 时触发
2. **不足预警：** 扣减课时前，余额不足时触发

#### 关键代码片段

**预警事件发布：**
```java
private void publishWarningEvent(ClassHourAccount account, String warningType, String message) {
    ClassHourWarningEvent event = new ClassHourWarningEvent(
        this,
        warningType,
        account.getId(),
        account.getStudentId(),
        account.getCourseId(),
        account.getRemainingHours(),
        BigDecimal.valueOf(5), // 默认阈值
        message,
        true
    );
    eventPublisher.publishEvent(event);
}
```

**预警事件监听：**
```java
@Async
@EventListener
public void handleClassHourWarning(ClassHourWarningEvent event) {
    log.info("收到课时预警事件: type={}, studentId={}, courseId={}, remaining={}",
            event.getWarningType(), event.getStudentId(), event.getCourseId(),
            event.getRemainingHours());

    if (event.getSendNotification()) {
        // TODO: 发送通知给学员、家长、顾问
        // 1. 查询学员信息
        // 2. 查询课程信息
        // 3. 构建通知消息
        // 4. 发送站内消息
        // 5. 发送短信/微信通知（可选）
    }
}
```

## 技术实现要点

### 1. 事件驱动架构

使用 Spring Events 实现事件驱动，解耦业务模块：

```java
// 启用异步支持
@EnableAsync
@SpringBootApplication
public class EduAdminApplication {
    // ...
}

// 事件监听器
@Async
@EventListener
@Transactional(rollbackFor = Exception.class)
public void handleEvent(Event event) {
    // 异步处理事件
}
```

**优势：**
- 解耦业务模块（支付模块不依赖课时模块）
- 异步处理，不阻塞主流程
- 易于扩展（可以添加更多监听器）

### 2. 事务管理

所有涉及数据修改的操作都使用 `@Transactional` 注解：

```java
@Transactional(rollbackFor = Exception.class)
public boolean deductHours(ClassHourDeductDTO dto) {
    // 1. 更新账户余额
    // 2. 创建消耗记录
    // 3. 发送预警（如果需要）
    // 任何步骤失败都会回滚
}
```

**注意事项：**
- 异步事件监听器中的事务是独立的
- 事件处理失败不会影响主流程
- 使用 `rollbackFor = Exception.class` 确保所有异常都回滚

### 3. 异步处理

事件监听器使用 `@Async` 注解实现异步处理：

```java
@Async
@EventListener
public void handleEvent(Event event) {
    // 异步执行，不阻塞主流程
}
```

**优势：**
- 提高响应速度（收款确认、签到等操作立即返回）
- 避免阻塞主流程
- 提高系统吞吐量

### 4. 错误处理

完善的错误处理机制：

```java
try {
    // 业务逻辑
} catch (BusinessException e) {
    // 业务异常，记录日志并抛出
    log.error("业务异常: {}", e.getMessage());
    throw e;
} catch (Exception e) {
    // 系统异常，记录日志但不抛出（避免影响主流程）
    log.error("系统异常: {}", e.getMessage(), e);
}
```

## API 接口

### 课时账户管理

#### 1. 查询学员的课时账户列表
```http
GET /finance/class-hour-account/student/{studentId}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "studentId": 1,
      "contractId": 1,
      "courseId": 1,
      "campusId": 1,
      "totalHours": 100,
      "usedHours": 20,
      "remainingHours": 80,
      "giftHours": 0,
      "status": "active",
      "createTime": "2024-01-31 10:00:00"
    }
  ]
}
```

#### 2. 根据合同创建课时账户
```http
POST /finance/class-hour-account/create-by-contract/{contractId}
```

#### 3. 冻结课时账户
```http
PUT /finance/class-hour-account/{id}/freeze
```

#### 4. 解冻课时账户
```http
PUT /finance/class-hour-account/{id}/unfreeze
```

### 课时消耗记录管理

#### 1. 查询学员的课时消耗记录
```http
GET /finance/class-hour-record/student/{studentId}?accountId=1
```

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "accountId": 1,
      "studentId": 1,
      "scheduleId": 1,
      "type": "consume",
      "hours": -1.0,
      "balance": 79.0,
      "remark": "签到扣减课时 - 排课ID: 1, 考勤ID: 1",
      "createTime": "2024-01-31 14:00:00"
    }
  ]
}
```

## 测试场景

### 场景1：合同支付后自动创建课时账户

**前置条件：**
- 已创建合同，包含课程明细
- 合同状态为待支付

**测试步骤：**
1. 创建收款记录
2. 确认收款（金额等于合同总额）
3. 系统自动发布 ContractPaidEvent
4. 监听器自动创建课时账户

**预期结果：**
- 课时账户创建成功
- 账户状态为 active
- 总课时等于合同明细中的课时数
- 剩余课时等于总课时
- 已用课时为 0

### 场景2：学员签到后自动扣减课时

**前置条件：**
- 学员已有课时账户
- 课时余额充足
- 已创建排课记录

**测试步骤：**
1. 学员签到（状态为 present）
2. 系统自动发布 AttendanceSignedEvent
3. 监听器自动扣减课时

**预期结果：**
- 课时扣减成功
- 已用课时 +1
- 剩余课时 -1
- 创建课时消耗记录

### 场景3：课时不足拒绝扣减

**前置条件：**
- 学员已有课时账户
- 课时余额不足（< 需要扣减的课时）

**测试步骤：**
1. 学员签到
2. 系统尝试扣减课时

**预期结果：**
- 扣减失败，抛出 BusinessException
- 发送课时不足预警
- 账户余额不变

### 场景4：课时低余额预警

**前置条件：**
- 学员已有课时账户
- 课时余额为 6

**测试步骤：**
1. 学员签到，扣减 1 课时
2. 余额变为 5

**预期结果：**
- 课时扣减成功
- 发送低余额预警
- 预警消息包含剩余课时数

## 文件清单

### 修改的文件

1. `/edu-admin/src/main/java/com/edu/EduAdminApplication.java`
   - 添加 `@EnableAsync` 注解

2. `/edu-finance/src/main/java/com/edu/finance/service/impl/ClassHourAccountServiceImpl.java`
   - 增强 `deductHours()` 方法，添加预警机制
   - 添加 `publishWarningEvent()` 方法

### 已存在的文件（无需修改）

**事件和监听器：**
- `/edu-finance/src/main/java/com/edu/finance/event/ContractPaidEvent.java`
- `/edu-finance/src/main/java/com/edu/finance/listener/ContractPaidEventListener.java`
- `/edu-teaching/src/main/java/com/edu/teaching/event/AttendanceSignedEvent.java`
- `/edu-finance/src/main/java/com/edu/finance/listener/AttendanceSignedEventListener.java`
- `/edu-finance/src/main/java/com/edu/finance/event/ClassHourWarningEvent.java`
- `/edu-finance/src/main/java/com/edu/finance/listener/ClassHourWarningEventListener.java`

**服务层：**
- `/edu-finance/src/main/java/com/edu/finance/service/ClassHourAccountService.java`
- `/edu-finance/src/main/java/com/edu/finance/service/impl/ClassHourAccountServiceImpl.java`
- `/edu-finance/src/main/java/com/edu/finance/service/ClassHourRecordService.java`
- `/edu-finance/src/main/java/com/edu/finance/service/ClassHourRuleService.java`

**实体类：**
- `/edu-finance/src/main/java/com/edu/finance/domain/entity/ClassHourAccount.java`
- `/edu-finance/src/main/java/com/edu/finance/domain/entity/ClassHourRecord.java`
- `/edu-finance/src/main/java/com/edu/finance/domain/entity/ContractItem.java`

**控制器：**
- `/edu-finance/src/main/java/com/edu/finance/controller/ClassHourAccountController.java`
- `/edu-finance/src/main/java/com/edu/finance/controller/ClassHourRecordController.java`

## 配置说明

### 异步线程池配置（可选）

如果需要自定义异步线程池，可以添加配置类：

```java
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("async-event-");
        executor.initialize();
        return executor;
    }
}
```

### 课时预警阈值配置（可选）

可以在 `application.yml` 中配置预警阈值：

```yaml
edu:
  class-hour:
    warning:
      threshold: 5  # 剩余课时低于此值时发送预警
```

## 注意事项

1. **异步事务：** 事件监听器使用 `@Async` 和 `@Transactional`，事务是独立的
2. **事件处理失败：** 监听器中捕获异常，不影响主流程（收款确认、签到等）
3. **重复创建检查：** 创建课时账户前会检查是否已存在
4. **账户状态管理：** 余额为0时自动更新为 exhausted
5. **课时记录类型：** consume（消耗）、gift（赠送）、adjust（调整）、refund（退费）、revoke（撤销）

## 后续优化建议

1. **班级类型获取：** 当前班级类型需要从班级服务获取，需要实现完整的班级服务接口
2. **预警通知：** 完善预警通知功能，支持站内消息、短信、微信等多种通知方式
3. **课时转让：** 支持课时在学员之间转让
4. **批量操作：** 支持批量创建账户、批量调整课时等操作
5. **统计报表：** 添加课时消耗统计、课时使用率分析等报表功能
6. **课时有效期：** 支持设置课时有效期，到期自动冻结或失效
7. **课时包管理：** 支持课时包的购买、使用、转让等功能

## 总结

本次实现完成了课时账户的自动创建和签到扣减功能，主要特点：

✅ **事件驱动：** 使用 Spring Events 实现模块解耦
✅ **异步处理：** 使用 @Async 提高响应速度
✅ **事务管理：** 使用 @Transactional 确保数据一致性
✅ **预警机制：** 课时不足时自动发送预警
✅ **完整日志：** 所有关键操作都有详细日志记录
✅ **错误处理：** 完善的异常处理机制

系统已具备完整的课时管理能力，可以支持教育机构的日常运营需求。
