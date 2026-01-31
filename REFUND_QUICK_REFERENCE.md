# 退费申请接口快速参考

## API接口速查

### 1. 计算退费金额
```http
GET /finance/refund/calculate/{contractId}
```
**用途**: 在提交退费申请前，先计算可退金额

**示例**:
```bash
curl -X GET "http://localhost:8080/finance/refund/calculate/1001"
```

---

### 2. 提交退费申请
```http
POST /finance/refund/apply
Content-Type: application/json

{
  "contractId": 1001,
  "reason": "退费原因",
  "description": "详细说明"
}
```

**示例**:
```bash
curl -X POST "http://localhost:8080/finance/refund/apply" \
  -H "Content-Type: application/json" \
  -d '{"contractId":1001,"reason":"学员搬家","description":"无法继续上课"}'
```

---

### 3. 审批退费申请
```http
POST /finance/refund/approve
Content-Type: application/json

{
  "refundId": 2001,
  "approveResult": "approved",
  "actualAmount": 6650.00,
  "approveRemark": "同意退费"
}
```

**示例（通过）**:
```bash
curl -X POST "http://localhost:8080/finance/refund/approve" \
  -H "Content-Type: application/json" \
  -d '{"refundId":2001,"approveResult":"approved","actualAmount":6650.00,"approveRemark":"同意"}'
```

**示例（拒绝）**:
```bash
curl -X POST "http://localhost:8080/finance/refund/approve" \
  -H "Content-Type: application/json" \
  -d '{"refundId":2001,"approveResult":"rejected","approveRemark":"不符合退费条件"}'
```

---

## 计算公式速查

```
可退金额 = 已支付金额 - 已消耗金额 - 违约金

单课时价格 = 已支付金额 / 总课时数
已消耗金额 = 单课时价格 × 已消耗课时数
违约金 = (已支付金额 - 已消耗金额) × 5%
```

**示例**:
```
合同金额: 10000元
总课时: 100课时
已消耗: 30课时

单价 = 10000 / 100 = 100元/课时
已消耗金额 = 100 × 30 = 3000元
违约金 = (10000 - 3000) × 5% = 350元
可退金额 = 10000 - 3000 - 350 = 6650元
```

---

## 状态流转速查

```
pending (待审批)
    ↓
approved (已通过) → refunded (已退款)
    ↓
rejected (已拒绝)
```

---

## 核心文件速查

| 类型 | 文件路径 |
|------|---------|
| Controller | `/edu-finance/src/main/java/com/edu/finance/controller/RefundController.java` |
| Service | `/edu-finance/src/main/java/com/edu/finance/service/impl/RefundServiceImpl.java` |
| Entity | `/edu-finance/src/main/java/com/edu/finance/domain/entity/Refund.java` |
| DTO | `/edu-finance/src/main/java/com/edu/finance/domain/dto/RefundApplyDTO.java` |
| Mapper | `/edu-finance/src/main/java/com/edu/finance/mapper/RefundMapper.java` |

---

## 常用SQL速查

### 查询待审批的退费申请
```sql
SELECT * FROM fin_refund WHERE status = 'pending' AND deleted = 0;
```

### 查询某个合同的退费记录
```sql
SELECT * FROM fin_refund WHERE contract_id = 1001 AND deleted = 0;
```

### 查询某个学员的退费记录
```sql
SELECT * FROM fin_refund WHERE student_id = 1 AND deleted = 0;
```

### 统计退费金额
```sql
SELECT
    status,
    COUNT(*) as count,
    SUM(apply_amount) as total_apply,
    SUM(actual_amount) as total_actual
FROM fin_refund
WHERE deleted = 0
GROUP BY status;
```

---

## 错误码速查

| 错误信息 | 原因 | 解决方法 |
|---------|------|---------|
| 合同不存在 | contractId无效 | 检查合同ID是否正确 |
| 只有已签署或已完成的合同才能申请退费 | 合同状态不对 | 确认合同状态为signed或completed |
| 该合同已有待审批的退费申请 | 重复提交 | 等待当前申请审批完成 |
| 退费申请不存在 | refundId无效 | 检查退费申请ID是否正确 |
| 只有待审批状态的申请才能审批 | 状态不对 | 确认申请状态为pending |
| 实际退费金额必须大于0 | 金额无效 | 填写正确的退费金额 |
| 实际退费金额不能大于申请金额 | 金额超限 | 调整退费金额 |

---

## 测试数据速查

### 创建测试合同
```sql
INSERT INTO fin_contract (id, contract_no, student_id, campus_id, type, amount, paid_amount, total_hours, status, create_time, update_time, deleted)
VALUES (1001, 'HT202601010001', 1, 1, 'new', 10000.00, 10000.00, 100, 'signed', NOW(), NOW(), 0);
```

### 创建课时账户
```sql
INSERT INTO fin_class_hour_account (id, student_id, contract_id, course_id, campus_id, total_hours, used_hours, remaining_hours, status, create_time, update_time, deleted)
VALUES (1, 1, 1001, 1, 1, 100.00, 30.00, 70.00, 'active', NOW(), NOW(), 0);
```

---

## 文档链接速查

| 文档 | 路径 |
|------|------|
| API文档 | http://localhost:8080/doc.html |
| 使用文档 | `/edu-finance/REFUND_README.md` |
| 实现总结 | `/edu-finance/REFUND_IMPLEMENTATION_SUMMARY.md` |
| 测试指南 | `/edu-finance/REFUND_API_TEST_GUIDE.md` |
| 实现报告 | `/edu-server/REFUND_IMPLEMENTATION_REPORT.md` |

---

## 开发提示

### 1. 修改违约金比例
在 `RefundServiceImpl.java` 中修改：
```java
private static final BigDecimal DEFAULT_PENALTY_RATE = new BigDecimal("0.05"); // 5%
```

### 2. 添加退费通知
在 `RefundCompletedListener.java` 中添加：
```java
@EventListener
public void handleRefundCompleted(RefundCompletedEvent event) {
    // 发送通知给学员
    // 发送通知给管理员
}
```

### 3. 自定义计算规则
在 `RefundServiceImpl.calculateRefundAmount()` 方法中修改计算逻辑。

---

**版本**: 1.0
**日期**: 2026-01-31
