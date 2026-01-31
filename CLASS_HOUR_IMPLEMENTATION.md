# 课时管理核心接口实现文档

## 概述

本文档描述了教育机构学生管理系统中课时管理核心接口的实现，包括任务 19.2（课时账户创建）、19.3（课时扣减逻辑）和 19.4（消课规则配置）。

## 实现的功能

### 1. 课时账户创建（任务 19.2）

**功能描述：**
- 合同支付后自动创建课时账户
- 根据合同明细初始化课时余额
- 支持多个课程的课时账户

**实现文件：**
- Entity: `/edu-finance/src/main/java/com/edu/finance/domain/entity/ClassHourAccount.java`
- Service: `/edu-finance/src/main/java/com/edu/finance/service/ClassHourAccountService.java`
- ServiceImpl: `/edu-finance/src/main/java/com/edu/finance/service/impl/ClassHourAccountServiceImpl.java`
- Controller: `/edu-finance/src/main/java/com/edu/finance/controller/ClassHourAccountController.java`
- Event: `/edu-finance/src/main/java/com/edu/finance/event/ContractPaidEvent.java`
- Listener: `/edu-finance/src/main/java/com/edu/finance/listener/ContractPaidEventListener.java`

**核心流程：**
1. 收款确认时，PaymentServiceImpl 检查合同是否已全额支付
2. 如果已全额支付，发布 `ContractPaidEvent` 事件
3. `ContractPaidEventListener` 监听事件，调用 `createAccountByContract()` 方法
4. 根据合同明细（fin_contract_item）为每个课程创建课时账户
5. 初始化账户状态为 `active`，设置总课时、已用课时（0）、剩余课时

### 2. 课时扣减逻辑（任务 19.3）

**功能描述：**
- 监听签到事件自动扣减课时
- 根据消课规则计算扣减数量
- 记录课时扣减明细

**实现文件：**
- Entity: `/edu-finance/src/main/java/com/edu/finance/domain/entity/ClassHourRecord.java`
- Service: `/edu-finance/src/main/java/com/edu/finance/service/ClassHourRecordService.java`
- ServiceImpl: `/edu-finance/src/main/java/com/edu/finance/service/impl/ClassHourRecordServiceImpl.java`
- Controller: `/edu-finance/src/main/java/com/edu/finance/controller/ClassHourRecordController.java`
- Listener: `/edu-finance/src/main/java/com/edu/finance/listener/AttendanceSignedEventListener.java`

**核心流程：**
1. 学员签到时，AttendanceServiceImpl 发布 `AttendanceSignedEvent` 事件
2. `AttendanceSignedEventListener` 监听事件
3. 只处理出勤（present）和迟到（late）状态的签到
4. 调用 `ClassHourRuleService.calculateDeductHours()` 计算应扣减课时
5. 调用 `ClassHourAccountService.deductHours()` 扣减课时
6. 创建 `ClassHourRecord` 记录扣减明细
7. 更新账户余额，如果余额为0则更新状态为 `exhausted`

### 3. 消课规则配置（任务 19.4）

**功能描述：**
- 配置不同课程的消课规则
- 支持按课时、按课次扣减
- 支持特殊规则（如试听课不扣课时）

**实现文件：**
- Entity: `/edu-finance/src/main/java/com/edu/finance/domain/entity/ClassHourRule.java`
- Service: `/edu-finance/src/main/java/com/edu/finance/service/ClassHourRuleService.java`
- ServiceImpl: `/edu-finance/src/main/java/com/edu/finance/service/impl/ClassHourRuleServiceImpl.java`
- Controller: `/edu-finance/src/main/java/com/edu/finance/controller/ClassHourRuleController.java`
- Migration: `/edu-admin/src/main/resources/db/migration/V1.0.15__add_class_hour_rule_table.sql`

**规则匹配优先级：**
1. 课程ID + 班级类型 + 校区ID
2. 课程ID + 班级类型
3. 班级类型 + 校区ID
4. 班级类型
5. 默认规则（course_id、class_type、campus_id 均为 NULL）

**扣减类型：**
- `per_hour`: 按课时扣减（实际课时 × 扣减系数）
- `per_class`: 按课次扣减（固定扣减数量）
- `custom`: 自定义扣减（使用配置的扣减数量）

## 数据库表结构

### 1. fin_class_hour_rule（课时消课规则表）

```sql
CREATE TABLE fin_class_hour_rule (
    id BIGINT NOT NULL COMMENT 'ID',
    name VARCHAR(100) NOT NULL COMMENT '规则名称',
    course_id BIGINT COMMENT '课程ID（为空表示通用规则）',
    class_type VARCHAR(50) COMMENT '班级类型',
    deduct_type VARCHAR(20) NOT NULL DEFAULT 'per_hour' COMMENT '扣减类型',
    deduct_amount DECIMAL(10,2) NOT NULL DEFAULT 1.00 COMMENT '扣减数量',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态',
    campus_id BIGINT COMMENT '校区ID',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    deleted TINYINT DEFAULT 0,
    PRIMARY KEY (id)
);
```

### 2. fin_class_hour_account（课时账户表）

已存在，字段包括：
- student_id: 学员ID
- contract_id: 合同ID
- course_id: 课程ID
- campus_id: 校区ID
- total_hours: 总课时
- used_hours: 已消耗课时
- remaining_hours: 剩余课时
- gift_hours: 赠送课时
- status: 状态（active/frozen/exhausted）

### 3. fin_class_hour_record（课时消耗记录表）

已存在，字段包括：
- account_id: 课时账户ID
- student_id: 学员ID
- schedule_id: 排课ID
- type: 类型（consume/gift/adjust/refund）
- hours: 课时数（正数增加，负数减少）
- balance: 变动后余额
- remark: 备注

### 4. fin_contract_item（合同明细表）

已存在，字段包括：
- contract_id: 合同ID
- course_id: 课程ID
- course_name: 课程名称
- unit_price: 单价
- quantity: 数量
- hours: 课时数
- amount: 金额

## API 接口

### 课时账户管理

#### 1. 分页查询课时账户
```
GET /finance/class-hour-account/page?current=1&size=10
```

#### 2. 查询学员的课时账户列表
```
GET /finance/class-hour-account/student/{studentId}
```

#### 3. 创建课时账户
```
POST /finance/class-hour-account
Content-Type: application/json

{
  "studentId": 1,
  "contractId": 1,
  "courseId": 1,
  "campusId": 1,
  "totalHours": 100,
  "giftHours": 10
}
```

#### 4. 根据合同创建课时账户
```
POST /finance/class-hour-account/create-by-contract/{contractId}
```

#### 5. 冻结课时账户
```
PUT /finance/class-hour-account/{id}/freeze
```

#### 6. 解冻课时账户
```
PUT /finance/class-hour-account/{id}/unfreeze
```

#### 7. 调整课时余额
```
PUT /finance/class-hour-account/{id}/adjust?hours=10&remark=补偿课时
```

### 消课规则管理

#### 1. 分页查询消课规则
```
GET /finance/class-hour-rule/page?current=1&size=10
```

#### 2. 获取适用的消课规则
```
GET /finance/class-hour-rule/get-rule?courseId=1&classType=one_on_one&campusId=1
```

#### 3. 计算应扣减的课时数
```
GET /finance/class-hour-rule/calculate-deduct?courseId=1&classType=one_on_one&campusId=1&classHours=2
```

#### 4. 创建消课规则
```
POST /finance/class-hour-rule
Content-Type: application/json

{
  "name": "VIP一对一规则",
  "courseId": 1,
  "classType": "one_on_one",
  "deductType": "per_class",
  "deductAmount": 1.5,
  "status": "active",
  "campusId": 1,
  "remark": "VIP课程按1.5倍扣减"
}
```

#### 5. 更新消课规则
```
PUT /finance/class-hour-rule/{id}
Content-Type: application/json

{
  "name": "更新后的规则名称",
  "deductAmount": 2.0
}
```

#### 6. 启用/停用消课规则
```
PUT /finance/class-hour-rule/{id}/enable
PUT /finance/class-hour-rule/{id}/disable
```

### 课时消耗记录管理

#### 1. 分页查询课时消耗记录
```
GET /finance/class-hour-record/page?current=1&size=10
```

#### 2. 查询学员的课时消耗记录
```
GET /finance/class-hour-record/student/{studentId}?accountId=1
```

## 事件驱动架构

### 1. ContractPaidEvent（合同支付完成事件）

**触发时机：** 收款确认且合同已全额支付时

**事件数据：**
- contractId: 合同ID
- studentId: 学员ID
- campusId: 校区ID
- paidAmount: 支付金额

**监听器：** ContractPaidEventListener

**处理逻辑：** 根据合同明细自动创建课时账户

### 2. AttendanceSignedEvent（考勤签到事件）

**触发时机：** 学员签到成功时（出勤或迟到）

**事件数据：**
- attendanceId: 考勤记录ID
- scheduleId: 排课ID
- studentId: 学员ID
- classId: 班级ID
- courseId: 课程ID
- status: 考勤状态
- signTime: 签到时间
- classHours: 消耗课时
- campusId: 校区ID

**监听器：** AttendanceSignedEventListener

**处理逻辑：** 根据消课规则自动扣减课时

## 事务管理

所有涉及数据修改的操作都使用了 `@Transactional` 注解确保数据一致性：

1. **课时账户创建：** 创建账户和初始化记录在同一事务中
2. **课时扣减：** 更新账户余额和创建扣减记录在同一事务中
3. **收款确认：** 更新收款状态、更新合同已收金额、发布事件在同一事务中

## 异步处理

事件监听器使用 `@Async` 注解实现异步处理，避免阻塞主业务流程：

- ContractPaidEventListener: 异步创建课时账户
- AttendanceSignedEventListener: 异步扣减课时

## 错误处理

1. **业务异常：** 使用 `BusinessException` 抛出业务错误
2. **日志记录：** 使用 Slf4j 记录关键操作和错误信息
3. **事件处理异常：** 捕获异常并记录日志，不影响主流程

## 默认规则

系统预置了4条默认消课规则：

1. **默认规则：** 按实际课时扣减（1:1）
2. **一对一课程：** 按课次扣减（每次1课时）
3. **小班课：** 按课时扣减（1:1）
4. **大班课：** 按0.5课时扣减

## 使用示例

### 场景1：合同支付后自动创建课时账户

```java
// 1. 确认收款
paymentService.confirmPayment(paymentId, transactionNo);

// 2. 系统自动检查合同是否全额支付
// 3. 如果全额支付，发布 ContractPaidEvent
// 4. ContractPaidEventListener 监听事件
// 5. 根据合同明细自动创建课时账户
```

### 场景2：学员签到后自动扣减课时

```java
// 1. 学员签到
attendanceService.signIn(scheduleId, studentId, "present", null);

// 2. 系统发布 AttendanceSignedEvent
// 3. AttendanceSignedEventListener 监听事件
// 4. 根据消课规则计算应扣减课时
// 5. 自动扣减课时并记录明细
```

### 场景3：配置特殊消课规则

```java
// 创建VIP课程的特殊规则
ClassHourRule rule = new ClassHourRule();
rule.setName("VIP一对一规则");
rule.setCourseId(1L);
rule.setClassType("one_on_one");
rule.setDeductType("per_class");
rule.setDeductAmount(new BigDecimal("1.5"));
rule.setStatus("active");
classHourRuleService.save(rule);
```

## 文件清单

### 实体类（Entity）
- `/edu-finance/src/main/java/com/edu/finance/domain/entity/ClassHourAccount.java` (已存在)
- `/edu-finance/src/main/java/com/edu/finance/domain/entity/ClassHourRecord.java` (新增)
- `/edu-finance/src/main/java/com/edu/finance/domain/entity/ClassHourRule.java` (新增)
- `/edu-finance/src/main/java/com/edu/finance/domain/entity/ContractItem.java` (新增)

### DTO/VO
- `/edu-finance/src/main/java/com/edu/finance/domain/dto/ClassHourAccountCreateDTO.java` (新增)
- `/edu-finance/src/main/java/com/edu/finance/domain/dto/ClassHourDeductDTO.java` (新增)
- `/edu-finance/src/main/java/com/edu/finance/domain/vo/ClassHourAccountVO.java` (新增)

### Mapper
- `/edu-finance/src/main/java/com/edu/finance/mapper/ClassHourAccountMapper.java` (已存在)
- `/edu-finance/src/main/java/com/edu/finance/mapper/ClassHourRecordMapper.java` (新增)
- `/edu-finance/src/main/java/com/edu/finance/mapper/ClassHourRuleMapper.java` (新增)
- `/edu-finance/src/main/java/com/edu/finance/mapper/ContractItemMapper.java` (新增)

### Mapper XML
- `/edu-finance/src/main/resources/mapper/finance/ClassHourRecordMapper.xml` (新增)
- `/edu-finance/src/main/resources/mapper/finance/ContractItemMapper.xml` (新增)

### Service
- `/edu-finance/src/main/java/com/edu/finance/service/ClassHourAccountService.java` (新增)
- `/edu-finance/src/main/java/com/edu/finance/service/ClassHourRecordService.java` (新增)
- `/edu-finance/src/main/java/com/edu/finance/service/ClassHourRuleService.java` (新增)

### Service Implementation
- `/edu-finance/src/main/java/com/edu/finance/service/impl/ClassHourAccountServiceImpl.java` (新增)
- `/edu-finance/src/main/java/com/edu/finance/service/impl/ClassHourRecordServiceImpl.java` (新增)
- `/edu-finance/src/main/java/com/edu/finance/service/impl/ClassHourRuleServiceImpl.java` (新增)
- `/edu-finance/src/main/java/com/edu/finance/service/impl/PaymentServiceImpl.java` (修改)

### Controller
- `/edu-finance/src/main/java/com/edu/finance/controller/ClassHourAccountController.java` (新增)
- `/edu-finance/src/main/java/com/edu/finance/controller/ClassHourRecordController.java` (新增)
- `/edu-finance/src/main/java/com/edu/finance/controller/ClassHourRuleController.java` (新增)

### Event & Listener
- `/edu-finance/src/main/java/com/edu/finance/event/ContractPaidEvent.java` (新增)
- `/edu-finance/src/main/java/com/edu/finance/listener/ContractPaidEventListener.java` (新增)
- `/edu-finance/src/main/java/com/edu/finance/listener/AttendanceSignedEventListener.java` (新增)

### Database Migration
- `/edu-admin/src/main/resources/db/migration/V1.0.15__add_class_hour_rule_table.sql` (新增)

## 技术栈

- **Spring Boot 3.2.x**: 应用框架
- **MyBatis-Plus 3.5.x**: ORM框架
- **Spring Events**: 事件驱动
- **Spring Transaction**: 事务管理
- **Spring Async**: 异步处理
- **Flyway**: 数据库迁移
- **Lombok**: 简化代码
- **Hutool**: 工具库
- **Swagger/Knife4j**: API文档

## 注意事项

1. **事件异步处理：** 事件监听器使用异步处理，需要确保应用配置了线程池
2. **事务传播：** 异步事件监听器中的事务是独立的，不会影响主流程
3. **规则优先级：** 消课规则按照优先级匹配，越具体的规则优先级越高
4. **余额检查：** 扣减课时前会检查余额是否足够
5. **状态管理：** 账户状态会根据余额自动更新（active/frozen/exhausted）
6. **日志记录：** 所有关键操作都有详细的日志记录，便于问题排查

## 后续优化建议

1. **班级类型获取：** 当前班级类型需要从班级服务获取，需要实现班级服务接口
2. **关联信息填充：** ClassHourAccountVO 中的关联信息（学员姓名、课程名称等）需要填充
3. **试听课处理：** 可以添加试听课不扣课时的特殊规则
4. **课时预警：** 当课时余额低于阈值时发送预警通知
5. **课时转让：** 支持课时在学员之间转让
6. **课时冻结期限：** 支持设置冻结期限，到期自动解冻
7. **批量操作：** 支持批量创建账户、批量调整课时等操作
8. **统计报表：** 添加课时消耗统计、课时使用率分析等报表功能
