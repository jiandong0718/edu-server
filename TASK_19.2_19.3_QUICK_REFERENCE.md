# 课时账户创建和扣减功能 - 快速参考

## 核心流程

### 1. 课时账户创建流程

```
收款确认 → 检查合同全额支付 → 发布ContractPaidEvent →
监听器处理 → 查询合同明细 → 为每个课程创建账户
```

**触发条件：** 合同已全额支付

**关键文件：**
- 事件：`ContractPaidEvent.java`
- 监听器：`ContractPaidEventListener.java`
- 服务：`ClassHourAccountService.java`

### 2. 课时扣减流程

```
学员签到 → 发布AttendanceSignedEvent → 监听器处理 →
计算扣减课时 → 检查余额 → 扣减课时 → 记录明细 → 检查预警
```

**触发条件：** 学员签到（present 或 late）

**关键文件：**
- 事件：`AttendanceSignedEvent.java`
- 监听器：`AttendanceSignedEventListener.java`
- 服务：`ClassHourAccountService.java`

## 关键API

### 课时账户

```bash
# 查询学员课时账户
GET /finance/class-hour-account/student/{studentId}

# 根据合同创建账户
POST /finance/class-hour-account/create-by-contract/{contractId}

# 冻结账户
PUT /finance/class-hour-account/{id}/freeze

# 解冻账户
PUT /finance/class-hour-account/{id}/unfreeze
```

### 课时记录

```bash
# 查询学员课时记录
GET /finance/class-hour-record/student/{studentId}?accountId=1
```

## 数据库表

### fin_class_hour_account（课时账户）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| student_id | BIGINT | 学员ID |
| contract_id | BIGINT | 合同ID |
| course_id | BIGINT | 课程ID |
| campus_id | BIGINT | 校区ID |
| total_hours | DECIMAL(10,2) | 总课时 |
| used_hours | DECIMAL(10,2) | 已用课时 |
| remaining_hours | DECIMAL(10,2) | 剩余课时 |
| gift_hours | DECIMAL(10,2) | 赠送课时 |
| status | VARCHAR(20) | 状态：active/frozen/exhausted |

### fin_class_hour_record（课时记录）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| account_id | BIGINT | 账户ID |
| student_id | BIGINT | 学员ID |
| schedule_id | BIGINT | 排课ID |
| type | VARCHAR(20) | 类型：consume/gift/adjust/refund/revoke |
| hours | DECIMAL(10,2) | 课时数（正数增加，负数减少） |
| balance | DECIMAL(10,2) | 变动后余额 |
| remark | VARCHAR(500) | 备注 |

## 状态说明

### 账户状态（status）

- **active**: 正常，可以扣减课时
- **frozen**: 冻结，不能扣减课时
- **exhausted**: 已用完，余额为0

### 记录类型（type）

- **consume**: 消耗（签到扣减）
- **gift**: 赠送
- **adjust**: 调整
- **refund**: 退费
- **revoke**: 撤销

## 预警机制

### 预警类型

1. **low_balance**: 低余额预警（剩余课时 <= 5）
2. **insufficient_balance**: 余额不足预警（扣减时余额不足）

### 预警触发时机

- 扣减课时后，剩余课时 <= 5
- 扣减课时前，余额不足

## 异常处理

### 业务异常

| 异常信息 | 原因 | 解决方法 |
|---------|------|---------|
| 学员没有该课程的课时账户 | 未创建账户 | 先创建账户或检查合同支付状态 |
| 课时账户已冻结，无法扣减 | 账户被冻结 | 解冻账户 |
| 课时账户已用完，无法扣减 | 余额为0 | 充值或调整课时 |
| 课时余额不足 | 余额 < 需要扣减的课时 | 充值或调整课时 |

## 配置说明

### 启用异步支持

在 `EduAdminApplication.java` 中添加：

```java
@EnableAsync
@SpringBootApplication
public class EduAdminApplication {
    // ...
}
```

### 预警阈值配置（可选）

在 `application.yml` 中配置：

```yaml
edu:
  class-hour:
    warning:
      threshold: 5  # 剩余课时低于此值时发送预警
```

## 测试命令

### 1. 测试账户创建

```bash
# 创建合同并支付
curl -X POST http://localhost:8080/finance/payment/confirm/{paymentId} \
  -H "Content-Type: application/json" \
  -d '{"transactionNo": "TXN123456"}'

# 查询课时账户
curl http://localhost:8080/finance/class-hour-account/student/{studentId}
```

### 2. 测试课时扣减

```bash
# 学员签到
curl -X POST http://localhost:8080/teaching/attendance/sign-in \
  -H "Content-Type: application/json" \
  -d '{
    "scheduleId": 1,
    "studentId": 1,
    "status": "present"
  }'

# 查询课时记录
curl http://localhost:8080/finance/class-hour-record/student/{studentId}
```

## 常见问题

### Q1: 合同支付后没有创建课时账户？

**检查项：**
1. 合同是否已全额支付
2. 合同明细是否包含课时数
3. 查看日志是否有异常
4. 检查 @EnableAsync 是否已配置

### Q2: 签到后没有扣减课时？

**检查项：**
1. 考勤状态是否为 present 或 late
2. 学员是否有对应课程的课时账户
3. 课时余额是否充足
4. 查看日志是否有异常

### Q3: 如何手动创建课时账户？

```bash
curl -X POST http://localhost:8080/finance/class-hour-account \
  -H "Content-Type: application/json" \
  -d '{
    "studentId": 1,
    "contractId": 1,
    "courseId": 1,
    "campusId": 1,
    "totalHours": 100,
    "giftHours": 0
  }'
```

### Q4: 如何调整课时余额？

```bash
# 增加10课时
curl -X PUT "http://localhost:8080/finance/class-hour-account/{id}/adjust?hours=10&remark=补偿课时"

# 减少5课时
curl -X PUT "http://localhost:8080/finance/class-hour-account/{id}/adjust?hours=-5&remark=扣减课时"
```

## 日志关键字

查看日志时可以搜索以下关键字：

- `收到合同支付完成事件`
- `合同支付后课时账户创建成功`
- `收到考勤签到事件`
- `签到后课时扣减成功`
- `课时扣减成功`
- `收到课时预警事件`

## 文件位置

### 核心文件

```
edu-finance/
├── event/
│   ├── ContractPaidEvent.java
│   └── ClassHourWarningEvent.java
├── listener/
│   ├── ContractPaidEventListener.java
│   ├── AttendanceSignedEventListener.java
│   └── ClassHourWarningEventListener.java
├── service/
│   ├── ClassHourAccountService.java
│   ├── ClassHourRecordService.java
│   └── ClassHourRuleService.java
└── domain/
    ├── entity/
    │   ├── ClassHourAccount.java
    │   └── ClassHourRecord.java
    └── dto/
        ├── ClassHourAccountCreateDTO.java
        └── ClassHourDeductDTO.java

edu-teaching/
└── event/
    └── AttendanceSignedEvent.java
```

## 相关文档

- 详细实现文档：`TASK_19.2_19.3_IMPLEMENTATION_SUMMARY.md`
- 课时管理核心接口：`CLASS_HOUR_IMPLEMENTATION.md`
- API 文档：http://localhost:8080/doc.html
