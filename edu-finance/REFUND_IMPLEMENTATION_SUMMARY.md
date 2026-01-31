# 退费申请后端接口实现总结

## 任务完成情况

本文档总结了退费申请后端接口（任务18.5-18.7）的完整实现情况。

### 任务18.5：实现退费申请接口 ✅

**接口地址**: `POST /finance/refund/apply`

**功能描述**: 创建退费申请，自动计算退费金额并生成退费单号

**实现文件**:
- Controller: `/edu-finance/src/main/java/com/edu/finance/controller/RefundController.java`
- Service: `/edu-finance/src/main/java/com/edu/finance/service/impl/RefundServiceImpl.java`
- DTO: `/edu-finance/src/main/java/com/edu/finance/domain/dto/RefundApplyDTO.java`

**核心功能**:
1. 验证合同状态（只有已签署或已完成的合同才能申请退费）
2. 检查是否已有待审批的退费申请（防止重复提交）
3. 自动计算退费金额（调用计算接口）
4. 生成退费单号（格式：TF + 日期 + 序号，如 TF202601310001）
5. 创建退费申请记录，状态为"pending"（待审批）
6. 记录申请时间、合同ID、学员ID、校区ID等信息

**请求示例**:
```json
{
  "contractId": 1001,
  "reason": "学员搬家，无法继续上课",
  "description": "学员因工作调动需要搬迁至外地，无法继续在本校区上课"
}
```

**响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": 2001
}
```

---

### 任务18.6：实现退费金额计算逻辑 ✅

**接口地址**: `GET /finance/refund/calculate/{contractId}`

**功能描述**: 根据合同金额、已用课时、剩余课时计算退费金额，扣除违约金

**实现文件**:
- Service: `/edu-finance/src/main/java/com/edu/finance/service/impl/RefundServiceImpl.java` (calculateRefundAmount方法)
- DTO: `/edu-finance/src/main/java/com/edu/finance/domain/dto/RefundCalculationDTO.java`

**计算公式**:
```
可退金额 = 已支付金额 - 已消耗金额 - 违约金

其中：
- 单课时价格 = 已支付金额 / 总课时数
- 已消耗金额 = 单课时价格 × 已消耗课时数
- 违约金 = (已支付金额 - 已消耗金额) × 违约金比例（默认5%）
```

**计算示例**:
```
合同金额：10000元
已支付：10000元
总课时：100课时
已消耗：30课时
剩余：70课时

计算过程：
1. 单课时价格 = 10000 / 100 = 100元/课时
2. 已消耗金额 = 100 × 30 = 3000元
3. 剩余金额 = 10000 - 3000 = 7000元
4. 违约金 = 7000 × 5% = 350元
5. 可退金额 = 7000 - 350 = 6650元
```

**核心功能**:
1. 查询合同信息和课时账户信息
2. 计算总课时、已消耗课时、剩余课时
3. 计算单课时价格
4. 计算已消耗金额
5. 计算违约金（默认5%，可配置）
6. 计算可退金额
7. 生成详细的计算说明

**响应示例**:
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

---

### 任务18.7：实现退费审批接口 ✅

**接口地址**: `POST /finance/refund/approve`

**功能描述**: 审批退费申请，支持通过/拒绝，记录审批信息，更新合同状态

**实现文件**:
- Controller: `/edu-finance/src/main/java/com/edu/finance/controller/RefundController.java`
- Service: `/edu-finance/src/main/java/com/edu/finance/service/impl/RefundServiceImpl.java`
- DTO: `/edu-finance/src/main/java/com/edu/finance/domain/dto/RefundApproveDTO.java`

**核心功能**:
1. 验证退费申请状态（只有待审批状态才能审批）
2. 验证审批结果（approved/rejected）
3. **审批通过时**:
   - 验证实际退费金额（必填，必须大于0，不能大于申请金额）
   - 更新退费申请状态为"approved"
   - 更新合同状态为"refunded"
   - 冻结相关课时账户（状态变为"frozen"）
   - 记录审批人、审批时间、审批备注
4. **审批拒绝时**:
   - 更新退费申请状态为"rejected"
   - 记录拒绝原因
5. 使用事务管理确保数据一致性

**请求示例（通过）**:
```json
{
  "refundId": 2001,
  "approveResult": "approved",
  "actualAmount": 6650.00,
  "approveRemark": "同意退费申请"
}
```

**请求示例（拒绝）**:
```json
{
  "refundId": 2001,
  "approveResult": "rejected",
  "approveRemark": "不符合退费条件"
}
```

**响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": true
}
```

---

## 完整的API接口列表

### 1. 分页查询退费申请列表
```
GET /finance/refund/page
参数：
  - pageNum: 页码（默认1）
  - pageSize: 每页数量（默认10）
  - refundNo: 退费单号（可选）
  - contractId: 合同ID（可选）
  - studentId: 学员ID（可选）
  - campusId: 校区ID（可选）
  - status: 状态（可选）
```

### 2. 获取退费申请详情
```
GET /finance/refund/{id}
参数：
  - id: 退费申请ID
```

### 3. 计算退费金额
```
GET /finance/refund/calculate/{contractId}
参数：
  - contractId: 合同ID
```

### 4. 提交退费申请
```
POST /finance/refund/apply
请求体：RefundApplyDTO
```

### 5. 审批退费申请
```
POST /finance/refund/approve
请求体：RefundApproveDTO
```

### 6. 执行退款
```
POST /finance/refund/{id}/execute
参数：
  - id: 退费申请ID
```

### 7. 删除退费申请
```
DELETE /finance/refund/{id}
参数：
  - id: 退费申请ID
注意：只能删除待审批状态的申请
```

---

## 数据库表结构

### fin_refund 表
```sql
CREATE TABLE fin_refund (
    id BIGINT NOT NULL COMMENT 'ID',
    refund_no VARCHAR(50) NOT NULL COMMENT '退费单号',
    contract_id BIGINT NOT NULL COMMENT '合同ID',
    student_id BIGINT NOT NULL COMMENT '学员ID',
    campus_id BIGINT COMMENT '校区ID',
    apply_amount DECIMAL(10,2) DEFAULT 0 COMMENT '申请退费金额',
    actual_amount DECIMAL(10,2) DEFAULT 0 COMMENT '实际退费金额',
    penalty_amount DECIMAL(10,2) DEFAULT 0 COMMENT '违约金',
    reason VARCHAR(200) COMMENT '退费原因',
    description VARCHAR(1000) COMMENT '退费说明',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态：pending-待审批，approved-已通过，rejected-已拒绝，refunded-已退款',
    apply_time DATETIME COMMENT '申请时间',
    approver_id BIGINT COMMENT '审批人ID',
    approve_time DATETIME COMMENT '审批时间',
    approve_remark VARCHAR(500) COMMENT '审批备注',
    refund_time DATETIME COMMENT '退款时间',
    refund_method VARCHAR(20) COMMENT '退款方式',
    refund_transaction_no VARCHAR(100) COMMENT '退款交易号',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    UNIQUE KEY uk_refund_no (refund_no),
    KEY idx_contract_id (contract_id),
    KEY idx_student_id (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退费记录表';
```

---

## 核心实现类

### 1. 实体类
**文件**: `/edu-finance/src/main/java/com/edu/finance/domain/entity/Refund.java`

**字段说明**:
- `refundNo`: 退费单号（自动生成）
- `contractId`: 合同ID
- `studentId`: 学员ID
- `campusId`: 校区ID
- `applyAmount`: 申请退费金额（自动计算）
- `actualAmount`: 实际退费金额（审批时填写）
- `penaltyAmount`: 违约金（自动计算）
- `reason`: 退费原因
- `description`: 退费说明
- `status`: 状态（pending/approved/rejected/refunded）
- `applyTime`: 申请时间
- `approverId`: 审批人ID
- `approveTime`: 审批时间
- `approveRemark`: 审批备注
- `refundTime`: 退款时间
- `refundMethod`: 退款方式
- `refundTransactionNo`: 退款交易号

### 2. DTO类

#### RefundApplyDTO
**文件**: `/edu-finance/src/main/java/com/edu/finance/domain/dto/RefundApplyDTO.java`

**字段**:
- `contractId`: 合同ID（必填）
- `reason`: 退费原因（必填）
- `description`: 退费说明（可选）

#### RefundApproveDTO
**文件**: `/edu-finance/src/main/java/com/edu/finance/domain/dto/RefundApproveDTO.java`

**字段**:
- `refundId`: 退费申请ID（必填）
- `approveResult`: 审批结果（必填，approved/rejected）
- `actualAmount`: 实际退费金额（审批通过时必填）
- `approveRemark`: 审批备注（可选）

#### RefundCalculationDTO
**文件**: `/edu-finance/src/main/java/com/edu/finance/domain/dto/RefundCalculationDTO.java`

**字段**:
- `contractAmount`: 合同总金额
- `paidAmount`: 已支付金额
- `totalHours`: 总课时数
- `usedHours`: 已消耗课时数
- `remainingHours`: 剩余课时数
- `pricePerHour`: 单课时价格
- `usedAmount`: 已消耗金额
- `penaltyAmount`: 违约金
- `penaltyRate`: 违约金比例（百分比）
- `refundableAmount`: 可退金额
- `calculationNote`: 计算说明

### 3. VO类
**文件**: `/edu-finance/src/main/java/com/edu/finance/domain/vo/RefundVO.java`

包含所有退费申请信息，包括关联的学员姓名、合同编号、校区名称、审批人姓名等。

### 4. Mapper
**文件**: `/edu-finance/src/main/java/com/edu/finance/mapper/RefundMapper.java`

**XML文件**: `/edu-finance/src/main/resources/mapper/finance/RefundMapper.xml`

**核心方法**:
- `selectRefundPage`: 分页查询退费申请列表，支持多条件查询，关联查询学员、合同、校区、审批人信息

### 5. Service接口
**文件**: `/edu-finance/src/main/java/com/edu/finance/service/RefundService.java`

**核心方法**:
- `pageList`: 分页查询退费申请列表
- `calculateRefundAmount`: 计算退费金额
- `applyRefund`: 提交退费申请
- `approveRefund`: 审批退费申请
- `executeRefund`: 执行退款
- `generateRefundNo`: 生成退费单号

### 6. Service实现
**文件**: `/edu-finance/src/main/java/com/edu/finance/service/impl/RefundServiceImpl.java`

**核心特性**:
- 使用 `@Transactional` 确保事务一致性
- 完整的业务逻辑验证
- 自动计算退费金额
- 状态流转控制
- 事件驱动（发布退费完成事件）

### 7. Controller
**文件**: `/edu-finance/src/main/java/com/edu/finance/controller/RefundController.java`

**特性**:
- 使用 Knife4j 注解提供完整的API文档
- 参数验证（使用 `@Validated`）
- 统一响应格式（使用 `R<T>`）
- RESTful API设计

---

## 事件驱动机制

### RefundCompletedEvent
**文件**: `/edu-finance/src/main/java/com/edu/finance/event/RefundCompletedEvent.java`

**触发时机**: 退款执行成功后

**包含信息**:
- 退费申请ID
- 合同ID
- 学员ID
- 校区ID
- 退费金额

### RefundCompletedListener
**文件**: `/edu-finance/src/main/java/com/edu/finance/listener/RefundCompletedListener.java`

**功能**: 监听退费完成事件，执行后续操作

**特性**:
- 使用 `@Async` 异步处理
- 不影响主流程
- 可扩展（发送通知、记录日志、更新统计等）

---

## 业务流程

### 退费申请流程
1. 学员或管理员选择要退费的合同
2. 调用计算接口，系统自动计算可退金额
3. 填写退费原因和说明
4. 提交退费申请
5. 系统生成退费单号，状态为"pending"（待审批）

### 退费审批流程
1. 管理员查看退费申请列表
2. 查看退费申请详情和计算结果
3. 决定是否通过
   - **通过**: 填写实际退费金额（可调整），填写审批备注
   - **拒绝**: 填写拒绝原因
4. 提交审批结果
5. 系统更新退费申请状态
   - 通过: 状态变为"approved"，合同状态变为"refunded"，冻结相关课时账户
   - 拒绝: 状态变为"rejected"

### 退款执行流程
1. 财务人员查看已通过审批的退费申请
2. 执行退款操作（对接支付系统）
3. 系统更新退费状态为"refunded"
4. 发布退费完成事件
5. 触发后续操作（发送通知、记录日志等）

---

## 状态说明

### 退费申请状态
- `pending`: 待审批
- `approved`: 已通过
- `rejected`: 已拒绝
- `refunded`: 已退款

### 合同状态变化
- 退费审批通过后，合同状态从 `signed` 或 `completed` 变为 `refunded`

### 课时账户状态变化
- 退费审批通过后，相关课时账户状态从 `active` 变为 `frozen`

---

## 技术要求完成情况

### ✅ 使用 MyBatis-Plus
- 继承 `ServiceImpl<RefundMapper, Refund>`
- 使用 `LambdaQueryWrapper` 进行条件查询
- 使用 `IPage` 进行分页查询

### ✅ 完整的 Knife4j API 文档
- 使用 `@Tag` 标注控制器
- 使用 `@Operation` 标注接口
- 使用 `@Parameter` 标注参数
- 使用 `@Schema` 标注DTO字段

### ✅ 事务管理
- 使用 `@Transactional(rollbackFor = Exception.class)` 确保事务一致性
- 在 `applyRefund`、`approveRefund`、`executeRefund` 方法中使用事务

### ✅ 状态流转控制
- 严格的状态验证
- 只有待审批状态才能审批
- 只有已通过审批才能执行退款
- 只有待审批状态才能删除

### ✅ 审批流程记录
- 记录审批人ID
- 记录审批时间
- 记录审批备注
- 记录实际退费金额

---

## 数据校验

### 退费申请校验
1. 合同必须存在
2. 合同状态必须是"已签署"或"已完成"
3. 同一合同不能有多个待审批的退费申请

### 退费审批校验
1. 退费申请必须存在
2. 退费申请状态必须是"待审批"
3. 审批结果必须是"approved"或"rejected"
4. 审批通过时，实际退费金额必填
5. 实际退费金额必须大于0
6. 实际退费金额不能大于申请金额

### 退款执行校验
1. 退费申请必须存在
2. 退费申请状态必须是"已通过"

---

## 测试建议

### 1. 单元测试
- 测试退费金额计算逻辑
- 测试退费单号生成逻辑
- 测试状态流转逻辑

### 2. 集成测试
- 测试完整的退费申请流程
- 测试完整的退费审批流程
- 测试完整的退款执行流程

### 3. 接口测试
使用 Knife4j 文档页面进行接口测试：
- 访问 `http://localhost:8080/doc.html`
- 找到"退费管理"模块
- 测试所有接口

### 4. 测试用例

#### 用例1：正常退费流程
1. 创建合同并支付
2. 消耗部分课时
3. 计算退费金额
4. 提交退费申请
5. 审批通过
6. 执行退款
7. 验证合同状态、课时账户状态

#### 用例2：退费申请被拒绝
1. 提交退费申请
2. 审批拒绝
3. 验证状态变化

#### 用例3：重复提交退费申请
1. 提交退费申请
2. 再次提交退费申请
3. 验证是否抛出异常

#### 用例4：退费金额计算
1. 测试不同课时消耗情况下的退费金额计算
2. 验证违约金计算是否正确
3. 验证可退金额是否正确

---

## API文档访问

启动应用后，访问以下地址查看完整的API文档：

```
http://localhost:8080/doc.html
```

在文档页面中找到"退费管理"模块，可以看到所有接口的详细说明和在线测试功能。

---

## 文件清单

### 核心业务文件
1. `/edu-finance/src/main/java/com/edu/finance/domain/entity/Refund.java` - 退费实体
2. `/edu-finance/src/main/java/com/edu/finance/domain/dto/RefundApplyDTO.java` - 退费申请DTO
3. `/edu-finance/src/main/java/com/edu/finance/domain/dto/RefundApproveDTO.java` - 退费审批DTO
4. `/edu-finance/src/main/java/com/edu/finance/domain/dto/RefundCalculationDTO.java` - 退费计算DTO
5. `/edu-finance/src/main/java/com/edu/finance/domain/vo/RefundVO.java` - 退费VO
6. `/edu-finance/src/main/java/com/edu/finance/mapper/RefundMapper.java` - Mapper接口
7. `/edu-finance/src/main/resources/mapper/finance/RefundMapper.xml` - Mapper XML
8. `/edu-finance/src/main/java/com/edu/finance/service/RefundService.java` - Service接口
9. `/edu-finance/src/main/java/com/edu/finance/service/impl/RefundServiceImpl.java` - Service实现
10. `/edu-finance/src/main/java/com/edu/finance/controller/RefundController.java` - Controller

### 事件相关文件
11. `/edu-finance/src/main/java/com/edu/finance/event/RefundCompletedEvent.java` - 退费完成事件
12. `/edu-finance/src/main/java/com/edu/finance/listener/RefundCompletedListener.java` - 事件监听器

### 数据库迁移文件
13. `/edu-admin/src/main/resources/db/migration/V1.0.3__finance_marketing_tables.sql` - 初始表结构
14. `/edu-admin/src/main/resources/db/migration/V1.0.17__update_refund_table.sql` - 表结构更新

### 文档文件
15. `/edu-finance/REFUND_README.md` - 退费模块使用文档
16. `/edu-finance/REFUND_IMPLEMENTATION_SUMMARY.md` - 本实现总结文档

---

## 总结

### 完成的功能
1. ✅ **任务18.5**: 实现退费申请接口
   - POST /finance/refund/apply
   - 自动计算退费金额
   - 生成退费单号
   - 状态为"待审批"

2. ✅ **任务18.6**: 实现退费金额计算逻辑
   - GET /finance/refund/calculate/{contractId}
   - 根据合同金额、已用课时、剩余课时计算
   - 扣除违约金（默认5%）
   - 详细的计算说明

3. ✅ **任务18.7**: 实现退费审批接口
   - POST /finance/refund/approve
   - 支持通过/拒绝
   - 记录审批人、审批时间、审批意见
   - 审批通过后更新合同状态
   - 冻结相关课时账户

### 技术亮点
1. **完整的业务逻辑**: 涵盖退费申请、金额计算、审批、退款全流程
2. **严格的数据校验**: 确保数据的完整性和一致性
3. **事务管理**: 使用Spring事务确保数据一致性
4. **事件驱动**: 使用Spring事件机制实现解耦
5. **完整的API文档**: 使用Knife4j提供在线API文档和测试
6. **状态流转控制**: 严格的状态验证和流转
7. **灵活的计算逻辑**: 支持配置违约金比例
8. **关联查询优化**: 一次查询获取所有关联信息

### 扩展建议
1. 对接支付系统，实现自动退款
2. 发送退费通知给学员和管理员
3. 支持部分退费（退部分课时）
4. 支持退费规则配置（违约金比例、退费条件等）
5. 退费统计报表
6. 退费审批流程配置（多级审批）
7. 增加退费原因分类和统计
8. 增加退费时效控制（如签约后多少天内可以全额退费）

---

## 联系方式

如有问题或建议，请联系开发团队。

---

**文档版本**: 1.0
**创建日期**: 2026-01-31
**最后更新**: 2026-01-31
