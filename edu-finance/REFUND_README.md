# 退费管理模块

## 功能概述

退费管理模块实现了完整的退费申请、审批和退款流程，包括：

1. **退费申请（任务 18.5）**：学员或管理员可以提交退费申请
2. **退费金额计算（任务 18.6）**：自动计算退费金额，包括已消课时、违约金等
3. **退费审批（任务 18.7）**：管理员审批退费申请，支持通过/拒绝

## 核心文件

### 实体类
- `/edu-finance/src/main/java/com/edu/finance/domain/entity/Refund.java`
  - 退费申请实体，对应 `fin_refund` 表

### DTO
- `/edu-finance/src/main/java/com/edu/finance/domain/dto/RefundApplyDTO.java`
  - 退费申请DTO
- `/edu-finance/src/main/java/com/edu/finance/domain/dto/RefundApproveDTO.java`
  - 退费审批DTO
- `/edu-finance/src/main/java/com/edu/finance/domain/dto/RefundCalculationDTO.java`
  - 退费金额计算结果DTO

### VO
- `/edu-finance/src/main/java/com/edu/finance/domain/vo/RefundVO.java`
  - 退费申请视图对象

### Mapper
- `/edu-finance/src/main/java/com/edu/finance/mapper/RefundMapper.java`
  - MyBatis Mapper接口
- `/edu-finance/src/main/resources/mapper/finance/RefundMapper.xml`
  - MyBatis XML映射文件

### Service
- `/edu-finance/src/main/java/com/edu/finance/service/RefundService.java`
  - 退费服务接口
- `/edu-finance/src/main/java/com/edu/finance/service/impl/RefundServiceImpl.java`
  - 退费服务实现

### Controller
- `/edu-finance/src/main/java/com/edu/finance/controller/RefundController.java`
  - 退费管理控制器

### Event
- `/edu-finance/src/main/java/com/edu/finance/event/RefundCompletedEvent.java`
  - 退费完成事件
- `/edu-finance/src/main/java/com/edu/finance/listener/RefundCompletedListener.java`
  - 退费完成事件监听器

### 数据库迁移
- `/edu-admin/src/main/resources/db/migration/V1.0.17__update_refund_table.sql`
  - 更新退费表结构的迁移脚本

## API 接口

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
返回：
  - 退费金额计算结果，包括：
    - 合同总金额
    - 已支付金额
    - 总课时数
    - 已消耗课时数
    - 剩余课时数
    - 单课时价格
    - 已消耗金额
    - 违约金
    - 可退金额
    - 计算说明
```

### 4. 提交退费申请
```
POST /finance/refund/apply
请求体：
{
  "contractId": 合同ID,
  "reason": "退费原因",
  "description": "退费说明"
}
返回：退费申请ID
```

### 5. 审批退费申请
```
POST /finance/refund/approve
请求体：
{
  "refundId": 退费申请ID,
  "approveResult": "approved/rejected",
  "actualAmount": 实际退费金额（通过时必填）,
  "approveRemark": "审批备注"
}
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

## 退费金额计算规则

### 计算公式
```
可退金额 = 已支付金额 - 已消耗金额 - 违约金

其中：
- 单课时价格 = 已支付金额 / 总课时数
- 已消耗金额 = 单课时价格 × 已消耗课时数
- 违约金 = (已支付金额 - 已消耗金额) × 违约金比例（默认5%）
```

### 示例
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

## 业务流程

### 退费申请流程
1. 学员或管理员选择要退费的合同
2. 系统自动计算可退金额
3. 填写退费原因和说明
4. 提交退费申请
5. 系统生成退费单号，状态为"待审批"

### 退费审批流程
1. 管理员查看退费申请列表
2. 查看退费申请详情和计算结果
3. 决定是否通过
   - **通过**：填写实际退费金额（可调整），填写审批备注
   - **拒绝**：填写拒绝原因
4. 提交审批结果
5. 系统更新退费申请状态
   - 通过：状态变为"已通过"，合同状态变为"已退费"，冻结相关课时账户
   - 拒绝：状态变为"已拒绝"

### 退款执行流程
1. 财务人员查看已通过审批的退费申请
2. 执行退款操作（对接支付系统）
3. 系统更新退费状态为"已退款"
4. 发布退费完成事件
5. 触发后续操作：
   - 发送通知给学员
   - 发送通知给相关管理员
   - 记录操作日志
   - 更新统计数据

## 状态说明

### 退费申请状态
- `pending`：待审批
- `approved`：已通过
- `rejected`：已拒绝
- `refunded`：已退款

### 合同状态变化
- 退费审批通过后，合同状态从 `signed` 或 `completed` 变为 `refunded`

### 课时账户状态变化
- 退费审批通过后，相关课时账户状态从 `active` 变为 `frozen`

## 事务管理

以下操作使用了事务管理，确保数据一致性：

1. **提交退费申请**：
   - 创建退费申请记录

2. **审批退费申请**：
   - 更新退费申请状态
   - 更新合同状态（通过时）
   - 冻结课时账户（通过时）

3. **执行退款**：
   - 更新退费状态
   - 发布退费完成事件

## 事件驱动

### RefundCompletedEvent
退费完成后发布此事件，包含以下信息：
- 退费申请ID
- 合同ID
- 学员ID
- 校区ID
- 退费金额

### RefundCompletedListener
监听退费完成事件，执行以下操作：
- 记录日志
- 发送通知（待实现）
- 更新统计数据（待实现）

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
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态',
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

## 使用示例

### 1. 计算退费金额
```bash
curl -X GET "http://localhost:8080/finance/refund/calculate/1001"
```

### 2. 提交退费申请
```bash
curl -X POST "http://localhost:8080/finance/refund/apply" \
  -H "Content-Type: application/json" \
  -d '{
    "contractId": 1001,
    "reason": "学员搬家，无法继续上课",
    "description": "学员因工作调动需要搬迁至外地，无法继续在本校区上课"
  }'
```

### 3. 审批退费申请（通过）
```bash
curl -X POST "http://localhost:8080/finance/refund/approve" \
  -H "Content-Type: application/json" \
  -d '{
    "refundId": 2001,
    "approveResult": "approved",
    "actualAmount": 6650.00,
    "approveRemark": "同意退费申请"
  }'
```

### 4. 审批退费申请（拒绝）
```bash
curl -X POST "http://localhost:8080/finance/refund/approve" \
  -H "Content-Type: application/json" \
  -d '{
    "refundId": 2001,
    "approveResult": "rejected",
    "approveRemark": "不符合退费条件"
  }'
```

### 5. 执行退款
```bash
curl -X POST "http://localhost:8080/finance/refund/2001/execute"
```

## 注意事项

1. **权限控制**：
   - 退费申请：学员和管理员都可以提交
   - 退费审批：只有管理员可以审批
   - 执行退款：只有财务人员可以执行

2. **数据校验**：
   - 只有已签署或已完成的合同才能申请退费
   - 同一合同不能有多个待审批的退费申请
   - 实际退费金额不能大于申请金额
   - 只有待审批状态的申请才能审批
   - 只有已通过审批的申请才能执行退款

3. **违约金计算**：
   - 默认违约金比例为5%
   - 可以根据合同条款调整违约金比例
   - 违约金基于剩余金额计算

4. **课时账户处理**：
   - 退费审批通过后，相关课时账户会被冻结
   - 冻结后的课时账户不能再消耗课时
   - 如需恢复，需要手动解冻

5. **事件处理**：
   - 退费完成事件是异步处理的
   - 事件监听器中的操作不会影响主流程
   - 可以在监听器中添加更多业务逻辑

## 扩展功能

### 待实现功能
1. 对接支付系统，实现自动退款
2. 发送退费通知给学员和管理员
3. 支持部分退费（退部分课时）
4. 支持退费规则配置（违约金比例、退费条件等）
5. 退费统计报表
6. 退费审批流程配置（多级审批）

### 可优化点
1. 退费金额计算规则可以更灵活，支持多种计算方式
2. 支持批量退费
3. 支持退费预审（提前计算退费金额）
4. 增加退费原因分类和统计
5. 增加退费时效控制（如签约后多少天内可以全额退费）
