# 退费申请接口测试指南

## 快速开始

### 1. 启动应用
```bash
cd /Users/liujiandong/Documents/work/package/edu/edu-server
mvn spring-boot:run -pl edu-admin
```

### 2. 访问API文档
打开浏览器访问：`http://localhost:8080/doc.html`

找到"退费管理"模块，可以看到所有接口。

---

## 测试场景

### 场景1：完整的退费流程

#### 步骤1：计算退费金额
```bash
curl -X GET "http://localhost:8080/finance/refund/calculate/1001" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**预期响应**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "contractAmount": 10000.00,
    "paidAmount": 10000.00,
    "totalHours": 100.00,
    "usedHours": 30.00,
    "remainingHours": 70.00,
    "pricePerHour": 100.00,
    "usedAmount": 3000.00,
    "penaltyAmount": 350.00,
    "penaltyRate": 5.00,
    "refundableAmount": 6650.00,
    "calculationNote": "合同金额：10000.00元，已支付：10000.00元，总课时：100.00，已消耗：30.00，剩余：70.00，单价：100.00元/课时，已消耗金额：3000.00元，违约金（5%）：350.00元，可退金额：6650.00元"
  }
}
```

#### 步骤2：提交退费申请
```bash
curl -X POST "http://localhost:8080/finance/refund/apply" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "contractId": 1001,
    "reason": "学员搬家，无法继续上课",
    "description": "学员因工作调动需要搬迁至外地，无法继续在本校区上课"
  }'
```

**预期响应**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": 2001
}
```

#### 步骤3：查询退费申请详情
```bash
curl -X GET "http://localhost:8080/finance/refund/2001" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**预期响应**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "id": 2001,
    "refundNo": "TF202601310001",
    "contractId": 1001,
    "contractNo": "HT202601010001",
    "studentId": 1,
    "studentName": "张三",
    "campusId": 1,
    "campusName": "总部校区",
    "applyAmount": 6650.00,
    "actualAmount": 0.00,
    "penaltyAmount": 350.00,
    "reason": "学员搬家，无法继续上课",
    "description": "学员因工作调动需要搬迁至外地，无法继续在本校区上课",
    "status": "pending",
    "applyTime": "2026-01-31T10:00:00",
    "approverId": null,
    "approverName": null,
    "approveTime": null,
    "approveRemark": null,
    "refundTime": null,
    "refundMethod": null,
    "createTime": "2026-01-31T10:00:00",
    "updateTime": "2026-01-31T10:00:00"
  }
}
```

#### 步骤4：审批退费申请（通过）
```bash
curl -X POST "http://localhost:8080/finance/refund/approve" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "refundId": 2001,
    "approveResult": "approved",
    "actualAmount": 6650.00,
    "approveRemark": "同意退费申请"
  }'
```

**预期响应**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": true
}
```

#### 步骤5：执行退款
```bash
curl -X POST "http://localhost:8080/finance/refund/2001/execute" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**预期响应**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": true
}
```

#### 步骤6：查询退费申请列表
```bash
curl -X GET "http://localhost:8080/finance/refund/page?pageNum=1&pageSize=10&status=refunded" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**预期响应**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "records": [
      {
        "id": 2001,
        "refundNo": "TF202601310001",
        "contractId": 1001,
        "contractNo": "HT202601010001",
        "studentId": 1,
        "studentName": "张三",
        "campusId": 1,
        "campusName": "总部校区",
        "applyAmount": 6650.00,
        "actualAmount": 6650.00,
        "penaltyAmount": 350.00,
        "reason": "学员搬家，无法继续上课",
        "status": "refunded",
        "applyTime": "2026-01-31T10:00:00",
        "approverId": 1,
        "approverName": "管理员",
        "approveTime": "2026-01-31T10:30:00",
        "approveRemark": "同意退费申请",
        "refundTime": "2026-01-31T11:00:00",
        "refundMethod": "bank"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

---

### 场景2：退费申请被拒绝

#### 步骤1：提交退费申请
```bash
curl -X POST "http://localhost:8080/finance/refund/apply" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "contractId": 1002,
    "reason": "课程不满意",
    "description": "觉得课程内容不符合预期"
  }'
```

#### 步骤2：审批退费申请（拒绝）
```bash
curl -X POST "http://localhost:8080/finance/refund/approve" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "refundId": 2002,
    "approveResult": "rejected",
    "approveRemark": "不符合退费条件，课程已消耗超过50%"
  }'
```

**预期响应**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": true
}
```

---

### 场景3：异常情况测试

#### 测试1：重复提交退费申请
```bash
# 第一次提交
curl -X POST "http://localhost:8080/finance/refund/apply" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "contractId": 1001,
    "reason": "测试重复提交",
    "description": "测试"
  }'

# 第二次提交（应该失败）
curl -X POST "http://localhost:8080/finance/refund/apply" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "contractId": 1001,
    "reason": "测试重复提交",
    "description": "测试"
  }'
```

**预期响应**:
```json
{
  "code": 500,
  "msg": "该合同已有待审批的退费申请，请勿重复提交",
  "data": null
}
```

#### 测试2：审批不存在的退费申请
```bash
curl -X POST "http://localhost:8080/finance/refund/approve" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "refundId": 99999,
    "approveResult": "approved",
    "actualAmount": 1000.00,
    "approveRemark": "测试"
  }'
```

**预期响应**:
```json
{
  "code": 500,
  "msg": "退费申请不存在",
  "data": null
}
```

#### 测试3：实际退费金额大于申请金额
```bash
curl -X POST "http://localhost:8080/finance/refund/approve" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "refundId": 2001,
    "approveResult": "approved",
    "actualAmount": 10000.00,
    "approveRemark": "测试"
  }'
```

**预期响应**:
```json
{
  "code": 500,
  "msg": "实际退费金额不能大于申请金额",
  "data": null
}
```

#### 测试4：对非待审批状态的申请进行审批
```bash
# 先审批一次
curl -X POST "http://localhost:8080/finance/refund/approve" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "refundId": 2001,
    "approveResult": "approved",
    "actualAmount": 6650.00,
    "approveRemark": "同意"
  }'

# 再次审批（应该失败）
curl -X POST "http://localhost:8080/finance/refund/approve" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "refundId": 2001,
    "approveResult": "approved",
    "actualAmount": 6650.00,
    "approveRemark": "再次审批"
  }'
```

**预期响应**:
```json
{
  "code": 500,
  "msg": "只有待审批状态的申请才能审批",
  "data": null
}
```

---

## 使用Knife4j测试

### 1. 访问文档页面
打开浏览器访问：`http://localhost:8080/doc.html`

### 2. 找到退费管理模块
在左侧菜单中找到"退费管理"，展开可以看到所有接口。

### 3. 测试接口
1. 点击要测试的接口
2. 点击"调试"按钮
3. 填写参数
4. 点击"发送"按钮
5. 查看响应结果

### 4. 接口列表
- GET /finance/refund/page - 分页查询退费申请列表
- GET /finance/refund/{id} - 获取退费申请详情
- GET /finance/refund/calculate/{contractId} - 计算退费金额
- POST /finance/refund/apply - 提交退费申请
- POST /finance/refund/approve - 审批退费申请
- POST /finance/refund/{id}/execute - 执行退款
- DELETE /finance/refund/{id} - 删除退费申请

---

## 测试数据准备

### 1. 创建测试合同
```sql
-- 插入测试学员
INSERT INTO stu_student (id, name, phone, campus_id, status, create_time, update_time, deleted)
VALUES (1, '张三', '13800138000', 1, 'active', NOW(), NOW(), 0);

-- 插入测试合同
INSERT INTO fin_contract (id, contract_no, student_id, campus_id, type, amount, discount_amount, paid_amount, received_amount, total_hours, sign_date, effective_date, expire_date, status, sales_id, create_time, update_time, deleted)
VALUES (1001, 'HT202601010001', 1, 1, 'new', 10000.00, 0.00, 10000.00, 10000.00, 100, '2026-01-01', '2026-01-01', '2026-12-31', 'signed', 1, NOW(), NOW(), 0);

-- 插入课时账户
INSERT INTO fin_class_hour_account (id, student_id, contract_id, course_id, campus_id, total_hours, used_hours, remaining_hours, gift_hours, status, create_time, update_time, deleted)
VALUES (1, 1, 1001, 1, 1, 100.00, 30.00, 70.00, 0.00, 'active', NOW(), NOW(), 0);
```

### 2. 清理测试数据
```sql
-- 删除测试退费申请
DELETE FROM fin_refund WHERE contract_id = 1001;

-- 删除测试课时账户
DELETE FROM fin_class_hour_account WHERE contract_id = 1001;

-- 删除测试合同
DELETE FROM fin_contract WHERE id = 1001;

-- 删除测试学员
DELETE FROM stu_student WHERE id = 1;
```

---

## 验证点

### 1. 退费申请验证
- ✅ 退费单号格式正确（TF + 日期 + 序号）
- ✅ 申请金额计算正确
- ✅ 违约金计算正确
- ✅ 状态为"pending"
- ✅ 申请时间已记录

### 2. 退费审批验证
- ✅ 审批通过后状态变为"approved"
- ✅ 审批拒绝后状态变为"rejected"
- ✅ 审批人、审批时间、审批备注已记录
- ✅ 合同状态变为"refunded"（审批通过时）
- ✅ 课时账户状态变为"frozen"（审批通过时）

### 3. 退款执行验证
- ✅ 退款后状态变为"refunded"
- ✅ 退款时间已记录
- ✅ 退费完成事件已发布

### 4. 异常情况验证
- ✅ 重复提交退费申请被拒绝
- ✅ 非待审批状态不能审批
- ✅ 实际退费金额不能大于申请金额
- ✅ 非已通过状态不能执行退款

---

## 常见问题

### Q1: 如何获取认证Token?
A: 先调用登录接口获取Token，然后在请求头中添加 `Authorization: Bearer YOUR_TOKEN`

### Q2: 退费金额计算不正确怎么办?
A: 检查以下几点：
1. 合同的已支付金额是否正确
2. 课时账户的总课时和已消耗课时是否正确
3. 违约金比例是否为5%（默认值）

### Q3: 审批后合同状态没有变化?
A: 检查以下几点：
1. 审批结果是否为"approved"
2. 是否有事务回滚
3. 查看日志是否有异常

### Q4: 课时账户没有被冻结?
A: 检查以下几点：
1. 审批是否通过
2. 课时账户是否存在
3. 查看日志是否有异常

---

## 性能测试

### 1. 并发测试
使用JMeter或Apache Bench进行并发测试：

```bash
# 使用Apache Bench测试
ab -n 1000 -c 10 -H "Authorization: Bearer YOUR_TOKEN" \
  http://localhost:8080/finance/refund/page?pageNum=1&pageSize=10
```

### 2. 压力测试
测试系统在高负载下的表现：

```bash
# 使用JMeter进行压力测试
# 配置线程组：1000个线程，持续10分钟
# 测试所有退费相关接口
```

---

## 监控和日志

### 1. 查看应用日志
```bash
tail -f logs/edu-admin.log | grep -i refund
```

### 2. 查看数据库日志
```sql
-- 查看退费申请记录
SELECT * FROM fin_refund ORDER BY create_time DESC LIMIT 10;

-- 查看合同状态变化
SELECT id, contract_no, status, update_time
FROM fin_contract
WHERE status = 'refunded'
ORDER BY update_time DESC
LIMIT 10;

-- 查看课时账户状态变化
SELECT id, student_id, contract_id, status, update_time
FROM fin_class_hour_account
WHERE status = 'frozen'
ORDER BY update_time DESC
LIMIT 10;
```

---

## 总结

本测试指南涵盖了退费申请接口的所有测试场景，包括：
1. 正常流程测试
2. 异常情况测试
3. 边界条件测试
4. 性能测试

建议按照以下顺序进行测试：
1. 准备测试数据
2. 测试正常流程
3. 测试异常情况
4. 验证数据一致性
5. 清理测试数据

---

**文档版本**: 1.0
**创建日期**: 2026-01-31
**最后更新**: 2026-01-31
